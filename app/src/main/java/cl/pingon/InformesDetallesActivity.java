package cl.pingon;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class InformesDetallesActivity extends AppCompatActivity {

    private View ViewFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes_detalles);
        this.setTitle("CUSTOM FIELDS");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        //SOLUCION A LOS DIFERENTES TIPOS DE ITEMS http://android.amberfog.com/?p=296

        LinearLayout LinearLayoutInformesDetalles = (LinearLayout) findViewById(R.id.LinearLayoutInformesDetalles);
        LayoutInflater Inflater = LayoutInflater.from(this);
        View ItemFechaView = Inflater.inflate(R.layout.item_fecha, null);
        View ItemHoraView = Inflater.inflate(R.layout.item_hora, null);
        View ItemSelectView = Inflater.inflate(R.layout.item_select, null);
        View ItemTextareaView = Inflater.inflate(R.layout.item_textarea, null);
        View ItemNumeroView = Inflater.inflate(R.layout.item_numero, null);
        View ItemEmailView = Inflater.inflate(R.layout.item_email, null);
        View ItemRadioView = Inflater.inflate(R.layout.item_radio, null);
        View ItemFotoView = Inflater.inflate(R.layout.item_foto, null);
        View ItemVideoView = Inflater.inflate(R.layout.item_video, null);
        View ItemAudioView = Inflater.inflate(R.layout.item_audio, null);
        View ItemFirmaView = Inflater.inflate(R.layout.item_firma, null);

        LinearLayoutInformesDetalles.addView(ItemFechaView);
        LinearLayoutInformesDetalles.addView(ItemHoraView);
        LinearLayoutInformesDetalles.addView(ItemSelectView);
        LinearLayoutInformesDetalles.addView(ItemTextareaView);
        LinearLayoutInformesDetalles.addView(ItemNumeroView);
        LinearLayoutInformesDetalles.addView(ItemEmailView);
        LinearLayoutInformesDetalles.addView(ItemRadioView);
        LinearLayoutInformesDetalles.addView(ItemFotoView);
        LinearLayoutInformesDetalles.addView(ItemVideoView);
        LinearLayoutInformesDetalles.addView(ItemAudioView);
        LinearLayoutInformesDetalles.addView(ItemFirmaView);
    }
}
