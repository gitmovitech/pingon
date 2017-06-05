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
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cl.pingon.Libraries.RESTService;
import cl.pingon.Model.ModelDocumentos;
import cl.pingon.Model.ModelRegistros;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;
import cl.pingon.Sync.SyncDocumentos;
import cl.pingon.Sync.SyncDocumentosRegistros;
import cl.pingon.Sync.SyncRegistros;

public class SyncService extends Service {

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

    Thread thread;

    public SyncService() {

    }

    @Override
    public void onCreate() {

        thread = new Thread() {
            public void run() {

                UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
                UploadService.HTTP_STACK = new OkHttpStack();

                context = getApplicationContext();

                RollbackRegisteredIds = new ArrayList<>();
                RollbackDocIdInserted = 0;

                REST = new RESTService(getApplicationContext());

                session = getSharedPreferences("session", context.MODE_PRIVATE);
                ARN_ID = Integer.parseInt(session.getString("arn_id", ""));

                Documento = new TblDocumentoHelper(context);
                Formularios = new TblFormulariosHelper(context);

                new Timer().scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run(){
                        if(Processing == 0) {

                            Processing = 1;

                            //Notificacion por pantalla de proceso
                            builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.sync)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pingon))
                                    .setContentTitle("Sincronizando")
                                    .setContentText("Cargando documentos y registros");
                            builder.setProgress(0,0, true);
                            startForeground(1, builder.build());

                            //Obtener documentos de base de datos y guardar en arraylist
                            ArrayList<ModelDocumentos> Documentos = getAllSyncDocumentos();
                            ArrayList<ModelRegistros> Registros = new ArrayList<>();
                            if(Documentos.size() > 0){
                                for(int x = 0; x < Documentos.size(); x++){
                                    Registros = getAllSyncRegistros(Registros, Documentos.get(x).getID());
                                }
                                if(Registros.size() > 0) {
                                    subirDocumentosRegistros(Documentos, Registros);
                                } else {
                                    Processing = 0;
                                    stopForeground(true);
                                }
                            } else {
                                Processing = 0;
                                stopForeground(true);
                            }
                        }
                    }
                } ,0 ,60000);
            }
        };
        thread.start();

    }

    private void subirDocumentosRegistros(ArrayList<ModelDocumentos> Documentos, ArrayList<ModelRegistros> Registros){

        Integer local_doc_id;
        JSONObject JSONRegistros;
        JSONArray JSONArrayRegistros;
        JSONObject JSONDocumentos;
        JSONArray JSONArrayDocumentos = new JSONArray();

        for(int d = 0; d < Documentos.size(); d++){
            local_doc_id = Documentos.get(d).getID();
            JSONArrayRegistros = new JSONArray();
            for(int r = 0; r < Registros.size(); r++){
                if(Registros.get(r).getLOCAL_DOC_ID().equals(local_doc_id)) {
                    JSONRegistros = new JSONObject();
                    try {
                        JSONRegistros.put(TblRegistroDefinition.Entry.REG_ID, Registros.get(r).getREG_ID());
                        JSONRegistros.put(TblRegistroDefinition.Entry.LOCAL_DOC_ID, Registros.get(r).getLOCAL_DOC_ID());
                        JSONRegistros.put(TblRegistroDefinition.Entry.CAM_ID, Registros.get(r).getCAM_ID());
                        JSONRegistros.put(TblRegistroDefinition.Entry.FRM_ID, Registros.get(r).getFRM_ID());
                        JSONRegistros.put(TblRegistroDefinition.Entry.CHK_ID, Registros.get(r).getCHK_ID());
                        JSONRegistros.put(TblRegistroDefinition.Entry.REG_VALOR, Registros.get(r).getREG_VALOR());
                        JSONArrayRegistros.put(JSONRegistros);
                    } catch (JSONException e) {
                        Processing = 0;
                        stopForeground(true);
                    }
                }
            }

            JSONDocumentos = new JSONObject();
            try {
                JSONDocumentos.put(TblDocumentoDefinition.Entry.ID, local_doc_id);
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO, Documentos.get(d).getDOC_EXT_EQUIPO());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.USU_ID, Documentos.get(d).getUSU_ID());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.FRM_ID, Documentos.get(d).getFRM_ID());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE, Documentos.get(d).getDOC_EXT_ID_CLIENTE());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO, Documentos.get(d).getDOC_EXT_ID_PROYECTO());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO, Documentos.get(d).getDOC_EXT_MARCA_EQUIPO());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE, Documentos.get(d).getDOC_EXT_NOMBRE_CLIENTE());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE, Documentos.get(d).getDOC_EXT_NUMERO_SERIE());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_EXT_OBRA, Documentos.get(d).getDOC_EXT_OBRA());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION, Documentos.get(d).getDOC_FECHA_CREACION());
                JSONDocumentos.put(TblDocumentoDefinition.Entry.DOC_FECHA_MODIFICACION, Documentos.get(d).getDOC_FECHA_MODIFICACION());
                JSONDocumentos.put("REGISTROS", JSONArrayRegistros);
                JSONArrayDocumentos.put(JSONDocumentos);
            } catch(JSONException e){
                Processing = 0;
                stopForeground(true);
            }
        }

        if(detectInternet()){

            String url_documentos = getResources().getString(R.string.url_sync_upload_data);
            SyncDocumentosRegistros DocumentosRegistros = new SyncDocumentosRegistros(context, url_documentos);
            DocumentosRegistros.addToken(session.getString("token", ""));
            DocumentosRegistros.addData(JSONArrayDocumentos);
            DocumentosRegistros.post(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        if(response.getString("ok").contains("1")){
                            //Todo guardar respuesta en base de datos y cambiar status
                        }
                    } catch (JSONException e){
                        Processing = 0;
                        stopForeground(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Processing = 0;
                    stopForeground(true);
                }
            });
        } else {
            Processing = 0;
            stopForeground(true);
        }

    }

    /**
     * Obtiene registros asociados a los local_id y se guardan en un arraylist
     * @param local_doc_id
     * @return
     */
    private ArrayList<ModelRegistros> getAllSyncRegistros(ArrayList<ModelRegistros> ArrayRegistros, Integer local_doc_id){
        TblRegistroHelper Registros = new TblRegistroHelper(context);
        Cursor c = Registros.getByLocalDocId(local_doc_id, "SYNC");
        ModelRegistros Item;

        if(c.getCount() > 0){
            while(c.moveToNext()){
                Item = new ModelRegistros();
                Item.setREG_ID(c.getInt(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.ID)));
                Item.setLOCAL_DOC_ID(c.getInt(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.LOCAL_DOC_ID)));
                Item.setCAM_ID(c.getInt(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID)));
                Item.setFRM_ID(c.getInt(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.FRM_ID)));
                Item.setCHK_ID(c.getInt(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CHK_ID)));
                Item.setREG_TIPO(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)));
                Item.setREG_VALOR(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
                Item.setSEND_STATUS(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.SEND_STATUS)));
                ArrayRegistros.add(Item);
            }
        } else {
            Processing = 0;
        }
        c.close();

        return ArrayRegistros;
    }

    /**
     * Obtiene documentos de la base de datos por sincronizar y los guarda en un arraylist
     * @return
     */
    private ArrayList<ModelDocumentos> getAllSyncDocumentos(){
        TblDocumentoHelper Documentos = new TblDocumentoHelper(context);
        Cursor c = Documentos.getAllSync();
        ArrayList<ModelDocumentos> ArrayDocumentos = new ArrayList<>();
        ModelDocumentos Item;

        if(c.getCount() > 0){
            while (c.moveToNext()){
                Item = new ModelDocumentos();
                Item.setID(c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)));
                Item.setUSU_ID(c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.USU_ID)));
                Item.setFRM_ID(c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID)));
                Item.setDOC_FECHA_CREACION(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION)));
                Item.setDOC_FECHA_MODIFICACION(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_MODIFICACION)));
                Item.setDOC_EXT_EQUIPO(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO)));
                Item.setDOC_EXT_MARCA_EQUIPO(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO)));
                Item.setDOC_EXT_NUMERO_SERIE(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE)));
                Item.setDOC_EXT_NOMBRE_CLIENTE(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE)));
                Item.setDOC_EXT_OBRA(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA)));
                Item.setDOC_EXT_ID_CLIENTE(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE)));
                Item.setDOC_EXT_ID_PROYECTO(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO)));
                ArrayDocumentos.add(Item);
            }
        } else {
            Processing = 0;
        }
        c.close();

        return ArrayDocumentos;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {}


    /**
     * Recopilación de registros para sincronizar con el servidor
     * @param DOC_ID
     * @param local_doc_id
     */
    private void subirRegistros(Integer DOC_ID, Integer local_doc_id){

        if(detectInternet()) {

            String url_registros = getResources().getString(R.string.url_sync_registros);
            SyncRegistros sync_registros = new SyncRegistros(getApplicationContext(), url_registros, local_doc_id, DOC_ID);
            sync_registros.addToken(session.getString("token", ""));

            TblRegistroHelper Registros = new TblRegistroHelper(getApplicationContext());
            final Cursor cr = Registros.getSyncByLocalDocId(local_doc_id);
            final Integer contador = 0;
            if (cr.getCount() > 0) {
                builder.setProgress(cr.getCount(), contador, false);
                startForeground(1, builder.build());
                subirRegistro(cr, sync_registros, 0, DOC_ID, local_doc_id);
            } else {
                stopForeground(true);
                cr.close();
                Processing = 0;
            }

        } else {
            stopForeground(true);
            Processing = 0;
            //Todo rollback
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
                    uploadMultipart(getResources().getString(R.string.url_sync_upload_file),cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)), new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            registroPosition++;
                            builder.setProgress(cr.getCount(), registroPosition, false);
                            startForeground(1, builder.build());
                            subirRegistro(cr, sync_registros, registroPosition, DOC_ID, local_doc_id);
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    }, DOC_ID);

                } else if(cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).contains("video")){
                    uploadMultipart(getResources().getString(R.string.url_sync_upload_file), cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)), new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            registroPosition++;
                            builder.setProgress(cr.getCount(), registroPosition, false);
                            startForeground(1, builder.build());
                            subirRegistro(cr, sync_registros, registroPosition, DOC_ID, local_doc_id);
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    }, DOC_ID);
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
            //Todo, enviar PDF
            //RollbackDataSent();
            //setSentDocumentAndRegisters(local_doc_id);
            Processing = 0;
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


    /**
     * Deshace inserciones cuando un error ocurre
     */
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


    /**
     * Carga de archivos al servidor
     * @param url
     * @param filepath
     * @param UploadStatusDelegate
     */
    private void uploadMultipart(String url, String filepath, UploadStatusDelegate UploadStatusDelegate, Integer doc_id) {
        UploadNotificationConfig upconfig = new UploadNotificationConfig();
        upconfig.setTitle(getResources().getString(R.string.loader_files));
        upconfig.setAutoClearOnSuccess(true);
        try {
            MultipartUploadRequest fup = new MultipartUploadRequest(context, url);
            fup.addParameter("doc_id", String.valueOf(doc_id));
            fup.addFileToUpload(filepath, "file");
            fup.addParameter("token", session.getString("token", ""));
            fup.setNotificationConfig(upconfig);
            fup.setMaxRetries(5);
            fup.setDelegate(UploadStatusDelegate);
            fup.startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
            //Todo rollback
            Processing = 0;
            stopForeground(true);
        }
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
            Processing = 0;
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
