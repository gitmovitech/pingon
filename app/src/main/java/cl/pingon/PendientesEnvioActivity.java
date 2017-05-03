package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Model.Informes;

public class PendientesEnvioActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent IntentDetalle;
    Informes Informes;
    ArrayList<Informes> ArrayInformes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes_envio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Pendientes de envío");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        IntentDetalle = new Intent(this, InformesTabsActivity.class);

        ArrayInformes = new ArrayList<Informes>();
        /*Informes = new Informes("Producción", "Asistencia Técnica Grúas", "1");
        ArrayInformes.add(Informes);
        Informes = new Informes("Producción", "Orden de Trabajo Elevadores", "2");
        ArrayInformes.add(Informes);
        Informes = new Informes("Producción", "Informe Grúa Auxiliar", "3");
        ArrayInformes.add(Informes);*/


        ListDetalle = (ListView) findViewById(R.id.ListDetalle);
        ListDetalle.setAdapter(new AdapterInformes(this, ArrayInformes) {});

        ListDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentDetalle.putExtra("InformeId",ArrayInformes.get(i).getId());
                IntentDetalle.putExtra("InformeTitle",ArrayInformes.get(i).getTitle());
                IntentDetalle.putExtra("InformeSubtitle",ArrayInformes.get(i).getSubtitle());
                startActivity(IntentDetalle);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }
}
