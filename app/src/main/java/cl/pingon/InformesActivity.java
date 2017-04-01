package cl.pingon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Model.Informes;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;

public class InformesActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent IntentReemplazoTabs;
    Informes Informes;
    TblFormulariosHelper Formularios;
    ArrayList<Informes> ArrayInformes;
    SharedPreferences session;

    String ARN_ID;
    String ARN_NOMBRE;
    String FRM_NOMBRE;
    Integer FRM_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);

        this.setTitle("Informes");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ARN_ID = session.getString("arn_id", "");

        Formularios = new TblFormulariosHelper(this);
        Cursor CursorFormularios = Formularios.getByArnId(Integer.parseInt(ARN_ID));

        ArrayInformes = new ArrayList<Informes>();

        while(CursorFormularios.moveToNext()){
            ARN_NOMBRE = CursorFormularios.getString(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
            FRM_NOMBRE = CursorFormularios.getString(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
            FRM_ID = CursorFormularios.getInt(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_ID));
            Informes = new Informes(ARN_NOMBRE, FRM_NOMBRE, FRM_ID);
            //Log.d(ARN_ID, String.valueOf(FRM_ID)+" "+ARN_NOMBRE+" "+FRM_NOMBRE);
            ArrayInformes.add(Informes);
        }
        CursorFormularios.close();


        ListDetalle = (ListView) findViewById(R.id.ListDetalle);
        ListDetalle.setAdapter(new AdapterInformes(this, ArrayInformes) {});

        IntentReemplazoTabs = new Intent(this, ReemplazoTabsActivity.class);
        ListDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentReemplazoTabs.putExtra("FRM_ID",ArrayInformes.get(i).getId());
                IntentReemplazoTabs.putExtra("ARN_NOMBRE",ArrayInformes.get(i).getTitle());
                IntentReemplazoTabs.putExtra("FRM_NOMBRE",ArrayInformes.get(i).getSubtitle());
                startActivity(IntentReemplazoTabs);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
