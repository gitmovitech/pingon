package cl.pingon;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

import cl.pingon.Adapter.AdapterChecklist;
import cl.pingon.Libraries.TimerUtils;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

public class InformesDetallesActivity extends AppCompatActivity {

    AlertDialog.Builder alert;

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
        alert = new AlertDialog.Builder(this);

        this.setTitle(getIntent().getStringExtra("FRM_NOMBRE"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("CHK_NOMBRE"));


        ArrayList<ModelChecklistFields> ArrayChecklist;
        final ListView ListViewInformesDetalles = (ListView) findViewById(R.id.ListViewInformesDetalles);
        ListViewInformesDetalles.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.getParent().requestDisallowInterceptTouchEvent(true);
                v.onTouchEvent(event);
                return true;
            }
        });

        /**
         * CARGAR LOS CHECKLIST
         */
        ArrayChecklist = getChecklists(getIntent().getIntExtra("FRM_ID",0), getIntent().getIntExtra("CHK_ID",0));
        /**
         * BUSCAR VALORES YA GUARDADOS EN REGISTROS
         */
        ArrayChecklist = completeValuesOnChecklist(ArrayChecklist, getIntent().getIntExtra("LOCAL_DOC_ID",0));


        final AdapterChecklist AdapterChecklist = new AdapterChecklist(this, ArrayChecklist, this){};
        ListViewInformesDetalles.setAdapter(AdapterChecklist);


        /**
         * GUARDAR REGISTRO
         */
        FloatingActionButton fabsave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListViewInformesDetalles.smoothScrollBy(100000,400);
                TimerUtils.TaskHandle handle = TimerUtils.setTimeout(new Runnable() {
                    public void run() {
                        validarRegistros(AdapterChecklist.getChecklistData());
                    }
                }, 500);
            }
        });


        /**
         * @TODO FOR TESTING, REMOVER DESPUES
         */
        Log.d("EXTRAS",getIntent().getExtras().toString());
        getRegistrosDatabase();
    }





    /**
     * VALIDAR DATOS ANTES DE GUARDAR LOS ITEMS
     * ------------------------------------------------------------------------------------------------------------
     * @param data
     */
    private void validarRegistros(ArrayList<ModelChecklistFields> data){
        View WidgetView;
        EditText EditText;
        Spinner Spinner;
        RadioButton RadioButton;
        String MessageErrors = "";
        int MessageCount = 1;

        for(int x = 0; x < data.size(); x++){
            switch (data.get(x).getCAM_TIPO()){
                case "email":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.texto_input);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && !EditText.getText().toString().contains("@")) {
                            MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es requerido y debe ser válido.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(EditText.getText().toString());
                        }
                    } catch (Exception e){}
                    break;
                case "texto":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.texto_input);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && EditText.getText().toString().isEmpty()) {
                            MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(EditText.getText().toString());
                        }
                    } catch(Exception e){}
                    break;
                case "firma":
                    //MessageErrors += "PROGRAMAR FIRMA\n";
                    break;
                case "foto":
                    MessageErrors += "PROGRAMAR FOTO\n";
                    break;
                case "fecha":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.fecha_input);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && EditText.getText().toString().isEmpty()) {
                            MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(EditText.getText().toString());
                        }
                    } catch(Exception e){}
                    break;
                case "hora":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.hora_input);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && EditText.getText().toString().isEmpty()) {
                            MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(EditText.getText().toString());
                        }
                    } catch(Exception e){}
                    break;
                case "lista":
                    try {
                        WidgetView = data.get(x).getView();
                        Spinner = (Spinner) WidgetView.findViewById(R.id.SpinnerSelect);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && Spinner.getSelectedItem().toString().contentEquals("Seleccione aquí")) {
                            MessageErrors += MessageCount + " - El campo de selección \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(Spinner.getSelectedItem().toString());
                        }
                    } catch(Exception e){}
                    break;
                case "binario":
                    try {
                        int checked = 1;
                        String value = "";
                        WidgetView = data.get(x).getView();
                        RadioButton = (RadioButton) WidgetView.findViewById(R.id.radio_si);
                        if(!RadioButton.isChecked()){
                            RadioButton = (RadioButton) WidgetView.findViewById(R.id.radio_no);
                            if(!RadioButton.isChecked()){
                                checked = 0;
                            } else {
                                value = RadioButton.getText().toString();
                            }
                        } else {
                            value = RadioButton.getText().toString();
                        }
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && checked == 0) {
                            MessageErrors += MessageCount + " - El campo de selección \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(value);
                        }

                    } catch(Exception e){}
                    break;
                case "numero_entero":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.numero_input);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && EditText.getText().toString().isEmpty()) {
                            MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else {
                            data.get(x).setValue(EditText.getText().toString());
                        }
                    } catch(Exception e){}
                    break;
                case "moneda":
                    MessageErrors += "PROGRAMAR MONEDA\n";
                    break;
                case "sistema":
                    MessageErrors += "PROGRAMAR SISTEMA\n";
                    break;
                case "etiqueta":
                    break;
                default:
                    MessageErrors += "PROGRAMAR OTRO:\n"+data.get(x).getCAM_TIPO();
                    break;
            }
        }

        if(MessageErrors.isEmpty()){
            guardarRegistros(data);
        } else {
            alert.setTitle("Error ");
            alert.setMessage(MessageErrors);
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.create();
            alert.show();
        }

    }




    /**
     * GUARDAR REGISTROS VALIDOS
     * ------------------------------------------------------------------------------------------------------------
     * @param data
     */
    public void guardarRegistros(ArrayList<ModelChecklistFields> data){
        TblRegistroHelper Registros = new TblRegistroHelper(this);
        ContentValues values;
        int changeDocumentStatus = 0;
        for(int x = 0; x < data.size(); x++){

            if(data.get(x).getValue() != null) {
                if(!data.get(x).getValue().isEmpty()) {
                    values = new ContentValues();
                    values.put(TblRegistroDefinition.Entry.LOCAL_DOC_ID, getIntent().getIntExtra("LOCAL_DOC_ID", 0));
                    values.put(TblRegistroDefinition.Entry.CAM_ID, data.get(x).getCAM_ID());
                    values.put(TblRegistroDefinition.Entry.FRM_ID, getIntent().getIntExtra("FRM_ID", 0));
                    values.put(TblRegistroDefinition.Entry.REG_TIPO, data.get(x).getCAM_TIPO());
                    values.put(TblRegistroDefinition.Entry.REG_VALOR, data.get(x).getValue());
                    values.put(TblRegistroDefinition.Entry.SEND_STATUS, "DRAFT");
                    Registros.insert(values);
                    changeDocumentStatus = 1;
                }
            }

        }

        if(changeDocumentStatus == 1){
            TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
            values = new ContentValues();
            values.put(TblDocumentoDefinition.Entry.SEND_STATUS, "DRAFT");
            Documentos.update(getIntent().getIntExtra("LOCAL_DOC_ID", 0), values);
        }

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

    }





    /**
     * AUTOCOMPLETA INFORMACION POR ITEM DE LA BASE DE DATOS SI ES QUE YA EXISTE UN REGISTRO GUARDADO
     *  ------------------------------------------------------------------------------------------------------------
     * @param ArrayChecklist
     * @param LOCAL_DOC_ID
     * @return
     *
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
     *  ------------------------------------------------------------------------------------------------------------
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
     *  ------------------------------------------------------------------------------------------------------------
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





    /**
     * BOTON DE NAVEGACION HACIA ATRÁS
     *  ------------------------------------------------------------------------------------------------------------
     * @return
     */
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
     * ------------------------------------------------------------------------------------------------------------
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