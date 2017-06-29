package cl.pingon.Sync;


import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.RESTService;
import cl.pingon.MainActivity;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;

public class SyncChecklist {

    TblChecklistHelper HelperSQLite;
    Cursor Cursor;
    RESTService REST;
    ContentValues values;
    Boolean addItem;
    Boolean removeItem;
    JSONArray data;
    JSONObject item;
    Thread SyncThread;
    JSONObject RESTResponse;
    MainActivity MainActivity;
    String url;

    Integer FRM_ID;
    Integer CHK_ID;
    Integer CAM_ID;
    Integer CAM_POSICION;
    Integer CUSTOM_LIST;
    Integer ACTIVO;
    String CHK_NOMBRE;
    String CAM_NOMBRE_INTERNO;
    String CAM_NOMBRE_EXTERNO;
    String CAM_TIPO;
    String CAM_MANDATORIO;
    String CAM_VAL_DEFECTO;
    String CAM_PLACE_HOLDER;
    private int intentos;

    public SyncChecklist(MainActivity MainActivity, String url){
        this.intentos = 0;
        this.MainActivity = MainActivity;
        this.url = url;

        REST = new RESTService(MainActivity.getApplicationContext());
        HelperSQLite = new TblChecklistHelper(MainActivity.getApplicationContext());
    }

    public void Sync(final cl.pingon.MainActivity.CallbackSync cb){
        this.intentos++;

        SyncThread = new Thread(new Runnable() {
            public void run() {

                HashMap<String, String> headers = new HashMap<>();
                REST.get(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        RESTResponse = response;

                        try {
                            if(RESTResponse.getInt("ok") == 1){

                                data = (JSONArray) RESTResponse.get("data");

                                for(int i = 0;i < data.length(); i++){
                                    addItem = true;
                                    item = (JSONObject) data.get(i);
                                    Cursor = HelperSQLite.getByCamId(item.getInt(TblChecklistDefinition.Entry.CAM_ID));
                                    if(Cursor.getCount() > 0){
                                        addItem = false;
                                        Cursor.moveToFirst();

                                        CAM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
                                        CHK_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_ID));
                                        FRM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.FRM_ID));
                                        CAM_POSICION = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_POSICION));
                                        CUSTOM_LIST = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CUSTOM_LIST));
                                        ACTIVO = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.ACTIVO));
                                        CHK_NOMBRE = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CHK_NOMBRE));
                                        CAM_NOMBRE_INTERNO = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO));
                                        CAM_NOMBRE_EXTERNO = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO));
                                        CAM_TIPO = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_TIPO));
                                        CAM_MANDATORIO = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_MANDATORIO));
                                        CAM_VAL_DEFECTO = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO));
                                        CAM_PLACE_HOLDER = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER));


                                        values = new ContentValues();
                                        if(CHK_ID != item.getInt(TblChecklistDefinition.Entry.CHK_ID)){
                                            values.put(TblChecklistDefinition.Entry.CHK_ID, item.getInt(TblChecklistDefinition.Entry.CHK_ID));
                                        }
                                        if(FRM_ID != item.getInt(TblChecklistDefinition.Entry.FRM_ID)){
                                            values.put(TblChecklistDefinition.Entry.FRM_ID, item.getInt(TblChecklistDefinition.Entry.FRM_ID));
                                        }
                                        if(CAM_POSICION != item.getInt(TblChecklistDefinition.Entry.CAM_POSICION)){
                                            values.put(TblChecklistDefinition.Entry.CAM_POSICION, item.getInt(TblChecklistDefinition.Entry.CAM_POSICION));
                                        }
                                        if(CUSTOM_LIST != item.getInt(TblChecklistDefinition.Entry.CUSTOM_LIST)){
                                            values.put(TblChecklistDefinition.Entry.CUSTOM_LIST, item.getInt(TblChecklistDefinition.Entry.CUSTOM_LIST));
                                        }
                                        if(ACTIVO != item.getInt(TblChecklistDefinition.Entry.ACTIVO)){
                                            values.put(TblChecklistDefinition.Entry.ACTIVO, item.getInt(TblChecklistDefinition.Entry.ACTIVO));
                                        }
                                        if(CHK_NOMBRE != item.getString(TblChecklistDefinition.Entry.CHK_NOMBRE)){
                                            values.put(TblChecklistDefinition.Entry.CHK_NOMBRE, item.getString(TblChecklistDefinition.Entry.CHK_NOMBRE));
                                        }
                                        if(CAM_NOMBRE_INTERNO != item.getString(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO)){
                                            values.put(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO, item.getString(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO));
                                        }
                                        if(CAM_NOMBRE_EXTERNO != item.getString(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO)){
                                            values.put(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO, item.getString(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO));
                                        }
                                        if(CAM_TIPO != item.getString(TblChecklistDefinition.Entry.CAM_TIPO)){
                                            values.put(TblChecklistDefinition.Entry.CAM_TIPO, item.getString(TblChecklistDefinition.Entry.CAM_TIPO));
                                        }
                                        if(CAM_MANDATORIO != item.getString(TblChecklistDefinition.Entry.CAM_MANDATORIO)){
                                            values.put(TblChecklistDefinition.Entry.CAM_MANDATORIO, item.getString(TblChecklistDefinition.Entry.CAM_MANDATORIO));
                                        }
                                        if(CAM_VAL_DEFECTO != item.getString(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO)){
                                            values.put(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO, item.getString(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO));
                                        }
                                        if(CAM_PLACE_HOLDER != item.getString(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER)){
                                            values.put(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER, item.getString(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER));
                                        }
                                        HelperSQLite.update(CAM_ID, values);
                                    }
                                    if(addItem){
                                        values = new ContentValues();
                                        values.put(TblChecklistDefinition.Entry.CAM_ID, item.getInt(TblChecklistDefinition.Entry.CAM_ID));
                                        values.put(TblChecklistDefinition.Entry.CHK_ID, item.getInt(TblChecklistDefinition.Entry.CHK_ID));
                                        values.put(TblChecklistDefinition.Entry.FRM_ID, item.getInt(TblChecklistDefinition.Entry.FRM_ID));
                                        values.put(TblChecklistDefinition.Entry.CAM_POSICION, item.getInt(TblChecklistDefinition.Entry.CAM_POSICION));
                                        values.put(TblChecklistDefinition.Entry.CUSTOM_LIST, item.getInt(TblChecklistDefinition.Entry.CUSTOM_LIST));
                                        values.put(TblChecklistDefinition.Entry.ACTIVO, item.getInt(TblChecklistDefinition.Entry.ACTIVO));
                                        values.put(TblChecklistDefinition.Entry.CHK_NOMBRE, item.getString(TblChecklistDefinition.Entry.CHK_NOMBRE));
                                        values.put(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO, item.getString(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO));
                                        values.put(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO, item.getString(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO));
                                        values.put(TblChecklistDefinition.Entry.CAM_TIPO, item.getString(TblChecklistDefinition.Entry.CAM_TIPO));
                                        values.put(TblChecklistDefinition.Entry.CAM_MANDATORIO, item.getString(TblChecklistDefinition.Entry.CAM_MANDATORIO));
                                        values.put(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO, item.getString(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO));
                                        values.put(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER, item.getString(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER));
                                        HelperSQLite.insert(values);
                                    }
                                    Cursor.close();
                                }

                                Cursor = HelperSQLite.getAll();
                                while(Cursor.moveToNext()){
                                    removeItem = true;
                                    CAM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));

                                    for(int i = 0;i < data.length(); i++){
                                        item = (JSONObject) data.get(i);
                                        if(item.getInt(TblChecklistDefinition.Entry.CAM_ID) == CAM_ID){
                                            removeItem = false;
                                            break;
                                        }
                                    }
                                    if(removeItem){
                                        HelperSQLite.deleteByCamId(CAM_ID);
                                    }
                                }
                                Cursor.close();
                                HelperSQLite.close();

                                cb.success();

                                Cursor = HelperSQLite.getAll();
                                Log.d("CANTIDAD CHECKLIST", String.valueOf(Cursor.getCount()));
                                Cursor.close();
                                HelperSQLite.close();


                                /*Cursor = HelperSQLite.getAll();
                                while(Cursor.moveToNext()) {
                                    CAM_ID = Cursor.getInt(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
                                    CAM_NOMBRE_INTERNO = Cursor.getString(Cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO));
                                    Log.d(String.valueOf(CAM_ID), CAM_NOMBRE_INTERNO);
                                    Log.d("----------", "--------------");
                                }*/
                            } else {
                                MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR SyncChecklist", error.toString());
                        if (intentos >= 3) {
                            intentos = 0;
                            MainActivity.CheckErrorToExit(Cursor, "Ha habido un error de sincronización con el servidor (CHECKLIST). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        } else {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            intentos++;
                            Sync(cb);
                        }
                    }
                }, headers);

            }
        });
        SyncThread.start();



    }


}
