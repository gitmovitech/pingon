package cl.pingon.Adapter;


import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public abstract class AdapterChecklist extends BaseAdapter {

    private Context context;
    private ArrayList<ModelChecklistFields> ChecklistFields;
    private int contador = 0;

    private TextView Texto;
    TextInputLayout TextoInput;
    TextView TextViewLabel;
    Button ButtonFoto;
    TextView TextViewTitle;
    EditText NumeroInput;
    Spinner SpinnerSelect;

    public AdapterChecklist(Context context, ArrayList<ModelChecklistFields> ChecklistFields){
        this.ChecklistFields = ChecklistFields;
        this.context = context;
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
                    break;
                case "foto":
                    ViewReturn = Inflater.inflate(R.layout.item_foto, null);
                    ButtonFoto = (Button) ViewReturn.findViewById(R.id.item_foto);
                    ButtonFoto.setText(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
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

    private View Lista(LayoutInflater Inflater, ModelChecklistFields Fields){
        View view = Inflater.inflate(R.layout.item_select, null);
        TextViewTitle = (TextView) view.findViewById(R.id.TextViewLabel);
        TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());
        SpinnerSelect = (Spinner) view.findViewById(R.id.SpinnerSelect);
        return view;
    }
}
