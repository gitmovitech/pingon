package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BuzonActivity extends AppCompatActivity {

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

        IntentNuevo = new Intent(this, NuevoFormularioActivity.class);
        IntentBorradores = new Intent(this, BorradoresActivity.class);
        IntentPendientes = new Intent(this, PendientesEnvioActivity.class);
        IntentEnviados = new Intent(this, EnviadosActivity.class);

        Button ButtonNuevo = (Button) findViewById(R.id.ButtonNuevoInforme);
        Button ButtonBorradores = (Button) findViewById(R.id.ButtonBorradores);
        Button ButtonPendientes = (Button) findViewById(R.id.ButtonPendientesEnvio);
        Button ButtonEnviados = (Button) findViewById(R.id.ButtonEnviados);

        ButtonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentNuevo);
            }
        });
        ButtonBorradores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentBorradores);
            }
        });
        ButtonPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentPendientes);
            }
        });
        ButtonEnviados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentEnviados);
            }
        });

        try {
            if (getIntent().getStringExtra("GOTO").contains("PendientesEnvioActivity")) {
                //TODO Probar redireccion a enviados.
                startActivity(IntentPendientes);
            }
        } catch (Exception e){}
    }
}
