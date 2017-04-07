package cl.pingon.Fields;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsTitle {

    View view;

    public FieldsTitle(LayoutInflater Inflater, ModelChecklistFields Fields){
        this.view = Inflater.inflate(R.layout.item_title, null);
        TextView TextViewTitle = (TextView) view.findViewById(R.id.TextViewTitle);
        TextViewTitle.setText(Fields.getCAM_NOMBRE_INTERNO());
        Fields.setView(view);
    }

    public View getView(){
        return this.view;
    }
}
