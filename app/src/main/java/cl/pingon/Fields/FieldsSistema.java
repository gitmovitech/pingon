package cl.pingon.Fields;


import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsSistema {

    View view;

    public FieldsSistema(LayoutInflater Inflater, ModelChecklistFields Fields){

        this.view = Inflater.inflate(R.layout.item_numero, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        EditText NumeroInput = (EditText) view.findViewById(R.id.numero_input);
        NumeroInput.setEnabled(false);

        Fields.setView(view);

    }

    public View getView(){
        return this.view;
    }
}
