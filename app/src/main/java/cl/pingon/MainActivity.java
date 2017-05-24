package cl.pingon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.pingon.Libraries.NetworkUtils;
import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblEmpBrandsDefinition;
import cl.pingon.SQLite.TblEmpBrandsHelper;
import cl.pingon.SQLite.TblEmpCompanyDefinition;
import cl.pingon.SQLite.TblEmpCompanyHelper;
import cl.pingon.SQLite.TblEmpProductsDefinition;
import cl.pingon.SQLite.TblEmpProductsHelper;
import cl.pingon.SQLite.TblEmpProjectsDefinition;
import cl.pingon.SQLite.TblEmpProjectsHelper;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.Sync.SyncChecklist;
import cl.pingon.Sync.SyncFormularios;
import cl.pingon.Sync.SyncListOptions;


public class MainActivity extends AppCompatActivity {

    Intent IntentBuzon;
    public static Activity activity;
    SharedPreferences session;
    RESTService REST;
    AlertDialog.Builder alert;

    TblEmpCompanyHelper EmpCompany;
    TblEmpProjectsHelper EmpProjects;
    TblEmpBrandsHelper EmpBrands;
    TblEmpProductsHelper EmpProducts;
    TblFormulariosHelper Formularios;

    int Syncronized = 0;
    private Thread SyncEmpCompanyThread;
    private Thread SyncEmpProjectsThread;
    private Thread SyncEmpBrandsThread;
    private Thread SyncEmpProductsThread;
    private Thread SyncFormulariosThread;

    String ChecklistUrl;
    String ListOptionsUrl;
    String FormulariosUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        activity = this;
        alert = new AlertDialog.Builder(this);

        IntentBuzon = new Intent(this, BuzonActivity.class);

        /**
         * UNIT TEST
         *
        Intent IntentInformes = new Intent(this, InformesActivity.class);
        IntentInformes.putExtra("DOC_EXT_ID_CLIENTE", 308);
        IntentInformes.putExtra("DOC_EXT_NOMBRE_CLIENTE", "Renta Equipos Simunovic SPA");
        IntentInformes.putExtra("DOC_EXT_ID_PROYECTO", 222);
        IntentInformes.putExtra("DOC_EXT_OBRA", "Simunovic SPA");
        IntentInformes.putExtra("DOC_EXT_EQUIPO", "J 50.10");
        IntentInformes.putExtra("DOC_EXT_MARCA_EQUIPO", "Jaso");
        IntentInformes.putExtra("DOC_EXT_NUMERO_SERIE", "1017");
        startActivity(IntentInformes); finish();*/

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        REST = new RESTService(this);
        FormulariosUrl = getResources().getString(R.string.url_sync_formularios).toString()+"/"+session.getString("token","");
        ChecklistUrl = getResources().getString(R.string.url_sync_checklist).toString()+"/"+session.getString("token","");
        ListOptionsUrl = getResources().getString(R.string.url_sync_list_options).toString()+"/"+session.getString("token","");

        if(session.getString("token","") != "") {

            SyncEmpCompany();
            SyncEmpProjects();
            SyncEmpBrands();
            SyncEmpProducts();

            SyncFormularios Formularios = new SyncFormularios(this, FormulariosUrl);
            Formularios.Sync();

            SyncChecklist Checklist = new SyncChecklist(this, ChecklistUrl);
            Checklist.Sync();

            SyncListOptions ListOptions = new SyncListOptions(this, ListOptionsUrl);
            ListOptions.Sync();

        }
    }


    public void SyncReady(){
        Syncronized++;
        if(Syncronized >= 7){
            startActivity(IntentBuzon);
            finish();
        }
    }


    /**
     * SINCRONIZACION DE CLIENTES
     */
    private void SyncEmpCompany(){
        EmpCompany = new TblEmpCompanyHelper(this);
        final Cursor CursorEmpCompany = EmpCompany.getAll();
        HashMap<String, String> headers = new HashMap<>();
        String url = getResources().getString(R.string.url_sync_emp_company).toString()+"/"+session.getString("token","");

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final JSONObject ResponseEmpCompany = response;
                SyncEmpCompanyThread = new Thread(new Runnable() {
                    public void run() {
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
                                SyncReady();

                                    /*Cursor cursor = EmpCompany.getAll();
                                    while(cursor.moveToNext()) {
                                        ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.ID));
                                        NAME = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.NAME));
                                        RUT = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.RUT));
                                        Log.d("ID", ID.toString());
                                        Log.d("NAME", NAME.toString());
                                        Log.d("RUT", RUT.toString());
                                        Log.d("----------", "--------------");
                                    }*/
                            } else {
                                CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                        }
                    }
                });
                SyncEmpCompanyThread.start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (EMP COMPANY). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }


    /**
     * SINCRONIZACION DE PROYECTOS
     */
    private void SyncEmpProjects(){
        EmpProjects = new TblEmpProjectsHelper(this);
        final Cursor CursorEmpProjects = EmpProjects.getAll();
        HashMap<String, String> headers = new HashMap<>();
        String url = getResources().getString(R.string.url_sync_emp_projects).toString()+"/"+session.getString("token","");

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                final JSONObject ResponseEmpProjects = response;
                SyncEmpProjectsThread = new Thread(new Runnable() {
                    public void run() {
                        try {
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
                                    SyncReady();

                                } else {
                                    CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            }
                        } catch (Exception e) {}
                    }
                });
                SyncEmpProjectsThread.start();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (EMP PROJECTS). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }

    /**
     * SINCRONIZACION DE MARCAS
     */
    private void SyncEmpBrands(){
        EmpBrands = new TblEmpBrandsHelper(this);
        final Cursor CursorEmpBrands = EmpBrands.getAll();
        HashMap<String, String> headers = new HashMap<>();
        String url = getResources().getString(R.string.url_sync_emp_brands).toString()+"/"+session.getString("token","");

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final JSONObject ResponseEmpBrands = response;
                SyncEmpBrandsThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            try {
                                if(ResponseEmpBrands.getInt("ok") == 1){

                                    JSONArray data = (JSONArray) ResponseEmpBrands.get("data");
                                    JSONObject item;
                                    Integer ID = null;
                                    String NAME = null;
                                    Integer PROJECT_ID = null;
                                    Boolean addItem;
                                    ContentValues values;

                                    for(int i = 0;i < data.length(); i++){
                                        item = (JSONObject) data.get(i);
                                        addItem = true;
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
                                        if(addItem){
                                            values = new ContentValues();
                                            values.put(TblEmpBrandsDefinition.Entry.ID, item.getInt(TblEmpBrandsDefinition.Entry.ID));
                                            values.put(TblEmpBrandsDefinition.Entry.NAME, item.getString(TblEmpBrandsDefinition.Entry.NAME));
                                            values.put(TblEmpBrandsDefinition.Entry.PROJECT_ID, item.getInt(TblEmpBrandsDefinition.Entry.PROJECT_ID));
                                            EmpBrands.insert(values);
                                        }
                                    }
                                    CursorEmpBrands.close();
                                    SyncReady();
                                } else {
                                    CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            }
                        } catch (Exception e) {}
                    }
                });
                SyncEmpBrandsThread.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (EMP BRANDS). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }

    /**
     * SINCRONIZACION DE PRODUCTOS
     */
    private void SyncEmpProducts(){
        EmpProducts = new TblEmpProductsHelper(this);
        final Cursor CursorEmpProducts = EmpProducts.getAll();
        HashMap<String, String> headers = new HashMap<>();
        String url = getResources().getString(R.string.url_sync_emp_products).toString()+"/"+session.getString("token","");

        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final JSONObject ResponseEmpProducts = response;
                SyncEmpProductsThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            try {
                                if(ResponseEmpProducts.getInt("ok") == 1){

                                    JSONArray data = (JSONArray) ResponseEmpProducts.get("data");
                                    JSONObject item;
                                    Integer ID = null;
                                    String NAME = null;
                                    String CODE = null;
                                    String YEAR = null;
                                    Integer BRAND_ID = null;
                                    Boolean addItem;
                                    ContentValues values;

                                    for(int i = 0;i < data.length(); i++){
                                        item = (JSONObject) data.get(i);
                                        addItem = true;
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
                                        if(addItem){
                                            values = new ContentValues();
                                            values.put(TblEmpProductsDefinition.Entry.ID, item.getInt(TblEmpProductsDefinition.Entry.ID));
                                            values.put(TblEmpProductsDefinition.Entry.NAME, item.getString(TblEmpProductsDefinition.Entry.NAME));
                                            values.put(TblEmpProductsDefinition.Entry.CODE, item.getString(TblEmpProductsDefinition.Entry.CODE));
                                            values.put(TblEmpProductsDefinition.Entry.YEAR, item.getString(TblEmpProductsDefinition.Entry.YEAR));
                                            values.put(TblEmpProductsDefinition.Entry.BRAND_ID, item.getInt(TblEmpProductsDefinition.Entry.BRAND_ID));
                                            EmpProducts.insert(values);
                                        }
                                    }
                                    CursorEmpProducts.close();
                                    SyncReady();

                                } else {
                                    CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                            }
                        } catch (Exception e) {}
                    }
                });
                SyncEmpProductsThread.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (PRODUCTS). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }




    public void CheckErrorToExit(Cursor CursorSync, String message){
        if(CursorSync.getCount() == 0){
            alert.setTitle("Error de sincronización");
            alert.setMessage(message);
            alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    System.exit(0);
                    finish();
                }
            });
            alert.create();
            alert.show();
        } else {
            SyncReady();
        }
    }

}
