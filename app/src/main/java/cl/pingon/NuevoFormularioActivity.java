package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NuevoFormularioActivity extends AppCompatActivity {

    Spinner SpinnerClientes;
    Spinner SpinnerObras;
    Spinner SpinnerMarca;
    Spinner SpinnerEquipo;
    Spinner SpinnerSerie;
    Intent IntentInformes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_formulario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        this.setTitle("Nuevo formulario");

        IntentInformes = new Intent(this, InformesActivity.class);

        final String[] list = {"Constructora Belmar Y Ribba Limitada","Constructora Belmar Y Ribba Limitada","Constructora Belmar Y Ribba Limitada" };
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        SpinnerClientes = (Spinner) findViewById(R.id.SpinnerClientes);
        SpinnerObras = (Spinner) findViewById(R.id.SpinnerObras);
        SpinnerMarca = (Spinner) findViewById(R.id.SpinnerMarca);
        SpinnerEquipo = (Spinner) findViewById(R.id.SpinnerEquipo);
        SpinnerSerie = (Spinner) findViewById(R.id.SpinnerSerie);

        SpinnerClientes.setAdapter(listAdapter);
        SpinnerObras.setAdapter(listAdapter);
        SpinnerMarca.setAdapter(listAdapter);
        SpinnerEquipo.setAdapter(listAdapter);
        SpinnerSerie.setAdapter(listAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentInformes);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

}
