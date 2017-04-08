package cl.pingon.Fields;


import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsEmail {
    View view;

    public FieldsEmail(LayoutInflater Inflater, ModelChecklistFields Fields){
        this.view = Inflater.inflate(R.layout.item_email, null);

        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        try {
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }
        if(Fields.getCAM_VAL_DEFECTO() != null){
            EditText TextoInput = (EditText) view.findViewById(R.id.texto_input);
            TextoInput.setText(Fields.getCAM_VAL_DEFECTO());
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
