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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Model.Informes;
import cl.pingon.Model.ModelChecklistSimple;
import cl.pingon.Model.ModelRegistros;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

public class ReemplazoTabsActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent IntentDetalle;
    Informes Informes;
    ArrayList<Informes> ArrayInformes;

    Integer FRM_ID;
    Integer ARN_ID;
    String ARN_NOMBRE;
    String FRM_NOMBRE;
    String SECCION;

    Integer CHK_ID;
    String CHK_NOMBRE;

    SharedPreferences session;

    TblChecklistHelper Checklist;
    ArrayList ListItems;
    ArrayList<ModelChecklistSimple> ArrayChecklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reemplazo_tabs);

        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        IntentDetalle = new Intent(this, InformesDetallesActivity.class);
        IntentDetalle.putExtras(getIntent().getExtras());

        session = getSharedPreferences("session", Context.MODE_PRIVATE);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));
        FRM_ID = getIntent().getIntExtra("FRM_ID",0);
        ARN_NOMBRE = getIntent().getStringExtra("ARN_NOMBRE");
        FRM_NOMBRE = getIntent().getStringExtra("FRM_NOMBRE");

        SECCION = getIntent().getStringExtra("SECCION");
        if(SECCION != null){
            TblRegistroHelper Registros = new TblRegistroHelper(this);
            Cursor cursor = Registros.getDraftsByFrmId(FRM_ID);
            ArrayList<ModelRegistros> ModelRegistros = new ArrayList<ModelRegistros>();
            ModelRegistros VarModelRegistros;
            while(cursor.moveToNext()){
                VarModelRegistros = new ModelRegistros();
                VarModelRegistros.setREG_ID(cursor.getInt(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_ID)));
                VarModelRegistros.setDOC_ID(cursor.getInt(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.DOC_ID)));
                VarModelRegistros.setLOCAL_DOC_ID(cursor.getInt(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.LOCAL_DOC_ID)));
                VarModelRegistros.setCAM_ID(cursor.getInt(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID)));
                VarModelRegistros.setFRM_ID(cursor.getInt(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.FRM_ID)));
                VarModelRegistros.setREG_TIPO(cursor.getString(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)));
                VarModelRegistros.setREG_VALOR(cursor.getString(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
                VarModelRegistros.setREG_METADATOS(cursor.getString(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_METADATOS)));
                VarModelRegistros.setSEND_STATUS(cursor.getString(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.SEND_STATUS)));
                ModelRegistros.add(VarModelRegistros);
            }
            cursor.close();
            this.setTitle("Borradores");
            TblFormulariosHelper Formularios = new TblFormulariosHelper(this);
            cursor = Formularios.getByArnId(ARN_ID);
            while(cursor.moveToNext()){
                ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                if(FRM_ID == cursor.getInt(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_ID))){
                    FRM_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                    break;
                }
            }
            IntentDetalle.putExtra("ARN_NOMBRE", ARN_NOMBRE);
            IntentDetalle.putExtra("FRM_NOMBRE", FRM_NOMBRE);
        }
        this.setTitle(ARN_NOMBRE);
        getSupportActionBar().setSubtitle(FRM_NOMBRE);

        Checklist = new TblChecklistHelper(this);
        Cursor cursor = Checklist.getAllGroupByChkNombre(FRM_ID);
        ArrayChecklist = new ArrayList<ModelChecklistSimple>();
        ModelChecklistSimple ChecklistItem;
        ListItems = new ArrayList<String>();

        while (cursor.moveToNext()) {
            CHK_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_ID));
            CHK_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_NOMBRE));
            ChecklistItem = new ModelChecklistSimple(CHK_ID, CHK_NOMBRE);
            ArrayChecklist.add(ChecklistItem);
            ListItems.add(CHK_NOMBRE);
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListItems);

        ListView Listado = (ListView) findViewById(R.id.list);
        Listado.setAdapter(listAdapter);

        Listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                IntentDetalle.putExtra("CHK_ID", ArrayChecklist.get(index).getCHK_ID());
                IntentDetalle.putExtra("CHK_NOMBRE", ArrayChecklist.get(index).getCHK_NOMBRE());
                startActivity(IntentDetalle);
            }
        });
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
