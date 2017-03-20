package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Model.Informes;

public class ReemplazoTabsActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent IntentDetalle;
    Informes Informes;
    ArrayList<Informes> ArrayInformes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reemplazo_tabs);
        getSupportActionBar();
        this.setTitle(getIntent().getStringExtra("InformeTitle"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("InformeSubtitle"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        IntentDetalle = new Intent(this, InformesDetallesActivity.class);
        IntentDetalle.putExtras(getIntent().getExtras());

        final String[] ListItems = {"General", "Detalle", "Multimedia", "Cliente", };
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListItems);

        ListView Listado = (ListView) findViewById(R.id.list);
        Listado.setAdapter(listAdapter);

        Listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                IntentDetalle.putExtra("TabSeccion",ListItems[index]);
                startActivity(IntentDetalle);
            }
        });
    }
}