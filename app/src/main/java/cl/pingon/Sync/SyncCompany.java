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
import cl.pingon.MainActivity;
import cl.pingon.SQLite.TblEmpCompanyDefinition;
import cl.pingon.SQLite.TblEmpCompanyHelper;

public class SyncCompany {

    private RESTService REST;
    private String url;
    private Context context;
    private TblEmpCompanyHelper EmpCompany;
    private Thread SyncThread;
    private cl.pingon.MainActivity MainActivity;
    private int intentos;

    public SyncCompany(Context context, cl.pingon.MainActivity activity, String url){
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

                EmpCompany = new TblEmpCompanyHelper(context);
                final Cursor CursorEmpCompany = EmpCompany.getAll();
                HashMap<String, String> headers = new HashMap<>();

                REST.get(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONObject ResponseEmpCompany = response;
                        try {
                            if(ResponseEmpCompany.getInt("ok") == 1){

                                JSONArray data = (JSONArray) ResponseEmpCompany.get("data");
                                JSONObject item;
                                Integer ID = null;
                                String NAME = null;
                                String RUT = null;
                                Boolean addItem;
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    addItem = true;
                                    while(CursorEmpCompany.moveToNext()) {
                                        ID = CursorEmpCompany.getInt(CursorEmpCompany.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.ID));
                                        NAME = CursorEmpCompany.getString(CursorEmpCompany.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.NAME));
                                        RUT = CursorEmpCompany.getString(CursorEmpCompany.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.RUT));
                                        if(ID == item.getInt(TblEmpCompanyDefinition.Entry.ID)){
                                            addItem = false;

                                            values = new ContentValues();
                                            if(NAME != item.getString(TblEmpCompanyDefinition.Entry.NAME)){
                                                values.put(TblEmpCompanyDefinition.Entry.NAME, item.getString(TblEmpCompanyDefinition.Entry.NAME));
                                            }
                                            if(RUT != item.getString(TblEmpCompanyDefinition.Entry.RUT)){
                                                values.put(TblEmpCompanyDefinition.Entry.RUT, item.getString(TblEmpCompanyDefinition.Entry.RUT));
                                            }
                                            EmpCompany.update(ID, values);
                                            break;
                                        }
                                    }
                                    if(addItem){
                                        values = new ContentValues();
                                        values.put(TblEmpCompanyDefinition.Entry.ID, item.getInt(TblEmpCompanyDefinition.Entry.ID));
                                        values.put(TblEmpCompanyDefinition.Entry.NAME, item.getString(TblEmpCompanyDefinition.Entry.NAME));
                                        values.put(TblEmpCompanyDefinition.Entry.RUT, item.getString(TblEmpCompanyDefinition.Entry.RUT));
                                        EmpCompany.insert(values);
                                    }
                                }
                                CursorEmpCompany.close();
                                cb.success();

                                Cursor cursor = EmpCompany.getAll();
                                Log.d("CANTIDAD COMPANY", String.valueOf(cursor.getCount()));
                                cursor.close();
                                EmpCompany.close();
                                /*while(cursor.moveToNext()) {
                                    ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.ID));
                                    NAME = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.NAME));
                                    RUT = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.RUT));
                                    Log.d("ID", ID.toString());
                                    Log.d("NAME", NAME.toString());
                                    Log.d("RUT", RUT.toString());
                                    Log.d("----------", "--------------");
                                }*/
                            } else {
                                MainActivity.CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR SyncEmpCompany", error.toString());
                        if (intentos >= 3) {
                            intentos = 0;
                            MainActivity.CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (EMP COMPANY). Si el problema persiste por favor contáctenos.");
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
