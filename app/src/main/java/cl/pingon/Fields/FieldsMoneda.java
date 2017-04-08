package cl.pingon.Fields;

import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsMoneda {

    View view;

    public FieldsMoneda(LayoutInflater Inflater, ModelChecklistFields Fields){

        this.view = Inflater.inflate(R.layout.item_numero, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());

        Fields.setView(view);

    }

    public View getView(){
        return this.view;
    }
}
