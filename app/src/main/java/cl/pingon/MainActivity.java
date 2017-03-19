package cl.pingon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblAreaNegocioHelper;

public class MainActivity extends AppCompatActivity {

    Intent intentLogin;
    public static Activity activity;
    SharedPreferences session;
    RESTService REST;
    AlertDialog.Builder alert;
    TblAreaNegocioHelper AreaNegocio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        activity = this;
        alert = new AlertDialog.Builder(this);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        REST = new RESTService(this);

        if(session.getString("token","") != "") {
            SyncAreaNegocio();
        }
    }

    private void SyncAreaNegocio(){
        AreaNegocio = new TblAreaNegocioHelper(this);
        final Cursor CursorAreaNegocio = AreaNegocio.getAll();
        HashMap<String, String> headers = new HashMap<>();
        REST.get(getResources().getString(R.string.url_sync_area_negocio).toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(CursorAreaNegocio.getCount() == 0){
                    alert.setTitle("Error de sincronización");
                    alert.setMessage("Ha habido un error de conexión con el servidor. Si el problema persiste contáctese con nosotros.");
                    alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            System.exit(0);
                            finish();
                        }
                    });
                    alert.create();
                    alert.show();
                }
            }
        }, headers);

        /*ContentValues values = new ContentValues();
            values.put("ARN_ID", 2);
            values.put("ARN_NOMBRE", "Tu casa");
            values.put("activo", 1);
            AreaNegocio.insert(values);*/

            /*Cursor cursor = AreaNegocio.getAll();

            while(cursor.moveToNext()) {
                String item = cursor.getString(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                Log.d("ARN_NOMBRE", item.toString());
            }
            cursor.close();*/
    }

}
