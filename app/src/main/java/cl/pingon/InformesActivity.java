package cl.pingon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Model.Informes;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;

public class InformesActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent InformesTabsActivity;
    Informes Informes;
    TblFormulariosHelper Formularios;
    ArrayList<Informes> ArrayInformes;
    SharedPreferences session;

    String ARN_ID;
    String ARN_NOMBRE;
    String FRM_NOMBRE;
    Integer FRM_ID;
    String USU_ID;

    Integer DOC_EXT_ID_CLIENTE;
    Integer DOC_EXT_ID_PROYECTO;
    String DOC_EXT_OBRA;
    String DOC_EXT_EQUIPO;
    String DOC_EXT_MARCA_EQUIPO;
    String DOC_EXT_NUMERO_SERIE;
    String DOC_EXT_NOMBRE_CLIENTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DOC_EXT_ID_CLIENTE = getIntent().getIntExtra("DOC_EXT_ID_CLIENTE", 0);
        DOC_EXT_ID_PROYECTO = getIntent().getIntExtra("DOC_EXT_ID_PROYECTO", 0);
        DOC_EXT_OBRA = getIntent().getStringExtra("DOC_EXT_OBRA");
        DOC_EXT_EQUIPO = getIntent().getStringExtra("DOC_EXT_EQUIPO");
        DOC_EXT_MARCA_EQUIPO = getIntent().getStringExtra("DOC_EXT_MARCA_EQUIPO");
        DOC_EXT_NUMERO_SERIE = getIntent().getStringExtra("DOC_EXT_NUMERO_SERIE");
        DOC_EXT_NOMBRE_CLIENTE = getIntent().getStringExtra("DOC_EXT_NOMBRE_CLIENTE");

        session = getSharedPreferences("session", Context.MODE_PRIVATE);

        this.setTitle(DOC_EXT_NOMBRE_CLIENTE);
        getSupportActionBar().setSubtitle(DOC_EXT_OBRA);
        TextView marca = (TextView) findViewById(R.id.marca);
        marca.setText("MARCA: "+DOC_EXT_MARCA_EQUIPO);
        TextView equipo_serie = (TextView) findViewById(R.id.equipo_serie);
        equipo_serie.setText("EQUIPO Y SERIE: "+DOC_EXT_EQUIPO+" - "+DOC_EXT_NUMERO_SERIE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        ARN_ID = session.getString("arn_id", "");
        USU_ID = session.getString("user_id", "");

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

        InformesTabsActivity = new Intent(this, InformesTabsActivity.class);
        ListDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InformesTabsActivity.putExtras(getIntent().getExtras());
                Log.i("FRM_ID", String.valueOf(ArrayInformes.get(i).getId()));
                InformesTabsActivity.putExtra("FRM_ID",ArrayInformes.get(i).getId());
                InformesTabsActivity.putExtra("ARN_NOMBRE",ArrayInformes.get(i).getTitle());
                InformesTabsActivity.putExtra("FRM_NOMBRE",ArrayInformes.get(i).getSubtitle());
                startActivityForResult(InformesTabsActivity, 1);
            }
        });

        getDocumentsDatabase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            Log.i("VUELTA DE INFORM ACTI", getIntent().getExtras().toString());
        }
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

    public void getDocumentsDatabase(){
        TblDocumentoHelper Documentos = new TblDocumentoHelper(getApplicationContext());
        Cursor c = Documentos.getAll();
        Log.i("CANTIDAD DE DOCUMENTOS:", String.valueOf(c.getCount()));
        Log.i("C", "ID | DOC_ID | USU_ID | FRM_ID | DOC_NOMBRE | DOC_FECHA_CREACION | DOC_FECHA_MODIFICACION"+
                " | DOC_PDF | DOC_DECLARACION | DOC_EXT_EQUIPO | DOC_EXT_MARCA_EQUIPO | DOC_EXT_NUMERO_SERIE"+
                " | DOC_EXT_NOMBRE_CLIENTE | DOC_EXT_OBRA | DOC_EXT_ID_CLIENTE | DOC_EXT_ID_PROYECTO | SEND_STATUS");
        while(c.moveToNext()){
            Log.i("R", c.getString(c.getColumnIndexOrThrow("ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("USU_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("FRM_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_NOMBRE"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_FECHA_CREACION"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_FECHA_MODIFICACION"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_PDF"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_DECLARACION"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_EQUIPO"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_MARCA_EQUIPO"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_NUMERO_SERIE"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_NOMBRE_CLIENTE"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_OBRA"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_ID_CLIENTE"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("DOC_EXT_ID_PROYECTO"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("SEND_STATUS")));
        }
        c.close();
    }
}
