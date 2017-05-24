package cl.pingon;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;
import cl.pingon.Sync.SyncDocumentos;
import cl.pingon.Sync.SyncRegistros;

public class SyncService extends IntentService {

    private static final String ACTION_SYNC = "cl.pingon.action.SYNC";
    private static final String LOCAL_DOC_ID = "cl.pingon.extra.LOCAL_DOC_ID";
    SharedPreferences session;
    Integer ARN_ID;
    Integer FMR_ID;
    RESTService REST;
    NotificationCompat.Builder builder;
    String titulo;
    String subtitulo;

    public SyncService() {
        super("SyncService");
    }

    public static void startActionSync(Context context, Integer local_doc_id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_SYNC);
        intent.putExtra(LOCAL_DOC_ID, local_doc_id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC.equals(action)) {
                final Integer local_doc_id = intent.getIntExtra(LOCAL_DOC_ID, 0);
                handleActionSync(local_doc_id);
            }
        }
    }

    private void handleActionSync(final Integer local_doc_id) {

        REST = new RESTService(getApplicationContext());

        session = getSharedPreferences("session", this.MODE_PRIVATE);
        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));

        TblDocumentoHelper Documento = new TblDocumentoHelper(this);
        TblFormulariosHelper Formularios = new TblFormulariosHelper(this);


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

        /**
         * SINCRONIZAR DOCUMENTOS Y OBTENER ID
         */
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

                        String url_registros = getResources().getString(R.string.url_sync_registros);
                        SyncRegistros sync_registros = new SyncRegistros(getApplicationContext(), url_registros, local_doc_id, DOC_ID);
                        sync_registros.addToken(session.getString("token", ""));

                        /**
                         * SUBIR REGISTROS
                         */
                        TblRegistroHelper Registros = new TblRegistroHelper(getApplicationContext());
                        Cursor cr = Registros.getSyncByLocalDocId(local_doc_id);
                        Integer contador = 0;
                        if(cr.getCount() > 0){
                            while(cr.moveToNext()){
                                builder = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.sync)
                                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon))
                                        .setContentTitle(titulo)
                                        .setContentText(subtitulo);
                                builder.setProgress(cr.getCount(), contador, false);
                                startForeground(1, builder.build());
                                //TODO Cambiar IntentService por Service para arreglar la notificacion

                                sync_registros.addData(cr);
                                sync_registros.Post(new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //TODO Respuesta de registro
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO trabajar en respuesta de registro
                                    }
                                });
                                contador++;

                                //sync_informes.SyncData(cr);

                                Log.d("SYNCING", ":"+cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.ID)));
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            //stopForeground(true);
                        }
                        cr.close();

                        //TODO Subir archivo a producción

                    }
                } catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Alerta de error
            }
        });

        Log.d("FINISHED", "RAZON");

    }

}
