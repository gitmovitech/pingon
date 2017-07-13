package cl.pingon.Sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblListasGeneralesDefinition;
import cl.pingon.SQLite.TblListasGeneralesHelper;
import cl.pingon.SQLite.TblListasGeneralesHelper;

public class SyncListasGenerales {

    private RESTService REST;
    private String url;
    private Context context;
    private TblListasGeneralesHelper ListasGenerales;
    private Thread SyncThread;
    private cl.pingon.MainActivity MainActivity;
    private int intentos;

    public SyncListasGenerales(Context context, cl.pingon.MainActivity activity, String url){
        this.intentos = 0;
        this.url = url;
        this.context = context;
        REST = new RESTService(context);
        this.MainActivity = activity;
    }

    public void Sync(final cl.pingon.MainActivity.CallbackSync cb){
        this.intentos++;
        ListasGenerales = new TblListasGeneralesHelper(context);
        final Cursor CursorListasGenerales = ListasGenerales.getAll();
        HashMap<String, String> headers = new HashMap<>();

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final JSONObject ResponseListasGenerales = response;
                SyncThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if(ResponseListasGenerales.getInt("ok") == 1){

                                ListasGenerales.deleteAll();

                                JSONArray data = (JSONArray) ResponseListasGenerales.get("data");
                                JSONObject item;
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    values = new ContentValues();
                                    values.put(TblListasGeneralesDefinition.Entry.ID, item.getInt(TblListasGeneralesDefinition.Entry.ID));
                                    values.put(TblListasGeneralesDefinition.Entry.NAME, item.getString(TblListasGeneralesDefinition.Entry.NAME));
                                    ListasGenerales.insert(values);
                                }
                                cb.success();

                                Cursor cursor = ListasGenerales.getAll();
                                Log.d("LISTAS GENERALES", String.valueOf(cursor.getCount()));
                                cursor.close();
                                ListasGenerales.close();
                            } else {
                                MainActivity.CheckErrorToExit(CursorListasGenerales, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(CursorListasGenerales, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }
                    }
                });
                SyncThread.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR SyncListasGene", error.toString());
                if (intentos >= 3) {
                    intentos = 0;
                    MainActivity.CheckErrorToExit(CursorListasGenerales, "Ha habido un error de sincronización con el servidor (EMP BRANDS). Si el problema persiste por favor contáctenos.");
                    cb.error();
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intentos++;
                    Sync(cb);
                }
            }
        }, headers);
    }
}
