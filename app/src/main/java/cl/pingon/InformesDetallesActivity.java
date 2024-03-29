package cl.pingon;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.lowagie.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import cl.pingon.Adapter.AdapterChecklist;
import cl.pingon.Libraries.CalculateHours;
import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.ImageUtils;
import cl.pingon.Libraries.Rut;
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
    AdapterChecklist AdapterChecklist;
    ListView ListViewInformesDetalles;
    Dialog ImagePreviewDialog;
    ImageView ImagePreview;
    Button btnGuardar;
    Button btnCancelar;

    private static final int PERMS_REQUEST_CAMERA = 0;
    private static final int PERMS_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

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
        ImagePreviewDialog = new Dialog(this);
        ImagePreviewDialog.setContentView(R.layout.image_preview_dialog);
        ImagePreviewDialog.setTitle("Previsualización");

        ImagePreview = (ImageView) ImagePreviewDialog.findViewById(R.id.ImagePreviewDialog);
        btnGuardar = (Button) ImagePreviewDialog.findViewById(R.id.btnGuardar);
        btnCancelar = (Button) ImagePreviewDialog.findViewById(R.id.btnCancelar);

        this.setTitle(getIntent().getStringExtra("FRM_NOMBRE"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("CHK_NOMBRE"));


        ArrayList<ModelChecklistFields> ArrayChecklist;
        ListViewInformesDetalles = (ListView) findViewById(R.id.ListViewInformesDetalles);


        /**
         * CARGAR LOS CHECKLIST
         */
        ArrayChecklist = getChecklists(getIntent().getIntExtra("FRM_ID",0), getIntent().getIntExtra("CHK_ID",0));
        /**
         * BUSCAR VALORES YA GUARDADOS EN REGISTROS
         */
        ArrayChecklist = completeValuesOnChecklist(ArrayChecklist, getIntent().getIntExtra("LOCAL_DOC_ID",0),getIntent().getIntExtra("FRM_ID", 0));

        AdapterChecklist = new AdapterChecklist(this, ArrayChecklist, this, getIntent().getIntExtra("FRM_ID",0)){};
        ListViewInformesDetalles.setAdapter(AdapterChecklist);
        ListViewInformesDetalles.setVisibility(View.INVISIBLE);
        /*ListViewInformesDetalles.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                ListViewInformesDetalles.smoothScrollToPosition(ListViewInformesDetalles.getCount());
                Log.e("FOCUS", "TRUE");
            }
        });*/

        RequestWriteExternalPerms();
        //getRegistrosDatabase();
        //Log.d("NUMERO", ListViewInformesDetalles.scrollListBy();+")");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        int height = size.y;
        ListViewInformesDetalles.scrollBy(width,0);
        ListViewInformesDetalles.setFastScrollEnabled(false);
        ListViewInformesDetalles.setVisibility(View.VISIBLE);
        TimerUtils.setTimeout(new Runnable() {
            public void run() {
                ListViewInformesDetalles.smoothScrollToPosition(ListViewInformesDetalles.getCount());
            }
        }, 100);
        TimerUtils.setTimeout(new Runnable() {
            public void run() {
                ListViewInformesDetalles.smoothScrollToPosition(0);
            }
        }, 1000);
        TimerUtils.setTimeout(new Runnable() {
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup);
                ListViewInformesDetalles.startAnimation(animation);
                ListViewInformesDetalles.scrollTo(0,0);
            }
        }, 1500);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_informe_detalles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ButtonSave:
                ListViewInformesDetalles.smoothScrollBy(100000,400);
                TimerUtils.TaskHandle handle = TimerUtils.setTimeout(new Runnable() {
                    public void run() {
                        validarRegistros(AdapterChecklist.getChecklistData());
                    }
                }, 500);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Crear estructura de carpetas par archivos
     */
    private void createFolderImagesStructure(){
        File file = new File(Environment.getExternalStorageDirectory() + "/Pingon");
        if(!file.exists()){
            file.mkdirs();
        }
        file = new File(Environment.getExternalStorageDirectory() + "/Pingon/fotos");
        if(!file.exists()){
            file.mkdirs();
        }
        file = new File(Environment.getExternalStorageDirectory() + "/Pingon/firmas");
        if(!file.exists()){
            file.mkdirs();
        }
        file = new File(Environment.getExternalStorageDirectory() + "/Pingon/videos");
        if(!file.exists()){
            file.mkdirs();
        }
        file = new File(Environment.getExternalStorageDirectory() + "/Pingon/audios");
        if(!file.exists()){
            file.mkdirs();
        }
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
        SeekBar SeekBar;
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
                case "rut_responsable":
                case "rut":
                case "texto":
                case "responsable":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.texto_input);
                        if (data.get(x).getCAM_MANDATORIO().equals("S") && EditText.getText().toString().isEmpty()) {
                            MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" es obligatorio.\n\n";
                            MessageCount++;
                        } else if(data.get(x).getCAM_TIPO().contains("rut") && data.get(x).getCAM_MANDATORIO().equals("S")){
                            EditText.setText(Rut.formatear(EditText.getText().toString()));
                            if(!Rut.validar(EditText.getText().toString())){
                                MessageErrors += MessageCount + " - El campo \"" + data.get(x).getCAM_NOMBRE_EXTERNO() + "\" no es un RUT válido.\n\n";
                                MessageCount++;
                            } else {
                                data.get(x).setValue(EditText.getText().toString());
                            }
                        } else {
                            if(data.get(x).getCAM_TIPO().contains("rut")){
                                EditText.setText(Rut.formatear(EditText.getText().toString()));
                            }
                            data.get(x).setValue(EditText.getText().toString());
                        }
                    } catch(Exception e){}
                    break;
                case "firma":
                    //NO REQUERIDO, SE GUARDA EN EL ADAPTADOR
                    break;
                case "foto":
                    //NO REQUERIDO, SE GUARDA EN EL ADAPTADOR
                    break;
                case "video":
                    //NO REQUERIDO, SE GUARDA EN EL ADAPTADOR
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
                case "hora_entrada":
                case "hora_salida":
                    try {
                        WidgetView = data.get(x).getView();
                        EditText = (EditText) WidgetView.findViewById(R.id.hora_input);
                        if(data.get(x).getCAM_TIPO().contains("hora_entrada")){
                            EditText.setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    Log.d("KEYLISTENER", ":"+keyCode);
                                    return false;
                                }
                            });
                        }
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
                case "hora_colacion":
                case "dia_habil":
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
                case "moneda":
                case "sistema":
                case "hora_total_diaria":
                case "hora_total_diaria_extra":
                case "hora_total_semanal":
                case "hora_total_semanal_extra":
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
                case "slider_bar":
                    try {
                        WidgetView = data.get(x).getView();
                        SeekBar = (SeekBar) WidgetView.findViewById(R.id.seekBar);
                        data.get(x).setValue(String.valueOf(SeekBar.getProgress()));
                    } catch(Exception e){}
                    break;
                case "etiqueta":
                    break;
                default:
                    //TODO: OTRO TIPO DE CAMPO??;
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

        Cursor cursor;
        int LOCAL_REG_ID;

        for(int x = 0; x < data.size(); x++){
            LOCAL_REG_ID = 0;
            cursor = Registros.getDraftByLocalDocIdCamIdAndFrmId(getIntent().getIntExtra("LOCAL_DOC_ID", 0), data.get(x).getCAM_ID(), getIntent().getIntExtra("FRM_ID", 0));
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                LOCAL_REG_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.ID));
            }
            cursor.close();

            int guardar = 0;
            if(data.get(x).getValue() != null) {
                if(!data.get(x).getValue().isEmpty()) {
                    guardar = 1;
                    if(data.get(x).getCAM_TIPO().contains("lista")){
                        if(data.get(x).getValue().contains("Seleccione aquí")){
                            guardar = 0;
                        }
                    }
                    if(guardar == 1) {
                        values = new ContentValues();
                        values.put(TblRegistroDefinition.Entry.LOCAL_DOC_ID, getIntent().getIntExtra("LOCAL_DOC_ID", 0));
                        values.put(TblRegistroDefinition.Entry.CAM_ID, data.get(x).getCAM_ID());
                        values.put(TblRegistroDefinition.Entry.FRM_ID, getIntent().getIntExtra("FRM_ID", 0));
                        values.put(TblRegistroDefinition.Entry.CHK_ID, getIntent().getIntExtra("CHK_ID", 0));
                        values.put(TblRegistroDefinition.Entry.REG_TIPO, data.get(x).getCAM_TIPO());
                        values.put(TblRegistroDefinition.Entry.REG_VALOR, data.get(x).getValue());
                        values.put(TblRegistroDefinition.Entry.SEND_STATUS, "DRAFT");
                        values.put(TblRegistroDefinition.Entry.CAM_POSICION, data.get(x).getCAM_POSICION());
                        if (LOCAL_REG_ID > 0) {
                            Registros.update(LOCAL_REG_ID, values);
                        } else {
                            Registros.insert(values);
                        }
                        changeDocumentStatus = 1;
                    }
                }
            }

        }
        Registros.close();

        if(changeDocumentStatus == 1){
            TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
            values = new ContentValues();
            values.put(TblDocumentoDefinition.Entry.SEND_STATUS, "DRAFT");
            Documentos.update(getIntent().getIntExtra("LOCAL_DOC_ID", 0), values);
            Documentos.close();
        }


        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        Snackbar.make(findViewById(R.id.activity_informes_detalles), "Registro guardado", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        //RETORNO AL MENU DE TABS
        onSupportNavigateUp();

    }





    /**
     * AUTOCOMPLETA INFORMACION POR ITEM DE LA BASE DE DATOS SI ES QUE YA EXISTE UN REGISTRO GUARDADO
     *  ------------------------------------------------------------------------------------------------------------
     * @param ArrayChecklist
     * @param LOCAL_DOC_ID
     * @return
     *
     */
    private ArrayList<ModelChecklistFields> completeValuesOnChecklist(ArrayList<ModelChecklistFields> ArrayChecklist, int LOCAL_DOC_ID, int FRM_ID){
        int CAM_ID = 0;
        String CAM_VAL_DEFECTO = "";
        TblRegistroHelper Registros = new TblRegistroHelper(this);
        Cursor c;

        for(int i = 0; i < ArrayChecklist.size(); i++){
            CAM_ID = ArrayChecklist.get(i).getCAM_ID();
            c = Registros.getDraftByLocalDocIdCamIdAndFrmId(LOCAL_DOC_ID, CAM_ID, FRM_ID);
            Log.d("REGISTROS", String.valueOf(c.getCount()));
            if(c.getCount() > 0) {
                c.moveToFirst();
                CAM_VAL_DEFECTO = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                ArrayChecklist.get(i).setCAM_VAL_DEFECTO(CAM_VAL_DEFECTO);
                CAM_VAL_DEFECTO = "";
            }
            c.close();
        }
        Registros.close();

        return ArrayChecklist;
    }





    /**
     * OBTENER TODOS LOS ITEMS DEL CHECKLIST POR FRM_ID Y CHK_ID
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
    private int CUSTOM_LIST;

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
            CUSTOM_LIST = c.getInt(c.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CUSTOM_LIST));
            ArrayChecklist.add(new ModelChecklistFields(
                    CAM_ID,
                    CAM_POSICION,
                    CAM_NOMBRE_INTERNO,
                    CAM_NOMBRE_EXTERNO,
                    CAM_TIPO,
                    CAM_MANDATORIO,
                    CAM_VAL_DEFECTO,
                    CAM_PLACE_HOLDER,
                    CUSTOM_LIST
            ));
        }
        c.close();
        Checklist.close();

        return ArrayChecklist;
    }





    /**
     * FUNCIONES DE LA CAMARA
     * @param index
     *  ------------------------------------------------------------------------------------------------------------
     */
    public int RowItemIndex = 0;
    int RowItemFrmId = 0;
    public String LastImageFilename = "";
    String LastVideoFilename = "";
    String ImageName = Environment.getExternalStorageDirectory() + "/Pingon/fotos/imagen-";
    String VideoName = Environment.getExternalStorageDirectory() + "/Pingon/videos/video-";
    String AudioName = Environment.getExternalStorageDirectory() + "/Pingon/audios/audio-";
    Intent CameraIntent;
    Intent takeVideoIntent;

    public void setCameraIntentAction(int index, int FRM_ID){
        RowItemFrmId = FRM_ID;
        String filename = ImageName+FRM_ID+"-"+RowItemIndex+".jpg";
        LastImageFilename = filename;
        RowItemIndex = index;
        CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filename)));
        RequestCameraPerms();
    }

    public void showPhoto(String filename){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filename)), "image/*");
        startActivity(intent);
    }

    public void dispatchTakeVideoIntent(int index, int FRM_ID) {
        RowItemIndex = index;
        LastVideoFilename = VideoName+FRM_ID+"-"+RowItemIndex+".mp4";
        takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(LastVideoFilename)));
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public void RequestWriteExternalPerms(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMS_WRITE_EXTERNAL_STORAGE);
        } else {
            createFolderImagesStructure();
        }
    }

    public void RequestCameraPerms(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMS_REQUEST_CAMERA);
        } else {
            startActivityForResult(CameraIntent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(CameraIntent, 1);
                } else {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                    dialogo.setTitle("Error de permisos");
                    dialogo.setMessage("Esta aplicación requiere poder usar la camara para funcionar correctamente.");
                    dialogo.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            RequestCameraPerms();
                        }
                    });
                    dialogo.create();
                    dialogo.show();
                }
                return;
            }
            case PERMS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createFolderImagesStructure();
                } else {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                    dialogo.setTitle("Error de permisos");
                    dialogo.setMessage("Esta aplicación requiere poder escribir datos en la memoria para guardar las fotografías.");
                    dialogo.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            RequestWriteExternalPerms();
                        }
                    });
                    dialogo.create();
                    dialogo.show();
                }
                return;
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        //FOTO
        if(requestCode == 1){
            ImageUtils img = new ImageUtils();
            try {
                Bitmap ImageBitmapDecoded = img.ImageThumb(BitmapFactory.decodeFile(LastImageFilename));
                ImagePreview.setImageBitmap(ImageBitmapDecoded);
                ImagePreviewDialog.show();
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePreviewDialog.hide();
                    }
                });
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePreviewDialog.hide();
                        ModelChecklistFields Fields = AdapterChecklist.getChecklistData().get(RowItemIndex);
                        Fields.setValue(LastImageFilename);
                        Fields.setCAM_VAL_DEFECTO(LastImageFilename);
                        Fields.getView().findViewById(R.id.image_button).setVisibility(View.VISIBLE);
                        Log.d("PATH", Fields.getValue());
                    }
                });
            } catch (Exception E){
                Log.e("IMAGENAME", ":"+ImageName);
                Log.e("IMAGEINDEX", ":"+RowItemIndex);
                Log.e("IMAGEFILENAME", ":"+LastImageFilename);
            }
        }


        //GALERIA
        if (requestCode == 77 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            final Uri uri = data.getData();
            ImageUtils img = new ImageUtils();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImagePreview.setImageBitmap(img.ImageThumb(bitmap));
                ImagePreviewDialog.show();
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePreviewDialog.hide();
                        ModelChecklistFields Fields = AdapterChecklist.getChecklistData().get(RowItemIndex);
                        Fields.getView().findViewById(R.id.image_button).setVisibility(View.VISIBLE);
                        try {
                            File filename = savebitmap(bitmap, ImageName+AdapterChecklist.getFRMID()+"-"+RowItemIndex+".jpg");
                            Fields.setValue(filename.getAbsolutePath());
                            Fields.setCAM_VAL_DEFECTO(filename.getAbsolutePath());
                            LastImageFilename = Fields.getValue();
                            Log.d("PATH", Fields.getValue());
                            Log.d("PATH", Fields.getCAM_VAL_DEFECTO());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePreviewDialog.hide();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //VIDEO
        if (requestCode == REQUEST_VIDEO_CAPTURE){
            AdapterChecklist.setVideoURI(Uri.parse(LastVideoFilename));
            AdapterChecklist.setLinearLayoutVideoVisibility();
            AdapterChecklist.setVideoViewItemVisibility();
            ModelChecklistFields Fields = AdapterChecklist.getChecklistData().get(RowItemIndex);
            Fields.setValue(LastVideoFilename);
        }


        //FIRMA
        if(requestCode == 10){
            if(resultCode == RESULT_OK) {
                try {
                    RowItemIndex = Integer.parseInt(data.getStringExtra("RowItemIndex"));
                    String sign = data.getStringExtra("sign");
                    ModelChecklistFields Fields = AdapterChecklist.getChecklistData().get(RowItemIndex);
                    ImageView signImage = (ImageView) Fields.getView().findViewById(R.id.ImageViewSign);
                    DrawSign firma = new DrawSign(sign);
                    firma.DrawToImageView(signImage);
                    Fields.setValue(sign);
                } catch(Exception e){
                    Log.e("FIRMA", e.toString());
                }
            }
        }
    }


    public static File savebitmap(Bitmap bmp, String filename) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(/*Environment.getExternalStorageDirectory()
                + File.separator + */filename);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
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
        Registros.close();
    }


}