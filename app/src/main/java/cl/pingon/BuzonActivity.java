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

        /**
         * UNIT TEST
         */
        Intent IntentInformes = new Intent(this, InformesActivity.class);
        IntentInformes.putExtra("DOC_EXT_ID_CLIENTE", "308");
        IntentInformes.putExtra("DOC_EXT_NOMBRE_CLIENTE", "Renta Equipos Simunovic SPA");
        IntentInformes.putExtra("DOC_EXT_ID_PROYECTO", "222");
        IntentInformes.putExtra("DOC_EXT_OBRA", "Simunovic SPA");
        IntentInformes.putExtra("DOC_EXT_EQUIPO", "J 50.10");
        IntentInformes.putExtra("DOC_EXT_MARCA_EQUIPO", "Jaso");
        IntentInformes.putExtra("DOC_EXT_NUMERO_SERIE", "1017");
        startActivity(IntentInformes); finish();

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
    }
}
