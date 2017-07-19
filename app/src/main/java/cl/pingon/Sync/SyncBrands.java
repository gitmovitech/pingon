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
import cl.pingon.SQLite.TblEmpBrandsDefinition;
import cl.pingon.SQLite.TblEmpBrandsHelper;

public class SyncBrands {

    private RESTService REST;
    private String url;
    private Context context;
    private TblEmpBrandsHelper EmpBrands;
    private Thread SyncEmpBrandsThread;
    private cl.pingon.MainActivity MainActivity;
    private int intentos;

    public SyncBrands(Context context, cl.pingon.MainActivity activity, String url){
        this.intentos = 0;
        this.url = url;
        this.context = context;
        REST = new RESTService(context);
        this.MainActivity = activity;
    }

    public void Sync(final cl.pingon.MainActivity.CallbackSync cb){
        this.intentos++;
        EmpBrands = new TblEmpBrandsHelper(context);
        final Cursor CursorEmpBrands = EmpBrands.getAll();
        HashMap<String, String> headers = new HashMap<>();

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final JSONObject ResponseEmpBrands = response;
                SyncEmpBrandsThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if(ResponseEmpBrands.getInt("ok") == 1){

                                EmpBrands.deleteAll();

                                JSONArray data = (JSONArray) ResponseEmpBrands.get("data");
                                JSONObject item;
                                /*Integer ID = null;
                                String NAME = null;
                                Integer PROJECT_ID = null;
                                Boolean addItem;*/
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    /*addItem = true;
                                    while(CursorEmpBrands.moveToNext()) {
                                        ID = CursorEmpBrands.getInt(CursorEmpBrands.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.ID));
                                        NAME = CursorEmpBrands.getString(CursorEmpBrands.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.NAME));
                                        PROJECT_ID = CursorEmpBrands.getInt(CursorEmpBrands.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.PROJECT_ID));
                                        if(ID == item.getInt(TblEmpBrandsDefinition.Entry.ID)){
                                            addItem = false;

                                            values = new ContentValues();
                                            if(NAME != item.getString(TblEmpBrandsDefinition.Entry.NAME)){
                                                values.put(TblEmpBrandsDefinition.Entry.NAME, item.getString(TblEmpBrandsDefinition.Entry.NAME));
                                            }
                                            if(PROJECT_ID != item.getInt(TblEmpBrandsDefinition.Entry.PROJECT_ID)){
                                                values.put(TblEmpBrandsDefinition.Entry.PROJECT_ID, item.getInt(TblEmpBrandsDefinition.Entry.PROJECT_ID));
                                            }
                                            EmpBrands.update(ID, values);
                                            break;
                                        }
                                    }
                                    if(addItem){*/
                                        values = new ContentValues();
                                        values.put(TblEmpBrandsDefinition.Entry.ID, item.getInt(TblEmpBrandsDefinition.Entry.ID));
                                        values.put(TblEmpBrandsDefinition.Entry.NAME, item.getString(TblEmpBrandsDefinition.Entry.NAME));
                                        values.put(TblEmpBrandsDefinition.Entry.PROJECT_ID, item.getInt(TblEmpBrandsDefinition.Entry.PROJECT_ID));
                                        EmpBrands.insert(values);
                                    //}
                                }
                                CursorEmpBrands.close();
                                cb.success();

                                Cursor cursor = EmpBrands.getAll();
                                Log.d("CANTIDAD BRANDS", String.valueOf(cursor.getCount()));
                                cursor.close();
                                EmpBrands.close();
                            } else {
                                MainActivity.CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }
                    }
                });
                SyncEmpBrandsThread.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR SyncEmpBrands", error.toString());
                if (intentos >= 3) {
                    intentos = 0;
                    MainActivity.CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (EMP BRANDS). Si el problema persiste por favor contáctenos.");
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
