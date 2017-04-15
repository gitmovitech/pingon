package cl.pingon;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import cl.pingon.Adapter.AdapterChecklist;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

public class InformesDetallesActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes_detalles);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        this.setTitle(getIntent().getStringExtra("FRM_NOMBRE"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("CHK_NOMBRE"));


        ArrayList<ModelChecklistFields> ArrayChecklist;
        ListView ListViewInformesDetalles = (ListView) findViewById(R.id.ListViewInformesDetalles);
        /**
         * CARGAR LOS CHECKLIST
         */
        ArrayChecklist = getChecklists(getIntent().getIntExtra("FRM_ID",0), getIntent().getIntExtra("CHK_ID",0));
        /**
         * BUSCAR VALORES YA GUADADOS EN REGISTROS
         */
        ArrayChecklist = completeValuesOnChecklist(ArrayChecklist, getIntent().getIntExtra("LOCAL_DOC_ID",0));


        AdapterChecklist AdapterChecklist = new AdapterChecklist(this, ArrayChecklist, this){};
        ListViewInformesDetalles.setAdapter(AdapterChecklist);

        /**
         * @TODO FOR TESTING, REMOVER DESPUES
         */
        Log.d("EXTRAS",getIntent().getExtras().toString());
        getRegistrosDatabase();
    }


    /**
     * AUTOCOMPLETA INFORMACION POR ITEM DE LA BASE DE DATOS SI ES QUE YA EXISTE UN REGISTRO GUARDADO
     * @param ArrayChecklist
     * @param LOCAL_DOC_ID
     * @return
     */
    private ArrayList<ModelChecklistFields> completeValuesOnChecklist(ArrayList<ModelChecklistFields> ArrayChecklist, int LOCAL_DOC_ID){
        int CAM_ID = 0;
        String CAM_VAL_DEFECTO = "";
        TblRegistroHelper Registros = new TblRegistroHelper(this);
        Cursor c;

        for(int i = 0; i < ArrayChecklist.size(); i++){
            CAM_ID = ArrayChecklist.get(i).getCAM_ID();
            c = Registros.getByLocalDocIdAndCamId(LOCAL_DOC_ID, CAM_ID);
            if(c.getCount() > 0) {
                c.moveToFirst();
                CAM_VAL_DEFECTO = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                ArrayChecklist.get(i).setCAM_VAL_DEFECTO(CAM_VAL_DEFECTO);
                CAM_VAL_DEFECTO = "";
                break;
            }
            c.close();
        }

        return ArrayChecklist;
    }




    /**
     * OBTENER TODOS LOS ITEMS DEL CHECKLIST POR RM_ID Y CHK_ID
     */
    private int CAM_ID;
    private int CAM_POSICION;
    private String CAM_NOMBRE_INTERNO;
    private String CAM_NOMBRE_EXTERNO;
    private String CAM_TIPO;
    private String CAM_MANDATORIO;
    private String CAM_VAL_DEFECTO;
    private String CAM_PLACE_HOLDER;

    private ArrayList<ModelChecklistFields> getChecklists(int FRM_ID, int CHK_ID){
        TblChecklistHelper Checklist = new TblChecklistHelper(this);
        Cursor c = Checklist.getAllByFrmIdAndChkId(FRM_ID, CHK_ID);
        ArrayList<ModelChecklistFields> ArrayChecklist = new ArrayList<>();
        while (c.moveToNext()) {
            CAM_ID = c.getInt(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
            CAM_POSICION = c.getInt(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_POSICION));
            CAM_NOMBRE_INTERNO = c.getString(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO));
            CAM_NOMBRE_EXTERNO = c.getString(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO));
            CAM_TIPO = c.getString(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_TIPO));
            CAM_MANDATORIO = c.getString(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_MANDATORIO));
            CAM_VAL_DEFECTO = c.getString(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO));
            CAM_PLACE_HOLDER = c.getString(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER));
            ArrayChecklist.add(new ModelChecklistFields(
                    CAM_ID,
                    CAM_POSICION,
                    CAM_NOMBRE_INTERNO,
                    CAM_NOMBRE_EXTERNO,
                    CAM_TIPO,
                    CAM_MANDATORIO,
                    CAM_VAL_DEFECTO,
                    CAM_PLACE_HOLDER
            ));
        }
        c.close();

        return ArrayChecklist;
    }





    /**
     * FUNCIONES DE LA CAMARA
     * @param index
     */
    int RowItemIndex = 0;
    String ImageName = Environment.getExternalStorageDirectory() + "/Pingon/fotos/imagen-";

    public void setCameraIntentAction(int index){
        RowItemIndex = index;
        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(ImageName+index+".jpg")));
        startActivityForResult(CameraIntent, 1);
    }

    public void showPhoto(int index){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(ImageName+index+".jpg")), "image/*");
        startActivity(intent);
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
     * LISTADO DE BASE DE DATOS POR CONSOLA
     */
    public void getRegistrosDatabase(){
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