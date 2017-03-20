package cl.pingon;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblAreaNegocioDefinition;
import cl.pingon.SQLite.TblAreaNegocioHelper;

public class MainActivity extends AppCompatActivity {

    Intent IntentBuzon;
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

        IntentBuzon = new Intent(this, BuzonActivity.class);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        REST = new RESTService(this);

        if(session.getString("token","") != "") {
            //SyncAreaNegocio();
            startActivity(IntentBuzon);
            finish();

        }
        startActivity(IntentBuzon);
        finish();
    }

    private void SyncAreaNegocio(){
        AreaNegocio = new TblAreaNegocioHelper(this);
        final Cursor CursorAreaNegocio = AreaNegocio.getAll();
        HashMap<String, String> headers = new HashMap<>();
        String url = getResources().getString(R.string.url_sync_area_negocio).toString()+"/"+session.getString("token","");
        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("ok") == 1){

                        JSONArray data = (JSONArray) response.get("data");
                        JSONObject item;
                        Integer ARN_ID = null;
                        String ARN_NOMBRE = null;
                        Integer ACTIVO = null;
                        Boolean addItem;
                        ContentValues values;

                        for(int i = 0;i < data.length(); i++){
                            item = (JSONObject) data.get(i);
                            addItem = true;
                            while(CursorAreaNegocio.moveToNext()) {
                                ARN_ID = CursorAreaNegocio.getInt(CursorAreaNegocio.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_ID));
                                ARN_NOMBRE = CursorAreaNegocio.getString(CursorAreaNegocio.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                                ACTIVO = CursorAreaNegocio.getInt(CursorAreaNegocio.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ACTIVO));
                                if(ARN_ID == item.getInt(TblAreaNegocioDefinition.Entry.ARN_ID)){
                                    addItem = false;

                                    values = new ContentValues();
                                    if(ARN_NOMBRE != item.getString(TblAreaNegocioDefinition.Entry.ARN_NOMBRE)){
                                        values.put(TblAreaNegocioDefinition.Entry.ARN_NOMBRE, item.getString(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                                    }
                                    if(ACTIVO != item.getInt(TblAreaNegocioDefinition.Entry.ACTIVO)){
                                        values.put(TblAreaNegocioDefinition.Entry.ACTIVO, item.getString(TblAreaNegocioDefinition.Entry.ACTIVO));
                                    }
                                    AreaNegocio.update(ARN_ID, values);
                                    break;
                                }
                            }
                            if(addItem){
                                values = new ContentValues();
                                values.put(TblAreaNegocioDefinition.Entry.ARN_ID, item.getInt(TblAreaNegocioDefinition.Entry.ARN_ID));
                                values.put(TblAreaNegocioDefinition.Entry.ARN_NOMBRE, item.getString(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                                values.put(TblAreaNegocioDefinition.Entry.ACTIVO, item.getInt(TblAreaNegocioDefinition.Entry.ACTIVO));
                                AreaNegocio.insert(values);
                            }
                        }
                        CursorAreaNegocio.close();

                        startActivity(IntentBuzon);
                        finish();

                        /*Cursor cursor = AreaNegocio.getAll();
                        while(cursor.moveToNext()) {
                            ARN_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_ID));
                            ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                            ACTIVO = cursor.getInt(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ACTIVO));
                            Log.d("ARN_ID", ARN_ID.toString());
                            Log.d("ARN_NOMBRE", ARN_NOMBRE.toString());
                            Log.d("ACTIVO", ACTIVO.toString());
                            Log.d("----------", "--------------");
                        }*/
                    } else {
                        CheckErrorToExit(CursorAreaNegocio, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CheckErrorToExit(CursorAreaNegocio, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorAreaNegocio, "Ha habido un error de sincronización con el servidor (ERROR). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }


    private void CheckErrorToExit(Cursor CursorAreaNegocio, String message){
        if(CursorAreaNegocio.getCount() == 0){
            alert.setTitle("Error de sincronización");
            alert.setMessage(message);
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

}
