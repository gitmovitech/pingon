package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class BuzonActivity extends AppCompatActivity {

    Intent IntentNuevo;
    Intent IntentBorradores;
    Intent IntentPendientes;
    Intent IntentEnviados;
    Intent IntentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        this.setTitle(getResources().getString(R.string.app_name)+" "+getResources().getString(R.string.app_version));

        IntentNuevo = new Intent(this, NuevoFormularioActivity.class);
        IntentBorradores = new Intent(this, BorradoresActivity.class);
        IntentPendientes = new Intent(this, PendientesEnvioActivity.class);
        IntentEnviados = new Intent(this, EnviadosActivity.class);
        IntentProfile = new Intent(this, ProfileActivity.class);

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

        Intent IntentSyncListenerService = new Intent(this, SyncService.class);
        startService(IntentSyncListenerService);

        try{
            InformesTabsActivity.activity.finish();
        } catch (Exception e){}
        try{
            BorradoresActivity.activity.finish();
        } catch (Exception e){}
        try{
            InformesActivity.activity.finish();
        } catch (Exception e){}
        try{
            InformesActivity.activity.finish();
        } catch (Exception e){}
        try{
            NuevoFormularioActivity.activity.finish();
        } catch (Exception e){}
        try{
            if (getIntent().getStringExtra("GOTO").equals("PendientesEnvioActivity")) {
                finish();
                startActivity(IntentPendientes);
            }
        } catch (Exception e){}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buzon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                try{
                    IntentProfile.putExtras(getIntent().getExtras());
                } catch (Exception e){

                }
                startActivityForResult(IntentProfile, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {

        }
    }
}
