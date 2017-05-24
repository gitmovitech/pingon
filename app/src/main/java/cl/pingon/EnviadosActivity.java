package cl.pingon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterListadoPendientes;
import cl.pingon.Model.ListadoPendientes;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;

public class EnviadosActivity extends AppCompatActivity {

    SharedPreferences session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviados);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Informes enviados");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);

        ArrayList<ListadoPendientes> ArrayListadoPendientes = new ArrayList<>();
        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
        TblFormulariosHelper Formularios = new TblFormulariosHelper(this);
        Cursor cursor = Documentos.getAllSent();
        Integer FRM_ID;
        Integer ARN_ID = Integer.parseInt(session.getString("arn_id", ""));
        Integer cantidad_registros = cursor.getCount();
        while(cursor.moveToNext()){
            FRM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
            Cursor c = Formularios.getByArnIdFrmId(ARN_ID,FRM_ID);
            c.moveToFirst();
            ArrayListadoPendientes.add(new ListadoPendientes(
                    cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE)),
                    c.getString(c.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE)),
                    c.getString(c.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE))
            ));
            c.close();
        }
        cursor.close();

        AdapterListadoPendientes adapter = new AdapterListadoPendientes(this, ArrayListadoPendientes);
        ListView ListViewEnviados = (ListView) findViewById(R.id.ListDetalle);
        ListViewEnviados.setAdapter(adapter);

        if(cantidad_registros == 0){
            ListViewEnviados.setVisibility(View.GONE);
            ImageView iv = (ImageView) findViewById(R.id.NotFound);
            iv.setVisibility(View.VISIBLE);
            Snackbar.make(findViewById(R.id.ListDetalle), "No hay informes enviados", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        Intent IntentSyncListenerService = new Intent(this, SyncService.class);
        IntentSyncListenerService.putExtra("param1", "test");
        startService(IntentSyncListenerService);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }
}
