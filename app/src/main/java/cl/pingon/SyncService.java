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
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telecom.Call;
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

import cl.pingon.Libraries.ErrorReport;
import cl.pingon.Libraries.RESTService;
import cl.pingon.Model.ModelDocumentos;
import cl.pingon.Model.ModelRegistros;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;
import cl.pingon.Sync.SyncDocumentosRegistros;
import cl.pingon.Sync.SyncRegistros;

public class SyncService extends Service {

    Integer Processing = 0;
    SharedPreferences session;
    NotificationCompat.Builder builder;
    Integer ARN_ID;
    String ARN_NOMBRE;
    RESTService REST;
    Context context;

    ErrorReport ErrorReport;

    ArrayList<Integer> RollbackRegisteredIds;
    Integer RollbackDocIdInserted;

    TblDocumentoHelper Documento;
    TblFormulariosHelper Formularios;

    JSONArray JSONDocumentos;

    String url_documentos;

    Thread thread;

    public SyncService() {

    }

    @Override
    public void onCreate() {

        ErrorReport = new ErrorReport(getApplicationContext());

        url_documentos = getResources().getString(R.string.url_sync_documentos);

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

                try {

                    Log.d("PINGON SERVICE", "EJECUTANDO");
                    Documento = new TblDocumentoHelper(context);
                    Formularios = new TblFormulariosHelper(context);

                    Cursor cursor = Formularios.getByArnId(ARN_ID);
                    cursor.moveToFirst();
                    ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                    cursor.close();

                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (Processing == 0) {

                                Processing = 1;

                                //Notificacion por pantalla de proceso
                                builder = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.sync)
                                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pingon_icon))
                                        .setContentTitle("Sincronizando")
                                        .setContentText("Cargando documentos y registros");
                                builder.setProgress(0, 0, true);


                                JSONDocumentos = new JSONArray();

                                prepararDocumentos(new Callback() {
                                    @Override
                                    public void success() {
                                        subirArchivosDocumento(0, new Callback() {
                                            @Override
                                            public void success() {
                                                Processing = 0;
                                                stopForeground(true);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }, 0, 60000);
                } catch (Exception e){

                }
            }
        };
        thread.start();

    }

    private void subirPDF(JSONObject documento, final Callback cb){
        String pdfPath = Environment.getExternalStorageDirectory() + "/Pingon/pdfs/";
        String LOCAL_DOC_ID = "";
        String DOC_ID = "";
        /*String NOMBRE_CLIENTE = "";
        String NOMBRE_OBRA = "";
        String NOMBRE_EQUIPO = "";*/
        try{
            DOC_ID = documento.get("DOC_ID").toString();
            LOCAL_DOC_ID = documento.get("LOCAL_DOC_ID").toString();
            /*NOMBRE_CLIENTE = documento.get("DOC_EXT_NOMBRE_CLIENTE").toString();
            NOMBRE_OBRA = documento.get("DOC_EXT_OBRA").toString();
            NOMBRE_EQUIPO = documento.get("DOC_EXT_EQUIPO").toString();*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String filename = LOCAL_DOC_ID+".pdf";

        uploadMultipart(
                getResources().getString(R.string.url_sync_upload_file),
                pdfPath + filename,
                new UploadStatusDelegate() {
                    @Override
                    public void onProgress(Context context, UploadInfo uploadInfo) {

                    }

                    @Override
                    public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                        Processing = 0;
                        ErrorReport.Send(session.getString("first_name", "")+" "+session.getString("last_name", ""), exception.getStackTrace().toString());
                        stopForeground(true);
                    }

                    @Override
                    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                        cb.success();
                    }

                    @Override
                    public void onCancelled(Context context, UploadInfo uploadInfo) {

                    }
                },
                Integer.parseInt(DOC_ID)
        );
    }

    private void subirArchivosDocumento(final int i, final Callback cb){
        try{
            final JSONObject documento = (JSONObject) JSONDocumentos.get(i);
            if(detectInternet()) {
                documento.put("token", session.getString("token", ""));
                REST.post(url_documentos, documento, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject registros_insertados) {
                        try {
                            if(registros_insertados.getInt("ok") == 1){
                                int DOC_ID_INSERTED = registros_insertados.getJSONObject("response").getInt("id");
                                JSONArray CAMPOS = registros_insertados.getJSONObject("response").getJSONArray("CAMPOS");
                                ((JSONObject) JSONDocumentos.get(i)).put("CAMPOS", CAMPOS);
                                documento.put("CAMPOS", CAMPOS);
                                documento.put("DOC_ID", DOC_ID_INSERTED);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        subirPDF(documento, new Callback(){
                                            @Override
                                            public void success() {
                                                super.success();

                                                subirArchivosRegistro(documento, 0, new Callback(){
                                                    @Override
                                                    public void success(){
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    cambiarStatusEnviado(documento.getInt("LOCAL_DOC_ID"));
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }).start();
                                                        subirArchivosDocumento(i+1, cb);
                                                    }
                                                });

                                            }
                                        });
                                    }
                                }).start();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR SUBIR DOCS", error.toString());
                        ErrorReport.Send(session.getString("first_name", "")+" "+session.getString("last_name", ""), error.getStackTrace().toString());
                        Processing = 0;
                        stopForeground(true);
                    }
                });
            } else {
                Log.d("SIN CONEXION", "INTERNET");
                Processing = 0;
                stopForeground(true);
            }

        } catch (Exception e){
            cb.success();
        }
    }

    /**
     * CAMBIA EL STATUS DEL DOCUMENTO A ENVIADO PARA NO VOLVER A PROCESARLO EN EL SINCRONIZADOR
     * @param LOCAL_DOC_ID
     */
    private void cambiarStatusEnviado(Integer LOCAL_DOC_ID){
        TblDocumentoHelper Documento = new TblDocumentoHelper(getApplicationContext());
        ContentValues values = new ContentValues();
        values.put(TblDocumentoDefinition.Entry.SEND_STATUS, "SENT");//todo PARA TEST CAMBIAR A SYNC
        Documento.update(LOCAL_DOC_ID, values);
        Documento.close();
    }


    private void subirArchivosRegistro(Object elementos, int c, final Callback cb){

        JSONObject items = (JSONObject) elementos;
        try {
            JSONArray campos = items.getJSONArray("CAMPOS");
            buscarySubirArchivos(0, campos, new Callback(){
                @Override
                public void success(){
                    cb.success();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * BUSCA ARCHIVOS DE TIPO FOTOGRAFIA PARA PODER SUBIRLOS POSTERIORMENTE
     * @param x
     * @param campos
     * @param cb
     */
    private void buscarySubirArchivos(final int x, final JSONArray campos, final Callback cb){
        try {
            JSONObject item = (JSONObject) campos.get(x);
            if(item.getString("REG_TIPO").contains("foto")){
                subirArchivo(item.getInt("DOC_ID"), item.getString("REG_VALOR"), new Callback(){
                    @Override
                    public void success(){
                        buscarySubirArchivos(x+1, campos, cb);
                    }
                });
            } else {
                buscarySubirArchivos(x+1, campos, cb);
            }
        } catch (JSONException e) {
            cb.success();
        }
    }

    /**
     * SUBIDA DE ARCHIVO CON DOC_ID DEL SERVIDOR
     * @param DOC_ID
     * @param file
     * @param cb
     */
    private void subirArchivo(int DOC_ID, String file, final Callback cb){
        String[] arr = file.split("/");
        final String filename = arr[arr.length-1];
        uploadMultipart(
                getResources().getString(R.string.url_sync_upload_file),
                file,
                new UploadStatusDelegate() {
                    @Override
                    public void onProgress(Context context, UploadInfo uploadInfo) {

                    }

                    @Override
                    public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                        Processing = 0;
                        stopForeground(true);
                    }

                    @Override
                    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                        Log.d("UPLOAD "+filename, serverResponse.getBodyAsString());
                        cb.success();
                    }

                    @Override
                    public void onCancelled(Context context, UploadInfo uploadInfo) {

                    }
                },
                DOC_ID
        );
    }



    /**
     * PREPARA LA INFORMACION DE TODOS LOS DOCUMENTOS EN FORMATO JSON PARA SER SINCRONIZADOS
     * @param cb
     */
    private void prepararDocumentos(final Callback cb){
        final Cursor c = Documento.getAllSync();
        if(c.getCount() > 0) {
            startForeground(1, builder.build());
            prepararDocumento(c, JSONDocumentos, new Callback() {
                @Override
                public void success() {
                    c.close();
                    cb.success();
                }
            });
        } else {
            c.close();
            cb.success();
        }
    }

    /**
     * PREPARACION DE DOCUMENTO PARA SER SINCRONIZADO
     * @param c
     * @param JSONDocumentos
     * @param cb
     */
    private void prepararDocumento(final Cursor c, final JSONArray JSONDocumentos, final Callback cb){
        final JSONObject JSONDocumento = new JSONObject();
        c.moveToNext();

        Integer LOCAL_DOC_ID = c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID));
        Integer USU_ID = c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.USU_ID));
        Integer FRM_ID = c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
        String DOC_FECHA_CREACION = c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION));
        String DOC_EXT_NOMBRE_CLIENTE = c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
        Integer DOC_EXT_ID_CLIENTE = c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE));
        String DOC_EXT_MARCA_EQUIPO = c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO));
        String DOC_EXT_OBRA = c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));
        Integer DOC_EXT_ID_PROYECTO = c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO));
        String DOC_EXT_EQUIPO = c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO));
        String DOC_EXT_NUMERO_SERIE = c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE));

        try{
            JSONDocumento.put("LOCAL_DOC_ID", LOCAL_DOC_ID);
            JSONDocumento.put(TblDocumentoDefinition.Entry.USU_ID, USU_ID);
            JSONDocumento.put(TblDocumentoDefinition.Entry.FRM_ID, FRM_ID);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION, DOC_FECHA_CREACION);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE, DOC_EXT_NOMBRE_CLIENTE);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE, DOC_EXT_ID_CLIENTE);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO, DOC_EXT_MARCA_EQUIPO);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_OBRA, DOC_EXT_OBRA);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO, DOC_EXT_ID_PROYECTO);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO, DOC_EXT_EQUIPO);
            JSONDocumento.put(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE, DOC_EXT_NUMERO_SERIE);

            prepararRegistros(JSONDocumento, LOCAL_DOC_ID, new Callback(){
                @Override
                public void success(){
                    if(c.isLast()){
                        JSONDocumentos.put(JSONDocumento);
                        cb.success();
                    } else {
                        prepararDocumento(c, JSONDocumentos, cb);
                    }
                }
            });
        } catch (JSONException e){

        }
    }

    private void prepararRegistros(final JSONObject JSONDocumento, Integer DOC_ID, final Callback cb){
        final TblRegistroHelper DBRegistros = new TblRegistroHelper(getApplicationContext());
        final Cursor c = DBRegistros.getByLocalDocId(DOC_ID, "SYNC");
        final JSONArray JSONRegistros = new JSONArray();
        prepararRegistro(c, JSONRegistros, new Callback(){
            @Override
            public void success(){
                try {
                    JSONDocumento.put("CAMPOS", JSONRegistros);
                    c.close();
                    DBRegistros.close();
                    cb.success();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * RECORRE LOS REGISTROS DE LA BASE DE DATOS Y LOS PREPARA EN FORMATO JSON
     * @param c
     * @param JSONRegistros
     * @param cb
     */
    private void prepararRegistro(Cursor c, JSONArray JSONRegistros, Callback cb){
        JSONObject JSONRegistro = new JSONObject();
        c.moveToNext();

        Integer CAM_ID = c.getInt(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID));
        String REG_TIPO = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO));
        String REG_VALOR = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));

        /*if(REG_TIPO.contains("video") || REG_TIPO.contains("foto")){
            String[] path = REG_VALOR.split("/");
            REG_VALOR = path[path.length-1];
        }*/

        try{
            JSONRegistro.put(TblRegistroDefinition.Entry.CAM_ID, CAM_ID);
            JSONRegistro.put(TblRegistroDefinition.Entry.REG_TIPO, REG_TIPO);
            JSONRegistro.put(TblRegistroDefinition.Entry.REG_VALOR, REG_VALOR);

            JSONRegistros.put(JSONRegistro);

            if(c.isLast()){
                cb.success();
            } else {
                prepararRegistro(c, JSONRegistros, cb);
            }

        } catch (JSONException e){

        }
    }


    /**
     * Clase para poder utilizar un callback
     */
    public class Callback{
        public void success(){
            Log.d("CALLBACK", "UPLOADFILES");
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {}



}
