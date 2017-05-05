package cl.pingon.Fields;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;
import cl.pingon.SQLite.TblListOptionsDefinition;
import cl.pingon.SQLite.TblListOptionsHelper;

public class FieldsFecha {

    View view;

    public FieldsFecha(final Context context, LayoutInflater Inflater, ModelChecklistFields Fields){

        this.view = Inflater.inflate(R.layout.item_fecha, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch(Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }
        Button Button = (Button) view.findViewById(R.id.button_fecha);
        final EditText EditTextFecha = (EditText) view.findViewById(R.id.fecha_input);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog DatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        EditTextFecha.setText(String.format("%02d",day)+"-"+String.format("%02d",month+1)+"-"+String.format("%02d",year));
                    }
                }, year, month, day);
                DatePickerDialog.show();
            }
        });

        /**
         * AGREGAR EL DATO POR DEFECTO GUARDADO
         */
        if(!Fields.getCAM_VAL_DEFECTO().isEmpty()){
            try {
                EditTextFecha.setText(Fields.getCAM_VAL_DEFECTO());
            } catch (Exception e){
                Log.e("ERROR CAMPO VACIO", e.toString());
            }
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
