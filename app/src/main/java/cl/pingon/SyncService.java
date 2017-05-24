package cl.pingon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;
import cl.pingon.Sync.SyncDocumentos;
import cl.pingon.Sync.SyncRegistros;

public class SyncService extends Service {

    TblDocumentoHelper Documentos;
    Integer Processing = 0;
    SharedPreferences session;
    NotificationCompat.Builder builder;
    Integer ARN_ID;
    Integer FMR_ID;
    RESTService REST;
    String titulo;
    String subtitulo;

    TblDocumentoHelper Documento;
    TblFormulariosHelper Formularios;

    public SyncService() {

    }

    @Override
    public void onCreate() {

        REST = new RESTService(getApplicationContext());

        session = getSharedPreferences("session", this.MODE_PRIVATE);
        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));

        Documento = new TblDocumentoHelper(this);
        Formularios = new TblFormulariosHelper(this);

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                if(Processing == 0){
                    Sync();
                }
            }
        },0,60000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {}

    /**
     * Proceso de sincronizacion iniciado
     */
    private void Sync(){
        Processing = 1;
        Documentos = new TblDocumentoHelper(this);
        Cursor c = Documentos.getAllSync();
        if(c.getCount() > 0){

            builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.sync)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon))
                    .setContentTitle("Sincronización")
                    .setContentText("Preparando información para enviar");
            builder.setProgress(0,0, true);
            startForeground(1, builder.build());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //if(detectInternet()){
                while(c.moveToNext()){
                    startSync(c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)));
                }
            /*} else {
                Processing = 0;
            }*/
        } else {
            Processing = 0;
        }
        c.close();
    }

    /**
     * Sincronizacion por cada documento encontrado en la base de datos
     * @param local_doc_id
     */
    private void startSync(Integer local_doc_id){
        Cursor cd = Documento.getById(local_doc_id);
        cd.moveToFirst();
        FMR_ID = cd.getInt(cd.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));

        Cursor cf = Formularios.getByArnIdFrmId(ARN_ID, FMR_ID);
        cf.moveToFirst();

        titulo = cf.getString(cf.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
        titulo += " / ";
        titulo += cf.getString(cf.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));

        subtitulo = cd.getString(cd.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
        subtitulo += " / ";
        subtitulo +=  cd.getString(cd.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));

        cd.close();
        cf.close();


        builder.setContentTitle(titulo);
        builder.setContentText(subtitulo);
        startForeground(1, builder.build());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        subirDocumento(local_doc_id);

    }

    /**
     * Subida al servidor de un documento
     * @param local_doc_id
     */
    private void subirDocumento(final Integer local_doc_id){

        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
        String url_documentos = getResources().getString(R.string.url_sync_documentos);
        SyncDocumentos sync_documentos = new SyncDocumentos(this, url_documentos, local_doc_id);
        sync_documentos.addToken(session.getString("token", ""));
        sync_documentos.AddData(Documentos.getById(local_doc_id));
        sync_documentos.Post(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("ok").contains("1")) {
                        JSONObject JSONResponse = response.getJSONObject("response");
                        final Integer DOC_ID = JSONResponse.getInt("id");
                        subirRegistros(DOC_ID, local_doc_id);
                    }
                } catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR VOLLEY", error.toString());
                stopForeground(true);
                Processing = 0;
            }
        });
    }

    /**
     * Recopilación de registros para sincronizar con el servidor
     * @param DOC_ID
     * @param local_doc_id
     */
    private void subirRegistros(Integer DOC_ID, Integer local_doc_id){

        String url_registros = getResources().getString(R.string.url_sync_registros);
        SyncRegistros sync_registros = new SyncRegistros(getApplicationContext(), url_registros, local_doc_id, DOC_ID);
        sync_registros.addToken(session.getString("token", ""));

        TblRegistroHelper Registros = new TblRegistroHelper(getApplicationContext());
        final Cursor cr = Registros.getSyncByLocalDocId(local_doc_id);
        final Integer contador = 0;
        if(cr.getCount() > 0){
            builder.setProgress(cr.getCount(),contador, false);
            startForeground(1, builder.build());
            subirRegistro(cr, sync_registros, 0, DOC_ID);
        } else {
            stopForeground(true);
            cr.close();
        }

    }

    /**
     * Subida al servidor de cada registro de forma sincrona
     * @param cr
     * @param sync_registros
     */
    Integer registroPosition = 0;
    private void subirRegistro(final Cursor cr, final SyncRegistros sync_registros, Integer index, final Integer DOC_ID){

        registroPosition = index;
        if(registroPosition < cr.getCount()){
            cr.moveToPosition(registroPosition);

            JSONObject params = new JSONObject();
            try{
                params.put("token", session.getString("token", ""));
                params.put(TblRegistroDefinition.Entry.CAM_ID, cr.getInt(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID)));
                params.put(TblRegistroDefinition.Entry.DOC_ID, DOC_ID);
                params.put(TblRegistroDefinition.Entry.FRM_ID, cr.getInt(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.FRM_ID)));
                params.put(TblRegistroDefinition.Entry.REG_TIPO, cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)));
                if(cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).contains("foto")){
                    uploadImage(cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)), cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
                } else {
                    params.put(TblRegistroDefinition.Entry.REG_VALOR, cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
                    //ENVIO REGISTRO AL SERVIDOR
                    REST.post(getResources().getString(R.string.url_sync_registros), params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("REGISTRO RESPONSE", ":" + response.toString());
                            //Todo validar insercion antes de continuar enviando informacion

                            registroPosition++;
                            builder.setProgress(cr.getCount(), registroPosition, false);
                            startForeground(1, builder.build());
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            subirRegistro(cr, sync_registros, registroPosition, DOC_ID);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            cr.close();
                            stopForeground(true);
                        }
                    });
                }

            } catch (Exception e){
                cr.close();
                stopForeground(true);
            }


        } else {
            cr.close();
            stopForeground(true);
        }


        /*while(cr.moveToNext()){

            sync_registros.addData(cr);
            sync_registros.Post(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    builder.setProgress(cr.getCount(),contador, false);
                    startForeground(1, builder.build());
                    contador++;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO trabajar en respuesta de registro
                }
            });


            //sync_informes.SyncData(cr);

            Log.d("SYNCING", ":"+cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.ID)));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        //stopForeground(true);
    }

    private String imagefileToBase64(String path){
        DrawSign sign = new DrawSign();
        return sign.base64FromFile(path);
    }

    private void uploadImage(String name, String path){
        String base64file = imagefileToBase64(path);
        String[] namearr = name.split("/");
        builder.setContentText("Subiendo imagen \""+namearr[namearr.length-1]+"\" ("+Math.round(base64file.length()/1024)+" KB).");
        builder.setProgress(0, 0, true);
        startForeground(1, builder.build());
        //Todo subir archivo
    }

    private boolean detectInternet(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            return activeNetwork.isConnected();
        } else{
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
