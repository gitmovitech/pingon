package cl.pingon.Fields;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Libraries.CalculateHours;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsBinario {

    View view;

    public FieldsBinario(LayoutInflater Inflater, ModelChecklistFields Fields, final ArrayList<ModelChecklistFields> ChecklistFields){

        this.view = Inflater.inflate(R.layout.item_radio, null);
        TextView TextViewTitle = (TextView) view.findViewById(R.id.radio_label);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }

        /**
         * Autocompletar valor por defecto
         */
        if(!Fields.getCAM_VAL_DEFECTO().isEmpty()){
            RadioButton rb1 = (RadioButton) this.view.findViewById(R.id.radio_si);
            RadioButton rb2 = (RadioButton) this.view.findViewById(R.id.radio_no);
            if(Fields.getCAM_VAL_DEFECTO().contains("Si")){
                rb1.setChecked(true);
                rb2.setChecked(false);
            }
            if(Fields.getCAM_VAL_DEFECTO().contains("No")){
                rb1.setChecked(false);
                rb2.setChecked(true);
            }
            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CalculateHours(ChecklistFields);
                }
            });
            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CalculateHours(ChecklistFields);
                }
            });
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
