package cl.pingon.Fields;


import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsText {

    View view;

    public FieldsText(LayoutInflater Inflater, ModelChecklistFields Fields, int RowItemIndex){
        this.view = Inflater.inflate(R.layout.item_texto, null);

        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        if(Fields.getCAM_VAL_DEFECTO() != null){
            EditText TextoInput = (EditText) view.findViewById(R.id.texto_input);
            TextoInput.setText(Fields.getCAM_VAL_DEFECTO());
        }
        Fields.setView(view);
    }

    public View getView(){
        return this.view;
    }
}
