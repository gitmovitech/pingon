package cl.pingon.Fields;


import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Libraries.DateUtils;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

public class FieldsSistema{

    View view;
    EditText NumeroInput;

    public FieldsSistema(LayoutInflater Inflater, ModelChecklistFields Fields, Integer FRM_ID, Context context){

        this.view = Inflater.inflate(R.layout.item_numero, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        NumeroInput = (EditText) view.findViewById(R.id.numero_input);
        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
            TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }
        NumeroInput.setEnabled(false);

        /**
         * Autocompletar sistema
         */
        if(!Fields.getCAM_VAL_DEFECTO().isEmpty()){
            NumeroInput.setText(Fields.getCAM_VAL_DEFECTO());
        }

        /**
         * CALCULO SEMANAL DE HORAS
         */
        if(Fields.getCAM_TIPO().contains("hora_total_semanal")){
            TblRegistroHelper Registros = new TblRegistroHelper(context);
            Cursor c = Registros.getByFrmId(FRM_ID);

            int horas = 0;
            int minutos = 0;
            String hora_string = "";
            String[] hora_arr;

            while(c.moveToNext()){
                if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).contains("hora_total_diaria")){
                    if(!c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)).isEmpty()){
                        hora_string = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                        hora_arr = hora_string.split(":");
                        Log.d("HORA FIELD", hora_string);
                        horas += Integer.parseInt(hora_arr[0]);
                        minutos += Integer.parseInt(hora_arr[1]);
                    }
                }
            }
            c.close();
            Registros.close();
            Log.d("HORA FIELD", horas+":"+minutos);

            DateUtils dateutils = new DateUtils();
            NumeroInput.setText(dateutils.AproximarHora(horas+":"+minutos));
        }

        //TODO probar campo sistema

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
