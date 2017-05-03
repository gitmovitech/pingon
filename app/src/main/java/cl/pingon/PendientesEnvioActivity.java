package cl.pingon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Adapter.AdapterListadoPendientes;
import cl.pingon.Model.Informes;
import cl.pingon.Model.ListadoPendientes;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;

public class PendientesEnvioActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes_envio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Pendientes de envío");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        ArrayList<ListadoPendientes> ArrayListadoPendientes = new ArrayList<>();
        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
        Cursor cursor = Documentos.getAllSync();
        while(cursor.moveToNext()){
            ArrayListadoPendientes.add(new ListadoPendientes(
                    cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE)),
                    "TITULO",
                    "Subtitulo"
            ));
        }

        AdapterListadoPendientes adapter = new AdapterListadoPendientes(this, ArrayListadoPendientes);
        ListView ListViewEnviados = (ListView) findViewById(R.id.ListDetalle);
        ListViewEnviados.setAdapter(adapter);

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
