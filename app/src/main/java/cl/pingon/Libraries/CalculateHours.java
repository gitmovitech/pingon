package cl.pingon.Libraries;


import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class CalculateHours {
    public CalculateHours(ArrayList<ModelChecklistFields> ChecklistFields){
        View viewFields;
        String hora_entrada = "";
        String hora_salida = "";
        Boolean hora_colacion = false;
        EditText HoraTotalDiariaText = null;
        for(int x = 0; x < ChecklistFields.size();x++){
            viewFields = ChecklistFields.get(x).getView();
            switch(ChecklistFields.get(x).getCAM_TIPO()){
                case "hora_entrada":
                    try {
                        EditText HoraEntradaText = (EditText) viewFields.findViewById(R.id.hora_input);
                        hora_entrada = HoraEntradaText.getText().toString();
                    } catch (Exception e){}
                    break;
                case "hora_salida":
                    try {
                        EditText HoraSalidaText = (EditText) viewFields.findViewById(R.id.hora_input);
                        hora_salida = HoraSalidaText.getText().toString();
                    } catch (Exception e){}
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
                    } catch (Exception e){}
                    break;
                case "hora_total_diaria":
                    try {
                        HoraTotalDiariaText = (EditText) viewFields.findViewById(R.id.numero_input);
                    } catch (Exception e){}
                    break;
            }
        }
        try {
            DateUtils dateutils = new DateUtils();
            HoraTotalDiariaText.setText(dateutils.HoursDiference(hora_entrada, hora_salida, hora_colacion));
        } catch (Exception e){

        }
    }
}
