package cl.pingon.Fields;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsBinario {

    View view;

    public FieldsBinario(LayoutInflater Inflater, ModelChecklistFields Fields){

        this.view = Inflater.inflate(R.layout.item_radio, null);
        TextView TextViewTitle = (TextView) view.findViewById(R.id.radio_label);
        try {
            TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }

        Fields.setView(view);

    }

    public View getView(){
        return this.view;
    }
}
