package cl.pingon.Fields;


import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsSistema{

    View view;
    EditText NumeroInput;

    public FieldsSistema(LayoutInflater Inflater, ModelChecklistFields Fields, ArrayList<ModelChecklistFields> ArrayFields){

        this.view = Inflater.inflate(R.layout.item_numero, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        NumeroInput = (EditText) view.findViewById(R.id.numero_input);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }
        NumeroInput.setEnabled(false);

        /**
         * Autocompletar sistema
         */
        if(!Fields.getCAM_VAL_DEFECTO().isEmpty()){
            NumeroInput.setText(Fields.getCAM_VAL_DEFECTO());
        }

        //TODO probar campo sistema

        try {
            Fields.setView(view);
        } catch (Exception e){
            Log.e("ERROR VIEW", e.toString());
        }

    }

    public View getView(){
        return this.view;
    }

}
