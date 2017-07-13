package cl.pingon.Libraries;


import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class CalculateHours {
    public CalculateHours(ArrayList<ModelChecklistFields> ChecklistFields){
        View viewFields;
        String hora_entrada = "";
        String hora_salida = "";
        Boolean hora_colacion = false;
        Boolean dia_habil = true;
        EditText HoraTotalDiariaText = null;
        EditText HoraTotalDiariaExtraText = null;
        for(int x = 0; x < ChecklistFields.size();x++){
            viewFields = ChecklistFields.get(x).getView();
            switch(ChecklistFields.get(x).getCAM_TIPO()){
                case "dia_habil":
                    try {
                        RadioButton DiaHabiliSi = (RadioButton) viewFields.findViewById(R.id.radio_si);
                        RadioButton DiaHabiliNo = (RadioButton) viewFields.findViewById(R.id.radio_no);
                        if (DiaHabiliSi.isChecked()) {
                            dia_habil = true;
                        }
                        if (DiaHabiliNo.isChecked()) {
                            dia_habil = false;
                        }
                    } catch (Exception e){
                        Log.e("ERROR DIA HABIL", e.toString());
                    }
                    break;
                case "hora_entrada":
                    try {
                        EditText HoraEntradaText = (EditText) viewFields.findViewById(R.id.hora_input);
                        hora_entrada = HoraEntradaText.getText().toString();
                    } catch (Exception e){
                        Log.e("ERROR HORA ENTRADA", e.toString());
                    }
                    break;
                case "hora_salida":
                    try {
                        EditText HoraSalidaText = (EditText) viewFields.findViewById(R.id.hora_input);
                        hora_salida = HoraSalidaText.getText().toString();
                    } catch (Exception e){
                        Log.e("ERROR HORA SALIDA", e.toString());
                    }
                    break;
                case "hora_colacion":
                    try {
                        RadioButton HoraColacionSi = (RadioButton) viewFields.findViewById(R.id.radio_si);
                        RadioButton HoraColacionNo = (RadioButton) viewFields.findViewById(R.id.radio_no);
                        if (HoraColacionSi.isChecked()) {
                            hora_colacion = true;
                        }
                        if (HoraColacionNo.isChecked()) {
                            hora_colacion = false;
                        }
                    } catch (Exception e){
                        Log.e("ERROR HORA COLACION", e.toString());
                    }
                    break;
                case "hora_total_diaria":
                    try {
                        HoraTotalDiariaText = (EditText) viewFields.findViewById(R.id.numero_input);
                    } catch (Exception e){
                        Log.e("ERROR HORA DIARIA", e.toString());
                    }
                    break;
                case "hora_total_diaria_extra":
                    try {
                        HoraTotalDiariaExtraText = (EditText) viewFields.findViewById(R.id.numero_input);
                    } catch (Exception e){
                        Log.e("ERROR HORA DIARIA EXTRA", e.toString());
                    }
                    break;
            }
        }
        DateUtils dateutils = new DateUtils();

        if(dia_habil){
            try{
                HoraTotalDiariaText.setText(dateutils.HoursDiference(hora_entrada, hora_salida, hora_colacion));
                HoraTotalDiariaExtraText.setText("00:00");
            } catch (Exception e){
                Log.e("ERRR", e.toString());
            }
        } else {
            try{
                HoraTotalDiariaExtraText.setText(dateutils.HoursDiference(hora_entrada, hora_salida, hora_colacion));
                HoraTotalDiariaText.setText("00:00");
            } catch (Exception e){
                Log.e("ERRR", e.toString());
            }

        }

        /*int minutos = dateutils.ObtenerMinutos(dateutils.HoursDiference(hora_entrada, hora_salida, hora_colacion));

        Log.d("MIONUTOS", ":"+minutos/60);
        Log.d("MIONUTOS", ":"+dateutils.ObtenerMinutos(hora_salida));

        //int minutos = dateutils.ObtenerMinutos(;
        Log.d("MIONUTOS", ":"+dateutils.HoursDiference(hora_entrada, hora_salida, hora_colacion));*/
    }
}
