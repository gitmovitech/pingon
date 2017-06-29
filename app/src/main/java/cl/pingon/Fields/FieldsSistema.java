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
import java.util.Calendar;

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
        if(Fields.getCAM_TIPO().contains("hora_total_semanal") || Fields.getCAM_TIPO().contains("hora_total_semanal_extra")){
            TblRegistroHelper Registros = new TblRegistroHelper(context);
            Cursor c = Registros.getByFrmId(FRM_ID);

            int horas = 0;
            int minutos = 0;
            int horas_extras = 0;
            int minutos_extras = 0;
            String hora_string = "";
            String[] hora_arr;
            String[] fecha_arr;
            String last_fecha = "";
            String last_value = "";

            while(c.moveToNext()){


                last_value = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));

                //Buscar fecha para saber si es sabado o domingo
                fecha_arr = last_value.split("-");
                if(fecha_arr.length == 3){
                    if(fecha_arr[2].length() == 4){
                        last_fecha = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                    }
                }

                if(c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)).contains("hora_total_diaria")){
                    if(!c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)).isEmpty()){

                        hora_string = c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR));
                        hora_arr = hora_string.split(":");

                        if(last_fecha.isEmpty()) {
                            horas += Integer.parseInt(hora_arr[0]);
                            minutos += Integer.parseInt(hora_arr[1]);
                        } else {
                            fecha_arr = last_fecha.split("-");
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, Integer.parseInt(fecha_arr[2]));
                            cal.set(Calendar.MONTH, Integer.parseInt(fecha_arr[1])-1);
                            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha_arr[0]));

                            //Sabado o domingo para sumar horas extras
                            if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                                horas_extras += Integer.parseInt(hora_arr[0]);
                                minutos_extras += Integer.parseInt(hora_arr[1]);
                            } else {
                                horas += Integer.parseInt(hora_arr[0]);
                                minutos += Integer.parseInt(hora_arr[1]);
                            }
                            last_fecha = "";
                        }
                    }
                }
            }
            c.close();
            Registros.close();

            DateUtils dateutils = new DateUtils();
            if(Fields.getCAM_TIPO().contains("hora_total_semanal")){
                NumeroInput.setText(dateutils.AproximarHora(horas+":"+minutos));
            }
            if(Fields.getCAM_TIPO().contains("hora_total_semanal_extra")){
                int minutos_extra = dateutils.ObtenerMinutos(horas+":"+minutos);
                if(minutos_extra <= 2700){
                    minutos_extra = dateutils.ObtenerMinutos(horas_extras+":"+minutos_extras);
                    NumeroInput.setText(dateutils.MinutosHora(minutos_extra));
                } else {
                    minutos_extra -= 2700;
                    minutos_extra += dateutils.ObtenerMinutos(horas_extras+":"+minutos_extras);
                    NumeroInput.setText(dateutils.MinutosHora(minutos_extra));
                }

            }
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
