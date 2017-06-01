package cl.pingon.Sync;

import android.app.Activity;
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
import cl.pingon.SQLite.TblEmpProjectsDefinition;
import cl.pingon.SQLite.TblEmpProjectsHelper;
import cl.pingon.SQLite.TblFormulariosHelper;

public class SyncProjects {

    private RESTService REST;
    private String url;
    private Context context;
    private TblEmpProjectsHelper EmpProjects;
    private Thread SyncEmpProjectsThread;
    private cl.pingon.MainActivity MainActivity;

    public SyncProjects(Context context, cl.pingon.MainActivity activity, String url){
        this.url = url;
        this.context = context;
        REST = new RESTService(context);
        this.MainActivity = activity;
    }

    public void Sync(){
        EmpProjects = new TblEmpProjectsHelper(context);
        final Cursor CursorEmpProjects = EmpProjects.getAll();
        HashMap<String, String> headers = new HashMap<>();

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                final JSONObject ResponseEmpProjects = response;
                SyncEmpProjectsThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if(ResponseEmpProjects.getInt("ok") == 1){

                                JSONArray data = (JSONArray) ResponseEmpProjects.get("data");
                                JSONObject item;
                                Integer ID = null;
                                String NAME = null;
                                String COORDINATES = null;
                                String ADDRESS = null;
                                Integer COMPANY_ID = null;
                                Boolean addItem;
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    addItem = true;
                                    while(CursorEmpProjects.moveToNext()) {
                                        ID = CursorEmpProjects.getInt(CursorEmpProjects.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ID));
                                        NAME = CursorEmpProjects.getString(CursorEmpProjects.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.NAME));
                                        COORDINATES = CursorEmpProjects.getString(CursorEmpProjects.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.COORDINATES));
                                        ADDRESS = CursorEmpProjects.getString(CursorEmpProjects.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ADDRESS));
                                        COMPANY_ID = CursorEmpProjects.getInt(CursorEmpProjects.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.COMPANY_ID));
                                        if(ID == item.getInt(TblEmpProjectsDefinition.Entry.ID)){
                                            addItem = false;

                                            values = new ContentValues();
                                            if(NAME != item.getString(TblEmpProjectsDefinition.Entry.NAME)){
                                                values.put(TblEmpProjectsDefinition.Entry.NAME, item.getString(TblEmpProjectsDefinition.Entry.NAME));
                                            }
                                            if(COORDINATES != item.getString(TblEmpProjectsDefinition.Entry.COORDINATES)){
                                                values.put(TblEmpProjectsDefinition.Entry.COORDINATES, item.getString(TblEmpProjectsDefinition.Entry.COORDINATES));
                                            }
                                            if(ADDRESS != item.getString(TblEmpProjectsDefinition.Entry.ADDRESS)){
                                                values.put(TblEmpProjectsDefinition.Entry.ADDRESS, item.getString(TblEmpProjectsDefinition.Entry.ADDRESS));
                                            }
                                            if(COMPANY_ID != item.getInt(TblEmpProjectsDefinition.Entry.COMPANY_ID)){
                                                values.put(TblEmpProjectsDefinition.Entry.COMPANY_ID, item.getInt(TblEmpProjectsDefinition.Entry.COMPANY_ID));
                                            }
                                            EmpProjects.update(ID, values);
                                            break;
                                        }
                                    }
                                    if(addItem){
                                        values = new ContentValues();
                                        values.put(TblEmpProjectsDefinition.Entry.ID, item.getInt(TblEmpProjectsDefinition.Entry.ID));
                                        values.put(TblEmpProjectsDefinition.Entry.NAME, item.getString(TblEmpProjectsDefinition.Entry.NAME));
                                        values.put(TblEmpProjectsDefinition.Entry.COORDINATES, item.getString(TblEmpProjectsDefinition.Entry.COORDINATES));
                                        values.put(TblEmpProjectsDefinition.Entry.ADDRESS, item.getString(TblEmpProjectsDefinition.Entry.ADDRESS));
                                        values.put(TblEmpProjectsDefinition.Entry.COMPANY_ID, item.getInt(TblEmpProjectsDefinition.Entry.COMPANY_ID));
                                        EmpProjects.insert(values);
                                    }
                                }
                                CursorEmpProjects.close();
                                MainActivity.SyncReady();

                            } else {
                                MainActivity.CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                        }
                    }
                });
                SyncEmpProjectsThread.start();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MainActivity.CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (EMP PROJECTS). Si el problema persiste por favor contáctenos.");
            }
        }, headers);
    }

}
