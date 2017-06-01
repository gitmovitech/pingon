package cl.pingon.Fields;


import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.TextView;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public class FieldsSliderbar {

    View view;

    public FieldsSliderbar(LayoutInflater Inflater, ModelChecklistFields Fields){
        this.view = Inflater.inflate(R.layout.item_sliderbar, null);

        SeekBar Seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        final TextView text = (TextView) view.findViewById(R.id.seekBarValue);
        TextView label = (TextView) view.findViewById(R.id.label);

        Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        try {
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView obli = (TextView) view.findViewById(R.id.label_obligatorio);
                obli.setVisibility(view.VISIBLE);
            }
            label.setText(Fields.getCAM_NOMBRE_INTERNO());
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }
        if(Fields.getCAM_VAL_DEFECTO() != null){
            if(Fields.getCAM_VAL_DEFECTO().isEmpty()){
                Seekbar.setProgress(0);
                text.setText("0");
            } else {
                try {
                    Seekbar.setProgress(Integer.parseInt(Fields.getCAM_VAL_DEFECTO()));
                    text.setText(Fields.getCAM_VAL_DEFECTO());
                } catch(Exception e){
                    Log.d("ERROR CAMPO DEFECTO", ":"+Fields.getCAM_VAL_DEFECTO());
                }
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
