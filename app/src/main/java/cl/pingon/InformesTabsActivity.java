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

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterTabs;
import cl.pingon.Model.ModelChecklistSimple;
import cl.pingon.Model.ModelContadorTabs;
import cl.pingon.Model.ModelTabsItem;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

public class InformesTabsActivity extends AppCompatActivity {

    Intent IntentDetalle;

    Integer FRM_ID;
    Integer ARN_ID;
    String ARN_NOMBRE;
    String FRM_NOMBRE;
    Integer USU_ID;

    Integer CHK_ID;
    String CHK_NOMBRE;
    Integer LOCAL_DOC_ID;
    Integer CAM_ID;
    Integer DOC_EXT_ID_CLIENTE;
    Integer DOC_EXT_ID_PROYECTO;
    String DOC_EXT_OBRA;
    String DOC_EXT_EQUIPO;
    String DOC_EXT_MARCA_EQUIPO;
    String DOC_EXT_NUMERO_SERIE;
    String DOC_EXT_NOMBRE_CLIENTE;
    String INFORME_STATUS;

    SharedPreferences session;

    TblChecklistHelper Checklist;
    ArrayList<ModelTabsItem> ListItems;
    ArrayList<ModelChecklistSimple> ArrayChecklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes_tabs);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        IntentDetalle = new Intent(this, InformesDetallesActivity.class);
        IntentDetalle.putExtras(getIntent().getExtras());

        session = getSharedPreferences("session", Context.MODE_PRIVATE);


        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));
        USU_ID = Integer.parseInt(session.getString("user_id", ""));
        FRM_ID = getIntent().getIntExtra("FRM_ID",0);
        ARN_NOMBRE = getIntent().getStringExtra("ARN_NOMBRE");
        FRM_NOMBRE = getIntent().getStringExtra("FRM_NOMBRE");
        DOC_EXT_ID_CLIENTE = getIntent().getIntExtra("DOC_EXT_ID_CLIENTE", 0);
        DOC_EXT_ID_PROYECTO = getIntent().getIntExtra("DOC_EXT_ID_PROYECTO", 0);
        DOC_EXT_OBRA = getIntent().getStringExtra("DOC_EXT_OBRA");
        DOC_EXT_EQUIPO = getIntent().getStringExtra("DOC_EXT_EQUIPO");
        DOC_EXT_MARCA_EQUIPO = getIntent().getStringExtra("DOC_EXT_MARCA_EQUIPO");
        DOC_EXT_NUMERO_SERIE = getIntent().getStringExtra("DOC_EXT_NUMERO_SERIE");
        DOC_EXT_NOMBRE_CLIENTE = getIntent().getStringExtra("DOC_EXT_NOMBRE_CLIENTE");
        LOCAL_DOC_ID = getIntent().getIntExtra("LOCAL_DOC_ID", 0); Log.d("LOCAL_DOC_ID DE IA", String.valueOf(LOCAL_DOC_ID));
        INFORME_STATUS = getIntent().getStringExtra("INFORME_STATUS");


        /**
         * INICIALIZACION DE VARIABLES
         */
        Checklist = new TblChecklistHelper(this);
        Cursor cursor = Checklist.getAllGroupByChkNombre(FRM_ID);
        ArrayChecklist = new ArrayList<ModelChecklistSimple>();
        ModelChecklistSimple ChecklistItem;
        ListItems = new ArrayList<ModelTabsItem>();
        ModelContadorTabs ContadorTabs;
        AdapterTabs list = new AdapterTabs(this, ListItems);
        ListView Listado = (ListView) findViewById(R.id.list);


        /**
         * Si no viene definido entonces viene de borradores
         */
        if(ARN_NOMBRE == null && FRM_NOMBRE == null){
            TblFormulariosHelper Formularios = new TblFormulariosHelper(this);
            Cursor CursorFormularios = Formularios.getByArnId(ARN_ID);
            while(CursorFormularios.moveToNext()){
                ARN_NOMBRE = CursorFormularios.getString(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                FRM_NOMBRE = CursorFormularios.getString(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                IntentDetalle.putExtra("FRM_NOMBRE", FRM_NOMBRE);
            }
            CursorFormularios.close();
        }

        this.setTitle(ARN_NOMBRE);
        getSupportActionBar().setSubtitle(FRM_NOMBRE);


        /**
         * CURSOR CHECKLIST
         */
        while (cursor.moveToNext()) {
            CHK_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_ID));
            CHK_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_NOMBRE));
            CAM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
            ChecklistItem = new ModelChecklistSimple(CHK_ID, CHK_NOMBRE);
            ArrayChecklist.add(ChecklistItem);

            /**
             * @TODO: REVISAR LOS CONTADORES SE SUMAN CON LAS OTRAS SECCIONES
             */
            ContadorTabs = getContadoresTabsRegistros(this, FRM_ID, CHK_ID);
            ListItems.add(new ModelTabsItem(
                    CHK_NOMBRE,
                    "Total "+ContadorTabs.getContador_total_completados()+" de "+ContadorTabs.getContador_total(),
                    "Obligatorios "+ContadorTabs.getContador_mandatorios_completados()+" de "+ContadorTabs.getContador_mandatorios(),
                    ContadorTabs.getCheck()));
        }
        cursor.close();



        Listado.setAdapter(list);

        Listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                IntentDetalle.putExtras(getIntent().getExtras());
                IntentDetalle.putExtra("LOCAL_DOC_ID", LOCAL_DOC_ID);
                IntentDetalle.putExtra("CHK_ID", ArrayChecklist.get(index).getCHK_ID());
                IntentDetalle.putExtra("CHK_NOMBRE", ArrayChecklist.get(index).getCHK_NOMBRE());
                startActivityForResult(IntentDetalle, 1);
            }
        });
    }

    /**
     * CONTADOR DE TOTAL DE ITEMS NORMALES Y OLBIGATORIOS CONTESTADOS
     * @param context
     * @param FRM_ID
     * @return String
     */
    private ModelContadorTabs getContadoresTabsRegistros(Context context, int FRM_ID, int CHK_ID){

        TblRegistroHelper Registros = new TblRegistroHelper(context);
        TblChecklistHelper Checklist = new TblChecklistHelper(context);
        ModelContadorTabs ContadorTabs = new ModelContadorTabs();
        int contador_total_completados = 0;
        int contador_total = 0;
        int contador_obligatorios = 0;
        int contador_obligatorios_completados = 0;

        Cursor CursorChecklist = Checklist.getAllByFrmIdAndChkId(FRM_ID, CHK_ID);
        Cursor CursorRegistros;

        String CAM_MANDATORIO;

        while(CursorChecklist.moveToNext()){
            //Log.d("OBTENIENDO FRM Y CHK_ID", FRM_ID+":"+CHK_ID);
            contador_total++;
            CAM_MANDATORIO = CursorChecklist.getString(CursorChecklist.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_MANDATORIO));
            if(CAM_MANDATORIO.contains("S")){
                contador_obligatorios++;
            }
            if(LOCAL_DOC_ID != 0){
                CursorRegistros = Registros.getByLocalDocId(LOCAL_DOC_ID);
                while(CursorRegistros.moveToNext()){
                    if(CAM_ID == CursorRegistros.getInt(CursorRegistros.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID))){
                        contador_total_completados++;
                        if(CAM_MANDATORIO.contains("S")){
                            contador_obligatorios_completados++;
                        }
                    }
                }
                CursorRegistros.close();
            }
        }
        CursorChecklist.close();

        ContadorTabs.setContador_total(contador_total);
        ContadorTabs.setContador_mandatorios(contador_obligatorios);
        ContadorTabs.setContador_total_completados(contador_total_completados);
        ContadorTabs.setContador_mandatorios_completados(contador_obligatorios_completados);

        if(contador_obligatorios == contador_obligatorios_completados){
            ContadorTabs.setCheck(1);
        } else {
            ContadorTabs.setCheck(0);
        }

        //Log.d("EXTRAS TOTAL STRING", getIntent().getExtras().toString());
        return ContadorTabs;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            Intent intent = new Intent();
            intent.putExtras(getIntent().getExtras());
            setResult(RESULT_OK, intent);
            Log.d("VUELTA DE ACTIVITIES", getIntent().getExtras().toString());
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

    public void getRegistersDatabase(){
        TblRegistroHelper Registros = new TblRegistroHelper(getApplicationContext());
        Cursor c = Registros.getAll();
        Log.i("CANTIDAD DE REGISTROS:", String.valueOf(c.getCount()));
        Log.i("C", "LOCAL_DOC_ID | CAM_ID | FRM_ID | REG_ID | REG_TIPO | REG_VALOR | REG_METADATOS | SEND_STATUS");
        while(c.moveToNext()){
            Log.i("R", c.getString(c.getColumnIndexOrThrow("LOCAL_DOC_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("CAM_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("FRM_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("REG_ID"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("REG_TIPO"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("REG_VALOR"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("REG_METADATOS"))+" | "+
                    c.getString(c.getColumnIndexOrThrow("SEND_STATUS")));
        }
        c.close();
    }
}
