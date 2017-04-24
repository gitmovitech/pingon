package cl.pingon.Fields;

import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsMoneda {

    View view;

    public FieldsMoneda(LayoutInflater Inflater, ModelChecklistFields Fields, final ArrayList<ModelChecklistFields> ArrayFields){

        this.view = Inflater.inflate(R.layout.item_numero, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        EditText text = (EditText) this.view.findViewById(R.id.numero_input);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }


        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String[] array;
                int index = 0;
                int sistema = 0;
                ArrayList<String> results = new ArrayList<String>();
                for(int a = 0; a < ArrayFields.size(); a++){
                    if(ArrayFields.get(a).getCAM_TIPO().contains("sistema")){
                        index = a;
                        sistema = 1;
                        array = ArrayFields.get(a).getSISTEMA().split(" ");
                        for(int b = 0; b < array.length; b++) {
                            String[] items = array[b].split("-");
                            results.add(items[1]);
                        }
                        break;
                    }
                }
                int valor = 1;
                int entro = 0;
                for(int a = 0; a < ArrayFields.size(); a++){
                    for(int b = 0; b < results.size(); b++){
                        if(results.get(b).contains(String.valueOf(ArrayFields.get(a).getCAM_ID()))){
                            View view = ArrayFields.get(a).getView();
                            EditText campo = (EditText) view.findViewById(R.id.numero_input);
                            try {
                                valor = valor * Integer.parseInt(campo.getText().toString());
                                entro = 1;
                            } catch (Exception e){}
                        }
                    }
                }

                if(entro == 0) {
                    valor = 0;
                }
                if(sistema == 1) {
                    View view = ArrayFields.get(index).getView();
                    EditText campo = (EditText) view.findViewById(R.id.numero_input);
                    campo.setText(String.valueOf(valor));
                }

                return false;
            }
        });


        /**
         * Autocompletar moneda
         */
        if(!Fields.getCAM_VAL_DEFECTO().isEmpty()){
            text.setText(Fields.getCAM_VAL_DEFECTO());
        }

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
