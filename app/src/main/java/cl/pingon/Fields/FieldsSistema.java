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

public class FieldsSistema {

    View view;

    public FieldsSistema(LayoutInflater Inflater, ModelChecklistFields Fields, ArrayList<ModelChecklistFields> ArrayFields){

        this.view = Inflater.inflate(R.layout.item_numero, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }
        EditText NumeroInput = (EditText) view.findViewById(R.id.numero_input);
        NumeroInput.setEnabled(false);

        /**
         * Autocompletar sistema
         */
        /*if(!Fields.getCAM_VAL_DEFECTO().isEmpty()){
            String[] text = Fields.getCAM_VAL_DEFECTO().split(" ");
            int resultado = 1;
            for(int t = 0; t < text.length; t++){
                String[] number = text[t].split("-");
                for(int a = 0; a < ArrayFields.size(); a++){
                    if(ArrayFields.get(a).getCAM_ID() == Integer.parseInt(number[1])){
                        resultado = resultado * Integer.parseInt(ArrayFields.get(a).getCAM_VAL_DEFECTO());
                        break;
                    }
                }
            }
            if(resultado == 1){
                resultado = 0;
            }
            NumeroInput.setText(String.valueOf(resultado));
        }*/

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
