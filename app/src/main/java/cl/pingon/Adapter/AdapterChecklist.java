package cl.pingon.Adapter;


import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public abstract class AdapterChecklist extends BaseAdapter {

    private Context context;
    private ArrayList<ModelChecklistFields> ChecklistFields;
    private int contador = 0;

    private TextView Texto;
    private EditText TextoInput;

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
                    TextInputLayout TextoInput = (TextInputLayout) ViewReturn.findViewById(R.id.texto_input_layout);
                    TextoInput.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                default:
                    ViewReturn = Inflater.inflate(R.layout.item_title, null);
                    TextView TextViewTitle = (TextView) ViewReturn.findViewById(R.id.TextViewTitle);
                    TextViewTitle.setText(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    Log.d("CAM_TIPO", ChecklistFields.get(contador).getCAM_TIPO());
                    break;
            }

        }
        contador++;

        return ViewReturn;
    }
}
