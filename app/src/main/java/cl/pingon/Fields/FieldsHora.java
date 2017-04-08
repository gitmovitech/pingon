package cl.pingon.Fields;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsHora {
    View view;

    public FieldsHora(final Context context, LayoutInflater Inflater, ModelChecklistFields Fields){

        this.view = Inflater.inflate(R.layout.item_hora, null);
        TextInputLayout TextoInputLayout = (TextInputLayout) view.findViewById(R.id.texto_input_layout);
        TextoInputLayout.setHint(Fields.getCAM_NOMBRE_INTERNO());
        Button Button = (Button) view.findViewById(R.id.button_hora);
        final EditText EditTextHora = (EditText) view.findViewById(R.id.hora_input);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog TimePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        EditTextHora.setText(hour+":"+minute);
                    }
                },hour, minute, true);
                TimePickerDialog.show();
            }
        });
        Fields.setView(view);

    }

    public View getView(){
        return this.view;
    }
}