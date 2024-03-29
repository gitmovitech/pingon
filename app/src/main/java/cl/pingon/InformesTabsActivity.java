package cl.pingon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterTabs;
import cl.pingon.Model.ModelChecklistSimple;
import cl.pingon.Model.ModelContadorTabs;
import cl.pingon.Model.ModelTabsItem;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblDocumentoHelper;
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

    AdapterTabs list;
    ListView Listado;

    Menu MenuButton;
    static Activity activity;

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
        if(getIntent().getBooleanExtra("anim_left", false)) {
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        }

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
        LOCAL_DOC_ID = getIntent().getIntExtra("LOCAL_DOC_ID", 0);
        INFORME_STATUS = getIntent().getStringExtra("INFORME_STATUS");


        /**
         * INICIALIZACION DE VARIABLES
         */
        Checklist = new TblChecklistHelper(this);
        ArrayChecklist = new ArrayList<ModelChecklistSimple>();
        getItems(FRM_ID);
        list = new AdapterTabs(this, ListItems);
        Listado = (ListView) findViewById(R.id.list);


        /**
         * Si no viene definido entonces viene de borradores
         */
        if(ARN_NOMBRE == null && FRM_NOMBRE == null){
            TblFormulariosHelper Formularios = new TblFormulariosHelper(this);
            Cursor CursorFormularios = Formularios.getByArnId(ARN_ID);
            while(CursorFormularios.moveToNext()) {
                if(FRM_ID == CursorFormularios.getInt(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_ID))){
                    ARN_NOMBRE = CursorFormularios.getString(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                    FRM_NOMBRE = CursorFormularios.getString(CursorFormularios.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                    IntentDetalle.putExtra("FRM_NOMBRE", FRM_NOMBRE);
                }
            }
            CursorFormularios.close();
            Formularios.close();
        }
        getSupportActionBar().setSubtitle(FRM_NOMBRE);
        this.setTitle(ARN_NOMBRE);


        activity = this;


        Listado.setAdapter(list);

        Listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                IntentDetalle.putExtras(getIntent().getExtras());
                IntentDetalle.putExtra("CHK_ID", ArrayChecklist.get(index).getCHK_ID());
                IntentDetalle.putExtra("CHK_NOMBRE", ArrayChecklist.get(index).getCHK_NOMBRE());
                startActivityForResult(IntentDetalle, 1);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_informes_tabs, menu);
        MenuButton = menu;
        if(ActivateSendButton == 1){
            for(int m = 0; m < MenuButton.size(); m++){
                if(MenuButton.getItem(m).getItemId() == R.id.ButtonSend){
                    MenuButton.getItem(m).setVisible(true);
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.completed_fields_message), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ButtonSend:
                Intent createPdfIntent = new Intent(getApplicationContext(), PdfPreviewActivity.class);
                createPdfIntent.putExtra("LOCAL_DOC_ID", LOCAL_DOC_ID);
                startActivity(createPdfIntent);
                return true;
            case R.id.ButtonErase:
                @SuppressWarnings("RestrictedApi")
                ContextThemeWrapper cwrapper = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog);
                AlertDialog.Builder alert = new AlertDialog.Builder(cwrapper);
                alert.setTitle(getResources().getString(R.string.delete_draft));
                alert.setMessage(getResources().getString(R.string.delete_draft_question));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        TblRegistroHelper Registros = new TblRegistroHelper(getApplicationContext());
                        Registros.deleteByDocId(LOCAL_DOC_ID);
                        Registros.close();

                        TblDocumentoHelper Documento = new TblDocumentoHelper(getApplicationContext());
                        Documento.deleteById(LOCAL_DOC_ID);
                        Documento.close();

                        dialog.cancel();
                        Intent intent = new Intent(getApplicationContext(), BuzonActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create();
                alert.show();
                return true;
            case R.id.ButtonGoHome:
                Intent intent = new Intent(getApplicationContext(), BuzonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    int ActivateSendButton = 1;
    private void getItems(int FRM_ID){
        ModelContadorTabs ContadorTabs;
        ModelChecklistSimple ChecklistItem;
        Cursor cursor = Checklist.getAllGroupByChkNombre(FRM_ID);
        ListItems = new ArrayList<ModelTabsItem>();
        String required_message = "";

        int obligatorios = 0;
        int obligatorios_completados = 0;

        while (cursor.moveToNext()) {
            CHK_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_ID));
            CHK_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_NOMBRE));
            CAM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
            ChecklistItem = new ModelChecklistSimple(CHK_ID, CHK_NOMBRE);
            ArrayChecklist.add(ChecklistItem);

            ContadorTabs = getContadoresTabsRegistros(this, FRM_ID, CHK_ID);

            obligatorios += ContadorTabs.getContador_mandatorios();
            obligatorios_completados += ContadorTabs.getContador_mandatorios_completados();


            if(ContadorTabs.getContador_mandatorios() == 0){
                required_message = "";
            } else {
                required_message = "Obligatorios "+ContadorTabs.getContador_mandatorios_completados()+" de "+ContadorTabs.getContador_mandatorios();
            }

            ListItems.add(new ModelTabsItem(
                    CHK_NOMBRE,
                    "Total "+ContadorTabs.getContador_total_completados()+" de "+ContadorTabs.getContador_total(),
                    required_message,
                    ContadorTabs.getCheck()));
        }

        if(obligatorios == obligatorios_completados){
            ActivateSendButton = 1;
        } else {
            ActivateSendButton = 0;
        }

        cursor.close();
        Checklist.close();
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
        Integer CAM_ID;
        Log.d("=============", "=============");
        while(CursorChecklist.moveToNext()){
            if(!CursorChecklist.getString(CursorChecklist.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_TIPO)).contains("etiqueta")){
                contador_total++;
                CAM_ID = 0;
                CAM_MANDATORIO = CursorChecklist.getString(CursorChecklist.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_MANDATORIO));
                if(CAM_MANDATORIO.contains("S")){
                    contador_obligatorios++;
                }
                if(LOCAL_DOC_ID != 0){
                    CAM_ID = CursorChecklist.getInt(CursorChecklist.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
                    CursorRegistros = Registros.getDraftByLocalDocIdChkIdFrmId(LOCAL_DOC_ID, CHK_ID, FRM_ID, CAM_ID);
                    while(CursorRegistros.moveToNext()){
                        contador_total_completados++;
                        if(CAM_MANDATORIO.contains("S")){
                            contador_obligatorios_completados++;
                        }
                    }
                    CursorRegistros.close();
                }
            }
        }

        Log.d(":"+contador_obligatorios_completados, ":"+ contador_obligatorios+"="+contador_total+"=="+contador_total_completados);
        CursorChecklist.close();

        ContadorTabs.setContador_total(contador_total);
        ContadorTabs.setContador_mandatorios(contador_obligatorios);
        ContadorTabs.setContador_total_completados(contador_total_completados);
        ContadorTabs.setContador_mandatorios_completados(contador_obligatorios_completados);

        if(contador_total == contador_total_completados){
            ContadorTabs.setCheck(1);
        } else {
            ContadorTabs.setCheck(0);
        }


        Registros.close();
        Checklist.close();

        return ContadorTabs;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            getItems(FRM_ID);
            Intent intent = new Intent(this, InformesTabsActivity.class);
            intent.putExtras(getIntent().getExtras());
            intent.putExtra("anim_left", false);
            finish();
            startActivity(intent);
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
        Registros.close();
    }
}
