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
import cl.pingon.SQLite.TblFeriadosDefinition;
import cl.pingon.SQLite.TblFeriadosHelper;

public class SyncFeriados {

    private RESTService REST;
    private String url;
    private Context context;
    private TblFeriadosHelper Feriados;
    private Thread SyncThread;
    private cl.pingon.MainActivity MainActivity;
    private int intentos;

    public SyncFeriados(Context context, cl.pingon.MainActivity activity, String url){
        this.intentos = 0;
        this.url = url;
        this.context = context;
        REST = new RESTService(context);
        this.MainActivity = activity;
    }

    public void Sync(final cl.pingon.MainActivity.CallbackSync cb){
        this.intentos++;

        SyncThread = new Thread(new Runnable() {
            public void run() {

                Feriados = new TblFeriadosHelper(context);
                final Cursor CursorFeriados = Feriados.getAll();
                HashMap<String, String> headers = new HashMap<>();

                REST.get(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONObject ResponseFeriados = response;
                        try {
                            if(ResponseFeriados.getInt("ok") == 1){

                                Feriados.deleteAll();

                                JSONArray data = (JSONArray) ResponseFeriados.get("data");
                                JSONObject item;
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                        values = new ContentValues();
                                        values.put(TblFeriadosDefinition.Entry.ID, item.getInt(TblFeriadosDefinition.Entry.ID));
                                        values.put(TblFeriadosDefinition.Entry.DAY, item.getString(TblFeriadosDefinition.Entry.DAY));
                                        values.put(TblFeriadosDefinition.Entry.MONTH, item.getString(TblFeriadosDefinition.Entry.MONTH));
                                        values.put(TblFeriadosDefinition.Entry.YEAR, item.getString(TblFeriadosDefinition.Entry.YEAR));
                                        Feriados.insert(values);
                                    //}
                                }
                                CursorFeriados.close();
                                cb.success();

                                Cursor cursor = Feriados.getAll();
                                Log.d("CANTIDAD FERIADOS", String.valueOf(cursor.getCount()));
                                cursor.close();
                                Feriados.close();
                            } else {
                                MainActivity.CheckErrorToExit(CursorFeriados, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(CursorFeriados, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR SyncFeriados", error.toString());
                        if (intentos >= 3) {
                            intentos = 0;
                            MainActivity.CheckErrorToExit(CursorFeriados, "Ha habido un error de sincronización con el servidor (FERIADOS). Si el problema persiste por favor contáctenos.");
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
