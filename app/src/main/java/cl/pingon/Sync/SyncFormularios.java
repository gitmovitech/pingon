package cl.pingon.Sync;

import android.content.ContentValues;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.RESTService;
import cl.pingon.MainActivity;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;

public class SyncFormularios {

    TblFormulariosHelper HelperSQLite;
    android.database.Cursor Cursor;
    RESTService REST;
    ContentValues values;
    Boolean addItem;
    JSONArray data;
    JSONObject item;
    Thread SyncThread;
    JSONObject RESTResponse;
    cl.pingon.MainActivity MainActivity;
    String url;

    Integer ARN_ID;
    Integer FRM_ID;
    String ARN_NOMBRE;
    String FRM_DECLARACION;
    String FRM_NOMBRE;
    private int intentos;

    public SyncFormularios(MainActivity MainActivity, String url){
        this.intentos = 0;
        this.MainActivity = MainActivity;
        this.url = url;

        REST = new RESTService(MainActivity.getApplicationContext());
        HelperSQLite = new TblFormulariosHelper(MainActivity.getApplicationContext());
    }

    public void Sync(){
        Cursor = HelperSQLite.getAll();
        HashMap<String, String> headers = new HashMap<>();
        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                RESTResponse = response;
                SyncThread = new Thread(new Runnable() {
                    public void run() {

                        try {
                            if(RESTResponse.getInt("ok") == 1){

                                data = (JSONArray) RESTResponse.get("data");

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    addItem = true;
                                    while(Cursor.moveToNext()) {

                                        ARN_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_ID));
                                        FRM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_ID));
                                        ARN_NOMBRE = Cursor.getString(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                                        FRM_NOMBRE = Cursor.getString(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                                        FRM_DECLARACION = Cursor.getString(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_DECLARACION));

                                        if(FRM_ID == item.getInt(TblFormulariosDefinition.Entry.FRM_ID)){
                                            addItem = false;

                                            values = new ContentValues();
                                            if(ARN_ID != item.getInt(TblFormulariosDefinition.Entry.ARN_ID)){
                                                values.put(TblFormulariosDefinition.Entry.ARN_ID, item.getInt(TblFormulariosDefinition.Entry.ARN_ID));
                                            }
                                            if(ARN_NOMBRE != item.getString(TblFormulariosDefinition.Entry.ARN_NOMBRE)){
                                                values.put(TblFormulariosDefinition.Entry.ARN_NOMBRE, item.getString(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                                            }
                                            if(FRM_NOMBRE != item.getString(TblFormulariosDefinition.Entry.FRM_NOMBRE)){
                                                values.put(TblFormulariosDefinition.Entry.FRM_NOMBRE, item.getString(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                                            }
                                            if(FRM_DECLARACION != item.getString(TblFormulariosDefinition.Entry.FRM_DECLARACION)){
                                                values.put(TblFormulariosDefinition.Entry.FRM_DECLARACION, item.getString(TblFormulariosDefinition.Entry.FRM_DECLARACION));
                                            }
                                            HelperSQLite.update(FRM_ID, values);
                                            break;
                                        }
                                    }
                                    if(addItem){
                                        values = new ContentValues();
                                        values.put(TblFormulariosDefinition.Entry.ARN_ID, item.getInt(TblFormulariosDefinition.Entry.ARN_ID));
                                        values.put(TblFormulariosDefinition.Entry.FRM_ID, item.getInt(TblFormulariosDefinition.Entry.FRM_ID));
                                        values.put(TblFormulariosDefinition.Entry.ARN_NOMBRE, item.getString(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                                        values.put(TblFormulariosDefinition.Entry.FRM_NOMBRE, item.getString(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                                        values.put(TblFormulariosDefinition.Entry.FRM_DECLARACION, item.getString(TblFormulariosDefinition.Entry.FRM_DECLARACION));
                                        HelperSQLite.insert(values);
                                    }
                                }
                                Cursor.close();

                                MainActivity.SyncReady();

                                /*Cursor = HelperSQLite.getAll();
                                while(Cursor.moveToNext()) {
                                    FRM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_ID));
                                    FRM_NOMBRE = Cursor.getString(Cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                                    Log.d(String.valueOf(FRM_ID), FRM_NOMBRE);
                                    Log.d("----------", "--------------");
                                }*/
                            } else {
                                MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                        }

                    }
                });
                SyncThread.start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (intentos >= 3) {
                    intentos = 0;
                    MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (FORMULARIOS). Si el problema persiste por favor contáctenos.");                } else {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intentos++;
                    Sync();
                }

            }
        }, headers);

    }
}
