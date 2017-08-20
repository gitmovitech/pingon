package cl.pingon.Fields;


import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import cl.pingon.Libraries.DateUtils;
import cl.pingon.Model.ModelChecklist;
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

        //HORA SEMANAL NORMAL
        if(Fields.getCAM_TIPO().equals("hora_total_semanal")){
            DateUtils dateutils = new DateUtils();
            TblRegistroHelper Registros = new TblRegistroHelper(context);
            Cursor c = Registros.getByFrmId(FRM_ID);

            int minutos_totales = 0;
            boolean dia_habil = false;
            while(c.moveToNext()) {

                if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).equals("dia_habil")){
                    if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)).equals("Si")){
                        dia_habil = true;
                    } else {
                        dia_habil = false;
                    }
                }
                if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).equals("hora_total_diaria") && dia_habil){
                    String tiempo = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                    int minutos = dateutils.ObtenerMinutos(tiempo);
                    int minutos_diferencia = 0;
                    if(minutos > (9*60)){
                        minutos_diferencia = minutos - (9*60);
                    }
                    minutos_totales += minutos-minutos_diferencia;
                }
            }
            NumeroInput.setText(dateutils.MinutosHora(minutos_totales));

            c.close();
            Registros.close();
        }

        //HORA SEMANAL EXTRA
        if(Fields.getCAM_TIPO().equals("hora_total_semanal_extra")){
            DateUtils dateutils = new DateUtils();
            TblRegistroHelper Registros = new TblRegistroHelper(context);
            Cursor c = Registros.getByFrmId(FRM_ID);

            int minutos_totales = 0;
            int minutos_extra = 0;
            boolean dia_habil = false;
            while(c.moveToNext()) {
                /*if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).equals("dia_habil")){
                    if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)).equals("Si")){
                        dia_habil = true;
                    } else {
                        dia_habil = false;
                    }
                }*/
                if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).equals("hora_total_diaria_extra")){
                    String tiempo = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                    int minutos = dateutils.ObtenerMinutos(tiempo);
                    if(!dia_habil){
                        minutos_totales += minutos;
                    } else {
                        if(minutos > (9*60)){
                            minutos_extra = minutos - (9*60);
                        }
                    }
                    minutos_totales += minutos_extra;
                }
                /*if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).equals("hora_colacion")){
                    if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)).equals("No")){
                        minutos_totales += 60;
                    }
                }*/
            }
            NumeroInput.setText(dateutils.MinutosHora(minutos_totales));

            c.close();
            Registros.close();
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
