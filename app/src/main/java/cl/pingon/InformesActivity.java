package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Model.Informes;

public class InformesActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent IntentDetalle;
    Informes Informes;
    ArrayList<Informes> ArrayInformes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);
        this.setTitle("Informes");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ArrayInformes = new ArrayList<Informes>();
        Informes = new Informes("Empresa 2", "Labor 2");
        ArrayInformes.add(Informes);
        Informes = new Informes("Empresa 4", "Labor 4");
        ArrayInformes.add(Informes);
        Informes = new Informes("Empresa N", "Labor N");
        ArrayInformes.add(Informes);


        ListDetalle = (ListView) findViewById(R.id.ListDetalle);
        ListDetalle.setAdapter(new AdapterInformes(this, ArrayInformes) {});
    }
}
