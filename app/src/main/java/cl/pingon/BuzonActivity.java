package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BuzonActivity extends AppCompatActivity {

    ListView ListadoBuzon;
    Intent IntentNuevo;
    Intent IntentBorradores;
    Intent IntentPendientes;
    Intent IntentEnviados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        final String[] ListItems = {"Nuevo formulario", "Borradores", "Pendientes de envío", "Formularios enviados" };
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListItems);

        IntentNuevo = new Intent(this, NuevoFormularioActivity.class);

        ListadoBuzon = (ListView) findViewById(R.id.ListadoBuzon);
        ListadoBuzon.setAdapter(listAdapter);

        ListadoBuzon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                switch(index){
                    case 0:
                        startActivity(IntentNuevo);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });
    }
}
