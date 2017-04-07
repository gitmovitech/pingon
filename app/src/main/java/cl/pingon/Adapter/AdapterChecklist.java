package cl.pingon.Adapter;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import cl.pingon.InformesDetallesActivity;
import cl.pingon.Libraries.DrawSign;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.Model.ModelImage;
import cl.pingon.R;
import cl.pingon.SQLite.TblListOptionsDefinition;
import cl.pingon.SQLite.TblListOptionsHelper;
import cl.pingon.SignDrawActivity;

public abstract class AdapterChecklist extends BaseAdapter {

    private Context context;
    private ArrayList<ModelChecklistFields> ChecklistFields;
    private int contador = 0;

    private TextView Texto;
    TextInputLayout TextoInputLayout;
    EditText TextoInput;
    TextView TextViewLabel;
    TextView TextViewTitle;
    EditText NumeroInput;
    Spinner SpinnerSelect;
    ImageView ImageView;

    InformesDetallesActivity InformesDetallesActivity;
    ArrayList<ModelImage> ImageItems;
    Intent IntentSign;

    FragmentManager fm;
    EditText EditText;
    Button Button;

    public AdapterChecklist(Context context, ArrayList<ModelChecklistFields> ChecklistFields, InformesDetallesActivity InformesDetallesActivity, FragmentManager fm){
        this.ChecklistFields = ChecklistFields;
        this.context = context;
        this.InformesDetallesActivity = InformesDetallesActivity;
        this.fm = fm;
        ImageItems = new ArrayList<ModelImage>();
        IntentSign = new Intent(context, SignDrawActivity.class);
    }



    @Override
    public int getCount() {
        return ChecklistFields.size();
    }

    @Override
    public Object getItem(int i) {
        return ChecklistFields.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View ViewReturn;
        LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            ViewReturn = Inflater.inflate(R.layout.item_empty, null);
        } else {
            ViewReturn = view;
        }

        if(contador < getCount()){

            switch(ChecklistFields.get(contador).getCAM_TIPO()){
                case "firma":
                    ViewReturn = Firma(Inflater, ChecklistFields.get(contador), contador);
                    break;
                case "email":
                    ViewReturn = Inflater.inflate(R.layout.item_email, null);
                    TextoInputLayout = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    if(ChecklistFields.get(contador).getCAM_VAL_DEFECTO() != null){
                        TextoInput = (EditText) ViewReturn.findViewById(R.id.texto_input);
                        TextoInput.setText(ChecklistFields.get(contador).getCAM_VAL_DEFECTO());
                    }
                    ChecklistFields.get(contador).setView(ViewReturn);
                    break;
                case "texto":
                    ViewReturn = Inflater.inflate(R.layout.item_texto, null);
                    TextoInputLayout = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    if(ChecklistFields.get(contador).getCAM_VAL_DEFECTO() != null){
                        TextoInput = (EditText) ViewReturn.findViewById(R.id.texto_input);
                        TextoInput.setText(ChecklistFields.get(contador).getCAM_VAL_DEFECTO());
                    }
                    ChecklistFields.get(contador).setView(ViewReturn);
                    break;
                case "foto":
                    ViewReturn = Foto(Inflater, ChecklistFields.get(contador), contador);
                    break;
                case "etiqueta":
                    ViewReturn = Inflater.inflate(R.layout.item_title, null);
                    TextViewTitle = (TextView) ViewReturn.findViewById(R.id.TextViewTitle);
                    TextViewTitle.setText(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "fecha":
                    ViewReturn = Fecha(Inflater, ChecklistFields.get(contador), contador);
                    break;
                case "hora":
                    ViewReturn = Hora(Inflater, ChecklistFields.get(contador), contador);
                    /*ViewReturn = Inflater.inflate(R.layout.item_hora, null);
                    TextoInputLayout = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());*/
                    break;
                case "numero_entero":
                    ViewReturn = Inflater.inflate(R.layout.item_numero, null);
                    TextoInputLayout = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "sistema":
                    ViewReturn = Inflater.inflate(R.layout.item_numero, null);
                    TextoInputLayout = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    NumeroInput = (EditText) ViewReturn.findViewById(R.id.numero_input);
                    NumeroInput.setText("$2.400.000");
                    NumeroInput.setEnabled(false);
                    break;
                case "moneda":
                    ViewReturn = Inflater.inflate(R.layout.item_numero, null);
                    TextoInputLayout = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "binario":
                    ViewReturn = Inflater.inflate(R.layout.item_radio, null);
                    TextViewTitle = (TextView) ViewReturn.findViewById(R.id.radio_label);
                    TextViewTitle.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "lista":
                    ViewReturn = Lista(Inflater, ChecklistFields.get(contador));
                    break;
                case "video":
                    ViewReturn = Inflater.inflate(R.layout.item_video, null);
                    TextViewTitle = (TextView) ViewReturn.findViewById(R.id.TextViewLabel);
                    TextViewTitle.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                default:
                    ViewReturn = Inflater.inflate(R.layout.item_title, null);
                    TextViewTitle = (TextView) ViewReturn.findViewById(R.id.TextViewTitle);
                    TextViewTitle.setText(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    Log.d("CAM_TIPO", ChecklistFields.get(contador).getCAM_TIPO());
                    break;
            }

        }
        contador++;

        return ViewReturn;
    }

    public ArrayList<ModelChecklistFields> getChecklistData(){
        return ChecklistFields;
    }


    /**
     * CONSTRUCTOR DE HORA
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    private View Hora(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View view = Inflater.inflate(R.layout.item_hora, null);
        TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
        Button = (Button) view.findViewById(R.id.button_hora);
        final EditText EditTextHora = (EditText) view.findViewById(R.id.hora_input);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog TimePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        EditTextHora.setText(hour+":"+minute);
                    }
                },hour, minute, true);
                TimePickerDialog.show();
            }
        });
        return view;
    }


    /**
     * CONSTRUCTOR DE FECHA
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    private View Fecha(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View view = Inflater.inflate(R.layout.item_fecha, null);
        TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        TextoInputLayout.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
        Button = (Button) view.findViewById(R.id.button_fecha);
        final EditText EditTextFecha = (EditText) view.findViewById(R.id.fecha_input);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog DatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        EditTextFecha.setText(day+"-"+month+"-"+year);
                    }
                }, year, month, day);
                DatePickerDialog.show();
            }
        });
        return view;
    }


    /**
     * CONSTRUCTOR DE FIRMA
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    private View Firma(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View view = Inflater.inflate(R.layout.item_firma, null);
        TextViewLabel = (TextView) view.findViewById(R.id.TextViewLabel);
        TextViewLabel.setText(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
        if(Fields.getCAM_VAL_DEFECTO() != null){
            ImageView = (ImageView) view.findViewById(R.id.ImageViewSign);
            String[] StringSign = Fields.getCAM_VAL_DEFECTO().split("<>");
            DrawSign DrawSign = new DrawSign(StringSign[0]);
            DrawSign.DrawToImageView(ImageView);
        }
        Button ButtonFirma = (Button) view.findViewById(R.id.item_firma);
        ButtonFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentSign.putExtra("RowItemIndex", String.valueOf(RowItemIndex));
                InformesDetallesActivity.startActivityForResult(IntentSign, 99);
            }
        });
        Fields.setView(view);
        return view;
    }


    /**
     * CONSTRUCTOR DE FOTO
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    private View Foto(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View view = Inflater.inflate(R.layout.item_foto, null);

        Button ButtonFoto = (Button) view.findViewById(R.id.item_foto);
        ButtonFoto.setText(Fields.getCAM_NOMBRE_INTERNO());
        ButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.setCameraIntentAction(RowItemIndex);
            }
        });
        ImageButton ImageButtonFoto = (ImageButton) view.findViewById(R.id.ImageViewFoto);
        ImageButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.showPhoto(RowItemIndex);
            }
        });

        ArrayList<ModelImage> ModelImageItems = new ArrayList<ModelImage>();
        ImageItems.add(new ModelImage(RowItemIndex, ButtonFoto, ImageButtonFoto));
        Fields.setView(view);
        return view;
    }

    public void setImageButton(Bitmap ImageBitmapDecoded, int index){
        for(int x = 0; x < ImageItems.size(); x++){
            if(ImageItems.get(x).getIndex() == index){
                ImageItems.get(x).getImageButtonFoto().setImageBitmap(ImageBitmapDecoded);
                ImageItems.get(x).getImageButtonFoto().setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * CONSTRUCTOR DE LISTA
     * @param Inflater
     * @param Fields
     * @return
     */
    private View Lista(LayoutInflater Inflater, ModelChecklistFields Fields){
        View view = Inflater.inflate(R.layout.item_select, null);
        TextViewTitle = (TextView) view.findViewById(R.id.TextViewLabel);
        TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());
        SpinnerSelect = (Spinner) view.findViewById(R.id.SpinnerSelect);

        ArrayList<String> Listado = new ArrayList<String>();

        Listado.add("Seleccione aquí");

        TblListOptionsHelper DBHelper = new TblListOptionsHelper(context);
        Cursor cursor = DBHelper.getAllByCamId(Fields.getCAM_ID());
        while(cursor.moveToNext()){
            Listado.add(cursor.getString(cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_VALOR)));
        }
        cursor.close();

        ArrayAdapter ListadoAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Listado);
        SpinnerSelect.setAdapter(ListadoAdapter);

        return view;
    }

}
