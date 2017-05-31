package cl.pingon;

import android.app.Service;
import android.content.ContentValues;
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

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.FileTransfer;
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
    Context context;

    ArrayList<Integer> RollbackRegisteredIds;
    Integer RollbackDocIdInserted;

    TblDocumentoHelper Documento;
    TblFormulariosHelper Formularios;

    public SyncService() {

    }

    @Override
    public void onCreate() {

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();

        RollbackRegisteredIds = new ArrayList<>();
        RollbackDocIdInserted = 0;

        REST = new RESTService(getApplicationContext());

        session = getSharedPreferences("session", this.MODE_PRIVATE);
        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));

        context = this;

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

    private void uploadMultipart(String url, String filepath, String filename) {
        try {
            new MultipartUploadRequest(context, url)
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(filepath, filename)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(5)
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
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

            if(detectInternet()){
                while(c.moveToNext()){
                    startSync(c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)));
                }
            } else {
                Processing = 0;
            }
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
                        RollbackDocIdInserted = DOC_ID;
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
            subirRegistro(cr, sync_registros, 0, DOC_ID, local_doc_id);
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
    private void subirRegistro(final Cursor cr, final SyncRegistros sync_registros, Integer index, final Integer DOC_ID, final Integer local_doc_id){

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
                    Log.d("UPLOADING", "IMAGE");
                    uploadImage(cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)), cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
                } else {
                    params.put(TblRegistroDefinition.Entry.REG_VALOR, cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));

                    REST.post(getResources().getString(R.string.url_sync_registros), params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(response.getString("ok").contains("1")){
                                    RollbackRegisteredIds.add(response.getJSONObject("response").getInt("id"));

                                    registroPosition++;
                                    builder.setProgress(cr.getCount(), registroPosition, false);
                                    startForeground(1, builder.build());
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    subirRegistro(cr, sync_registros, registroPosition, DOC_ID, local_doc_id);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            //RollbackDataSent();
            setSentDocumentAndRegisters(local_doc_id);
            cr.close();
            stopForeground(true);
        }

    }

    /**
     * SETEA DOCUMENTO COMO ENVIADO EN BASE DE DATOS LOCAL Y MODIFICA DOC_ID CON DATO DESDE EL SERVIDOR
     * @param local_doc_id
     */
    private void setSentDocumentAndRegisters(Integer local_doc_id){
        TblDocumentoHelper Documento = new TblDocumentoHelper(getApplicationContext());
        ContentValues values = new ContentValues();
        values.put(TblDocumentoDefinition.Entry.SEND_STATUS, "SENT");
        values.put(TblDocumentoDefinition.Entry.DOC_ID, RollbackDocIdInserted);
        Documento.update(local_doc_id, values);
    }

    private void RollbackDataSent(){
        Log.d("DOC_ID CREADO", ":"+ RollbackDocIdInserted);
        Log.d("REGISTROS CREADOS", RollbackRegisteredIds.toString());
        if(RollbackDocIdInserted > 0){
            if(RollbackRegisteredIds.size() > 0){
                JSONObject registros = new JSONObject();
                try {
                    registros.put("doc_id", RollbackDocIdInserted);
                    registros.put("ids", RollbackRegisteredIds.toString());

                    REST.post(getResources().getString(R.string.url_rollback_data_sent), registros, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Si esta OK, no hay nada que hacer, solo estar feliz porque la wea funcionó xD
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //SI hay error en el rollback nada se puede hacer por ahora (quizas otro procedimiento de sincronizacion en el futuro)
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
        uploadMultipart(getResources().getString(R.string.url_sync_upload_file), path, "test.jpg");
    }

    /**
     * DETECCIÓN DE CONEXIÓN A INTERNET
     * @return
     */
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
