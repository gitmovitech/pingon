package cl.pingon;

import android.app.Activity;
import android.content.ContentValues;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Model.Informes;
import cl.pingon.SQLite.TblDocumentoDefinition;
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

    static Activity activity;


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

        activity = this;

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
                Integer LOCAL_DOC_ID = GetInforme(ArrayInformes.get(i).getId());
                InformesTabsActivity.putExtras(getIntent().getExtras());
                InformesTabsActivity.putExtra("INFORME_STATUS","NUEVO");
                InformesTabsActivity.putExtra("FRM_ID",ArrayInformes.get(i).getId());
                InformesTabsActivity.putExtra("LOCAL_DOC_ID",LOCAL_DOC_ID);
                InformesTabsActivity.putExtra("ARN_NOMBRE",ArrayInformes.get(i).getTitle());
                InformesTabsActivity.putExtra("FRM_NOMBRE",ArrayInformes.get(i).getSubtitle());
                InformesTabsActivity.putExtra("anim_left", true);
                startActivityForResult(InformesTabsActivity, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            //Log.i("VUELTA DE INFORM ACTI", getIntent().getExtras().toString());
            Log.d("LOCAL_DOC_ID", ":"+getIntent().getStringExtra("LOCAL_DOC_ID"));
            getDocumentsDatabase();
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

    /**
     * INSERTA UN NUEVO INFORME EN LA BASE DE DATOS
     * @return
     */
    public int GetInforme(int FRM_ID){
        int ID = 0;
        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);

        //ELIMINAR REGISTROS EMPTY
        Cursor c = Documentos.getAll();
        while(c.moveToNext()){
            if(c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.SEND_STATUS)).contentEquals("EMPTY")){
                Documentos.deleteById(c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)));
            }
        }
        c.close();

        ContentValues InsertValues = new ContentValues();

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        InsertValues.put(TblDocumentoDefinition.Entry.USU_ID, USU_ID);
        InsertValues.put(TblDocumentoDefinition.Entry.FRM_ID, FRM_ID);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE, DOC_EXT_ID_CLIENTE);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO, DOC_EXT_ID_PROYECTO);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_OBRA, DOC_EXT_OBRA);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO, DOC_EXT_EQUIPO);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO, DOC_EXT_MARCA_EQUIPO);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE, DOC_EXT_NUMERO_SERIE);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE, DOC_EXT_NOMBRE_CLIENTE);
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION, ft.format(new Date()));
        InsertValues.put(TblDocumentoDefinition.Entry.DOC_FECHA_MODIFICACION, ft.format(new Date()));
        InsertValues.put(TblDocumentoDefinition.Entry.SEND_STATUS, "EMPTY");

        ID = Documentos.insert(InsertValues);
        return ID;
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
