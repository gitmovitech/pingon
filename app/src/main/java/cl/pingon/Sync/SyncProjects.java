package cl.pingon.Sync;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
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
    private Thread SyncThread;
    private cl.pingon.MainActivity MainActivity;
    private int intentos;

    public SyncProjects(Context context, cl.pingon.MainActivity activity, String url){
        this.intentos = 0;
        this.url = url;
        this.context = context;
        REST = new RESTService(context);
        this.MainActivity = activity;
    }

    public void Sync(final cl.pingon.MainActivity.CallbackSync cb){
        this.intentos ++;

        SyncThread = new Thread(new Runnable() {
            public void run() {

                EmpProjects = new TblEmpProjectsHelper(context);
                final Cursor CursorEmpProjects = EmpProjects.getAll();
                HashMap<String, String> headers = new HashMap<>();

                REST.get(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        final JSONObject ResponseEmpProjects = response;
                        try {
                            if(ResponseEmpProjects.getInt("ok") == 1){

                                EmpProjects.deleteAll();

                                JSONArray data = (JSONArray) ResponseEmpProjects.get("data");
                                JSONObject item;
                                /*Integer ID = null;
                                String NAME = null;
                                String COORDINATES = null;
                                String ADDRESS = null;
                                Integer COMPANY_ID = null;
                                Boolean addItem;*/
                                ContentValues values;

                                for(int i = 0;i < data.length(); i++){
                                    item = (JSONObject) data.get(i);
                                    /*addItem = true;
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
                                    if(addItem){*/
                                        values = new ContentValues();
                                        values.put(TblEmpProjectsDefinition.Entry.ID, item.getInt(TblEmpProjectsDefinition.Entry.ID));
                                        values.put(TblEmpProjectsDefinition.Entry.NAME, item.getString(TblEmpProjectsDefinition.Entry.NAME));
                                        values.put(TblEmpProjectsDefinition.Entry.COORDINATES, item.getString(TblEmpProjectsDefinition.Entry.COORDINATES));
                                        values.put(TblEmpProjectsDefinition.Entry.ADDRESS, item.getString(TblEmpProjectsDefinition.Entry.ADDRESS));
                                        values.put(TblEmpProjectsDefinition.Entry.COMPANY_ID, item.getInt(TblEmpProjectsDefinition.Entry.COMPANY_ID));
                                        EmpProjects.insert(values);
                                    //}
                                }
                                CursorEmpProjects.close();
                                cb.success();

                                Cursor cursor = EmpProjects.getAll();
                                Log.d("CANTIDAD PROJECTS", String.valueOf(cursor.getCount()));
                                cursor.close();
                                EmpProjects.close();

                            } else {
                                CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                cb.error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            cb.error();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR SyncProjects", error.toString());
                        if (intentos >= 10) {
                            intentos = 0;
                            CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (EMP PROJECTS). Si el problema persiste por favor contáctenos.");
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

    public void CheckErrorToExit(Cursor CursorSync, String message){
        if (CursorSync.getCount() == 0) {
            Message("Error de sincronización", message);
        } else {
            MainActivity.SyncReady();
        }
    }

    private void Message(String title, String message){

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.getApplicationContext());
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);
                MainActivity.finish();
            }
        });
        alert.create();
        alert.show();

    }

}
