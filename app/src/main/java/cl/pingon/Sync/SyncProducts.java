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
import cl.pingon.R;
import cl.pingon.SQLite.TblEmpProductsDefinition;
import cl.pingon.SQLite.TblEmpProductsHelper;
import cl.pingon.SQLite.TblEmpProjectsDefinition;


public class SyncProducts {

    private RESTService REST;
    private String url;
    private Context context;
    private TblEmpProductsHelper EmpProducts;
    private Thread SyncEmpProductsThread;
    private cl.pingon.MainActivity MainActivity;
    private int intentos;

    public SyncProducts(Context context, cl.pingon.MainActivity activity, String url){
        this.intentos = 0;
        this.url = url;
        this.context = context;
        REST = new RESTService(context);
        this.MainActivity = activity;
    }

    public void Sync(final cl.pingon.MainActivity.CallbackSync cb) {
        this.intentos++;
        EmpProducts = new TblEmpProductsHelper(context);
        final Cursor CursorEmpProducts = EmpProducts.getAll();
        HashMap<String, String> headers = new HashMap<>();

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final JSONObject ResponseEmpProducts = response;
                SyncEmpProductsThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if(ResponseEmpProducts.getInt("ok") == 1){

                                EmpProducts.deleteAll();

                                JSONArray data = (JSONArray) ResponseEmpProducts.get("data");
                                JSONObject item;
                                /*Integer ID = null;
                                String NAME = null;
                                String CODE = null;
                                String YEAR = null;
                                Integer BRAND_ID = null;
                                Boolean addItem;*/
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    /*addItem = true;
                                    while(CursorEmpProducts.moveToNext()) {
                                        ID = CursorEmpProducts.getInt(CursorEmpProducts.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.ID));
                                        NAME = CursorEmpProducts.getString(CursorEmpProducts.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.NAME));
                                        CODE = CursorEmpProducts.getString(CursorEmpProducts.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.CODE));
                                        YEAR = CursorEmpProducts.getString(CursorEmpProducts.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.YEAR));
                                        BRAND_ID = CursorEmpProducts.getInt(CursorEmpProducts.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.BRAND_ID));
                                        if(ID == item.getInt(TblEmpProjectsDefinition.Entry.ID)){
                                            addItem = false;

                                            values = new ContentValues();
                                            if(NAME != item.getString(TblEmpProductsDefinition.Entry.NAME)){
                                                values.put(TblEmpProductsDefinition.Entry.NAME, item.getString(TblEmpProductsDefinition.Entry.NAME));
                                            }
                                            if(CODE != item.getString(TblEmpProductsDefinition.Entry.CODE)){
                                                values.put(TblEmpProductsDefinition.Entry.CODE, item.getString(TblEmpProductsDefinition.Entry.CODE));
                                            }
                                            if(YEAR != item.getString(TblEmpProductsDefinition.Entry.YEAR)){
                                                values.put(TblEmpProductsDefinition.Entry.YEAR, item.getString(TblEmpProductsDefinition.Entry.YEAR));
                                            }
                                            if(BRAND_ID != item.getInt(TblEmpProductsDefinition.Entry.BRAND_ID)){
                                                values.put(TblEmpProductsDefinition.Entry.BRAND_ID, item.getInt(TblEmpProductsDefinition.Entry.BRAND_ID));
                                            }
                                            EmpProducts.update(ID, values);
                                            break;
                                        }
                                    }
                                    if(addItem){*/
                                        values = new ContentValues();
                                        values.put(TblEmpProductsDefinition.Entry.ID, item.getInt(TblEmpProductsDefinition.Entry.ID));
                                        values.put(TblEmpProductsDefinition.Entry.NAME, item.getString(TblEmpProductsDefinition.Entry.NAME).trim());
                                        values.put(TblEmpProductsDefinition.Entry.CODE, item.getString(TblEmpProductsDefinition.Entry.CODE));
                                        values.put(TblEmpProductsDefinition.Entry.YEAR, item.getString(TblEmpProductsDefinition.Entry.YEAR));
                                        values.put(TblEmpProductsDefinition.Entry.BRAND_ID, item.getInt(TblEmpProductsDefinition.Entry.BRAND_ID));
                                        values.put(TblEmpProductsDefinition.Entry.PROJECT_ID, item.getInt(TblEmpProductsDefinition.Entry.PROJECT_ID));
                                        EmpProducts.insert(values);
                                    //}
                                }
                                CursorEmpProducts.close();
                                cb.success();

                                Cursor cursor = EmpProducts.getAll();
                                Log.d("CANTIDAD PRODUCTS", String.valueOf(cursor.getCount()));
                                cursor.close();
                                EmpProducts.close();

                            } else {
                                MainActivity.CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }
                    }
                });
                SyncEmpProductsThread.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR SyncEmpCompany", error.toString());
                if (intentos >= 3) {
                    intentos = 0;
                    MainActivity.CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (PRODUCTS). Si el problema persiste por favor contáctenos.");
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
}
