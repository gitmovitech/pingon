package cl.pingon.Adapter;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.InformesDetallesActivity;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.Model.ModelImage;
import cl.pingon.R;
import cl.pingon.SQLite.TblListOptionsDefinition;
import cl.pingon.SQLite.TblListOptionsHelper;

public abstract class AdapterChecklist extends BaseAdapter {

    private Context context;
    private ArrayList<ModelChecklistFields> ChecklistFields;
    private int contador = 0;

    private TextView Texto;
    TextInputLayout TextoInput;
    TextView TextViewLabel;
    TextView TextViewTitle;
    EditText NumeroInput;
    Spinner SpinnerSelect;

    InformesDetallesActivity InformesDetallesActivity;
    ArrayList<ModelImage> ImageItems;

    EditText EditText;

    public AdapterChecklist(Context context, ArrayList<ModelChecklistFields> ChecklistFields, InformesDetallesActivity InformesDetallesActivity){
        this.ChecklistFields = ChecklistFields;
        this.context = context;
        this.InformesDetallesActivity = InformesDetallesActivity;
        ImageItems = new ArrayList<ModelImage>();
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
                case "texto":
                    ViewReturn = Inflater.inflate(R.layout.item_texto, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    EditText = (EditText) ViewReturn.findViewById(R.id.texto_input);
                    if(ChecklistFields.get(contador).getCAM_MANDATORIO() == "S"){
                        EditText.setError("Este campo es requerido");
                    }
                    break;
                case "firma":
                    ViewReturn = Inflater.inflate(R.layout.item_firma, null);
                    TextViewLabel = (TextView) ViewReturn.findViewById(R.id.TextViewLabel);
                    TextViewLabel.setText(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "email":
                    ViewReturn = Inflater.inflate(R.layout.item_email, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    ChecklistFields.get(contador).setView(ViewReturn);
                    if(ChecklistFields.get(contador).getCAM_MANDATORIO() == "S"){
                        EditText.setError("Este correo es requerido");
                    }
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
                    ViewReturn = Inflater.inflate(R.layout.item_fecha, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "hora":
                    ViewReturn = Inflater.inflate(R.layout.item_hora, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "numero_entero":
                    ViewReturn = Inflater.inflate(R.layout.item_numero, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                case "sistema":
                    ViewReturn = Inflater.inflate(R.layout.item_numero, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    NumeroInput = (EditText) ViewReturn.findViewById(R.id.numero_input);
                    NumeroInput.setText("$2.400.000");
                    NumeroInput.setEnabled(false);
                    break;
                case "moneda":
                    ViewReturn = Inflater.inflate(R.layout.item_numero, null);
                    TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
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
