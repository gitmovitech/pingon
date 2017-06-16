package cl.pingon.Sync;

import android.content.ContentValues;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.RESTService;
import cl.pingon.MainActivity;
import cl.pingon.SQLite.TblListOptionsDefinition;
import cl.pingon.SQLite.TblListOptionsHelper;

public class SyncListOptions {

    TblListOptionsHelper HelperSQLite;
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

    Integer FRM_ID;
    Integer OPC_ID;
    Integer CAM_ID;
    String OPC_VALOR;
    String OPC_NOMBRE;
    private int intentos;

    public SyncListOptions(MainActivity MainActivity, String url){
        this.intentos = 0;
        this.MainActivity = MainActivity;
        this.url = url;

        REST = new RESTService(MainActivity.getApplicationContext());
        HelperSQLite = new TblListOptionsHelper(MainActivity.getApplicationContext());
    }

    public void Sync() {
        this.intentos++;
        Cursor = HelperSQLite.getAll();
        HashMap<String, String> headers = new HashMap<>();
        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                RESTResponse = response;
                SyncThread = new Thread(new Runnable() {
                    public void run() {

                        try {
                            if (RESTResponse.getInt("ok") == 1) {

                                data = (JSONArray) RESTResponse.get("data");

                                for (int i = 0; i < data.length(); i++) {
                                    item = (JSONObject) data.get(i);
                                    addItem = true;
                                    while (Cursor.moveToNext()) {

                                        CAM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.CAM_ID));
                                        OPC_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_ID));
                                        FRM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.FRM_ID));
                                        OPC_NOMBRE = Cursor.getString(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_NOMBRE));
                                        OPC_VALOR = Cursor.getString(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_VALOR));

                                        if (OPC_ID == item.getInt(TblListOptionsDefinition.Entry.OPC_ID)) {
                                            addItem = false;

                                            values = new ContentValues();
                                            if (OPC_ID != item.getInt(TblListOptionsDefinition.Entry.OPC_ID)) {
                                                values.put(TblListOptionsDefinition.Entry.OPC_ID, item.getInt(TblListOptionsDefinition.Entry.OPC_ID));
                                            }
                                            if (FRM_ID != item.getInt(TblListOptionsDefinition.Entry.FRM_ID)) {
                                                values.put(TblListOptionsDefinition.Entry.FRM_ID, item.getInt(TblListOptionsDefinition.Entry.FRM_ID));
                                            }
                                            if (OPC_NOMBRE != item.getString(TblListOptionsDefinition.Entry.OPC_NOMBRE)) {
                                                values.put(TblListOptionsDefinition.Entry.OPC_NOMBRE, item.getString(TblListOptionsDefinition.Entry.OPC_NOMBRE));
                                            }
                                            if (OPC_VALOR != item.getString(TblListOptionsDefinition.Entry.OPC_VALOR)) {
                                                values.put(TblListOptionsDefinition.Entry.OPC_VALOR, item.getString(TblListOptionsDefinition.Entry.OPC_VALOR));
                                            }
                                            HelperSQLite.update(OPC_ID, values);
                                            break;
                                        }
                                    }
                                    if (addItem) {
                                        values = new ContentValues();
                                        values.put(TblListOptionsDefinition.Entry.CAM_ID, item.getInt(TblListOptionsDefinition.Entry.CAM_ID));
                                        values.put(TblListOptionsDefinition.Entry.OPC_ID, item.getInt(TblListOptionsDefinition.Entry.OPC_ID));
                                        values.put(TblListOptionsDefinition.Entry.FRM_ID, item.getInt(TblListOptionsDefinition.Entry.FRM_ID));
                                        values.put(TblListOptionsDefinition.Entry.OPC_NOMBRE, item.getString(TblListOptionsDefinition.Entry.OPC_NOMBRE));
                                        values.put(TblListOptionsDefinition.Entry.OPC_VALOR, item.getString(TblListOptionsDefinition.Entry.OPC_VALOR));
                                        HelperSQLite.insert(values);
                                    }
                                }
                                Cursor.close();

                                MainActivity.SyncReady();

                                /*Cursor = HelperSQLite.getAll();
                                while(Cursor.moveToNext()) {
                                    OPC_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_ID));
                                    OPC_VALOR = Cursor.getString(Cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_VALOR));
                                    Log.d(String.valueOf(OPC_ID), OPC_VALOR);
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
                Log.d("ERROR SyncListOptions", error.toString());
                if (intentos >= 3) {
                    intentos = 0;
                    MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (OPTIONS). Si el problema persiste por favor contáctenos.");
                } else {
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
