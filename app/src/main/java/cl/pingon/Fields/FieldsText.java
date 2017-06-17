package cl.pingon.Fields;


import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsText {

    View view;

    public FieldsText(LayoutInflater Inflater, ModelChecklistFields Fields, int RowItemIndex, String type){
        this.view = Inflater.inflate(R.layout.item_texto, null);

        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
            if(Fields.getCAM_VAL_DEFECTO() != null){
                EditText TextoInput = (EditText) view.findViewById(R.id.texto_input);
                TextoInput.setText(Fields.getCAM_VAL_DEFECTO());
            }
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
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
