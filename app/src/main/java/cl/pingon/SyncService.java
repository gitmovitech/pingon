package cl.pingon;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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

    private void handleActionSync(Integer local_doc_id) {

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

        String titulo = cf.getString(cf.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
        titulo += " / ";
        titulo += cf.getString(cf.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));

        String subtitulo = cd.getString(cd.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
        subtitulo += " / ";
        subtitulo +=  cd.getString(cd.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));

        cd.close();
        cf.close();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sync)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.icon))
                .setContentTitle(titulo)
                .setContentText(subtitulo);

        /**
         * SINCRONIZAR DOCUMENTOS Y OBTENER ID
         */
        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
        String url_documentos = getResources().getString(R.string.url_sync_documentos);
        SyncDocumentos sync_documentos = new SyncDocumentos(this, url_documentos, local_doc_id);
        sync_documentos.AddData(Documentos.getById(local_doc_id));
        sync_documentos.Post(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE JSON", ":"+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Alerta de error
            }
        });


        TblRegistroHelper Registros = new TblRegistroHelper(this);
        Cursor cr = Registros.getSyncByLocalDocId(local_doc_id);
        Integer contador = 0;
        if(cr.getCount() > 0){
            while(cr.moveToNext()){
                builder.setProgress(cr.getCount(), contador, false);
                startForeground(1, builder.build());
                contador++;

                //sync_informes.SyncData(cr);

                Log.d("SYNCING", ":"+cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.ID)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopForeground(true);
        }
        cr.close();

    }

}
