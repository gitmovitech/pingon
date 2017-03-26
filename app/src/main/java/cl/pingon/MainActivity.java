package cl.pingon;

import android.app.Activity;
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

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblAreaNegocioDefinition;
import cl.pingon.SQLite.TblAreaNegocioHelper;
import cl.pingon.SQLite.TblEmpBrandsDefinition;
import cl.pingon.SQLite.TblEmpBrandsHelper;
import cl.pingon.SQLite.TblEmpCompanyDefinition;
import cl.pingon.SQLite.TblEmpCompanyHelper;
import cl.pingon.SQLite.TblEmpProductsDefinition;
import cl.pingon.SQLite.TblEmpProductsHelper;
import cl.pingon.SQLite.TblEmpProjectsDefinition;
import cl.pingon.SQLite.TblEmpProjectsHelper;

public class MainActivity extends AppCompatActivity {

    Intent IntentBuzon;
    public static Activity activity;
    SharedPreferences session;
    RESTService REST;
    AlertDialog.Builder alert;
    TblAreaNegocioHelper AreaNegocio;
    TblEmpCompanyHelper EmpCompany;
    TblEmpProjectsHelper EmpProjects;
    TblEmpBrandsHelper EmpBrands;
    TblEmpProductsHelper EmpProducts;
    int Syncronized = 0;
    private Thread SyncEmpCompanyThread;
    private Thread SyncEmpProjectsThread;
    private Thread SyncEmpBrandsThread;
    private Thread SyncEmpProductsThread;

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

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        REST = new RESTService(this);

        if(session.getString("token","") != "") {

            SyncEmpCompany();
            SyncEmpProjects();
            SyncEmpBrands();
            SyncEmpProducts();

            //SyncAreaNegocio();

        }
    }


    private void SyncReady(){
        Syncronized++;
        if(Syncronized >= 4){
            startActivity(IntentBuzon);
            finish();
        }
    }

    private void SyncAreaNegocio(){
        AreaNegocio = new TblAreaNegocioHelper(this);
        final Cursor CursorAreaNegocio = AreaNegocio.getAll();
        HashMap<String, String> headers = new HashMap<>();
        String url = getResources().getString(R.string.url_sync_area_negocio).toString()+"/"+session.getString("token","");
        REST.get(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("ok") == 1){

                        JSONArray data = (JSONArray) response.get("data");
                        JSONObject item;
                        Integer ARN_ID = null;
                        String ARN_NOMBRE = null;
                        Integer ACTIVO = null;
                        Boolean addItem;
                        ContentValues values;

                        for(int i = 0;i < data.length(); i++){
                            item = (JSONObject) data.get(i);
                            addItem = true;
                            while(CursorAreaNegocio.moveToNext()) {
                                ARN_ID = CursorAreaNegocio.getInt(CursorAreaNegocio.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_ID));
                                ARN_NOMBRE = CursorAreaNegocio.getString(CursorAreaNegocio.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                                ACTIVO = CursorAreaNegocio.getInt(CursorAreaNegocio.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ACTIVO));
                                if(ARN_ID == item.getInt(TblAreaNegocioDefinition.Entry.ARN_ID)){
                                    addItem = false;

                                    values = new ContentValues();
                                    if(ARN_NOMBRE != item.getString(TblAreaNegocioDefinition.Entry.ARN_NOMBRE)){
                                        values.put(TblAreaNegocioDefinition.Entry.ARN_NOMBRE, item.getString(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                                    }
                                    if(ACTIVO != item.getInt(TblAreaNegocioDefinition.Entry.ACTIVO)){
                                        values.put(TblAreaNegocioDefinition.Entry.ACTIVO, item.getString(TblAreaNegocioDefinition.Entry.ACTIVO));
                                    }
                                    AreaNegocio.update(ARN_ID, values);
                                    break;
                                }
                            }
                            if(addItem){
                                values = new ContentValues();
                                values.put(TblAreaNegocioDefinition.Entry.ARN_ID, item.getInt(TblAreaNegocioDefinition.Entry.ARN_ID));
                                values.put(TblAreaNegocioDefinition.Entry.ARN_NOMBRE, item.getString(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                                values.put(TblAreaNegocioDefinition.Entry.ACTIVO, item.getInt(TblAreaNegocioDefinition.Entry.ACTIVO));
                                AreaNegocio.insert(values);
                            }
                        }
                        CursorAreaNegocio.close();
                        SyncReady();

                        /*Cursor cursor = AreaNegocio.getAll();
                        while(cursor.moveToNext()) {
                            ARN_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_ID));
                            ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ARN_NOMBRE));
                            ACTIVO = cursor.getInt(cursor.getColumnIndexOrThrow(TblAreaNegocioDefinition.Entry.ACTIVO));
                            Log.d("ARN_ID", ARN_ID.toString());
                            Log.d("ARN_NOMBRE", ARN_NOMBRE.toString());
                            Log.d("ACTIVO", ACTIVO.toString());
                            Log.d("----------", "--------------");
                        }*/
                    } else {
                        CheckErrorToExit(CursorAreaNegocio, "Ha habido un error de sincronización con el servidor (NO DATA). Si el problema persiste por favor contáctenos.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CheckErrorToExit(CursorAreaNegocio, "Ha habido un error de sincronización con el servidor (RESPONSE). Si el problema persiste por favor contáctenos.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorAreaNegocio, "Ha habido un error de sincronización con el servidor (ERROR). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }

    /**
     * SINCRONIZACION DE COMPANIES
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
                        } catch (Exception e) {}
                    }
                });
                SyncEmpCompanyThread.start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckErrorToExit(CursorEmpCompany, "Ha habido un error de sincronización con el servidor (ERROR). Si el problema persiste por favor contáctenos.");
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
                                            EmpProjects.insert(values);
                                        }
                                    }
                                    CursorEmpProjects.close();
                                    SyncReady();

                                    /*Cursor cursor = EmpProjects.getAll();
                                    while(cursor.moveToNext()) {
                                        ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ID));
                                        NAME = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.NAME));
                                        COORDINATES = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.COORDINATES));
                                        ADDRESS = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ADDRESS));
                                        Log.d("ID", ID.toString());
                                        Log.d("NAME", NAME.toString());
                                        Log.d("COORDINATES", COORDINATES.toString());
                                        Log.d("ADDRESS", ADDRESS.toString());
                                        Log.d("----------", "--------------");
                                    }*/
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
                CheckErrorToExit(CursorEmpProjects, "Ha habido un error de sincronización con el servidor (ERROR). Si el problema persiste por favor contáctenos.");
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
                                    Boolean addItem;
                                    ContentValues values;

                                    for(int i = 0;i < data.length(); i++){
                                        item = (JSONObject) data.get(i);
                                        addItem = true;
                                        while(CursorEmpBrands.moveToNext()) {
                                            ID = CursorEmpBrands.getInt(CursorEmpBrands.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.ID));
                                            NAME = CursorEmpBrands.getString(CursorEmpBrands.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.NAME));
                                            if(ID == item.getInt(TblEmpBrandsDefinition.Entry.ID)){
                                                addItem = false;

                                                values = new ContentValues();
                                                if(NAME != item.getString(TblEmpBrandsDefinition.Entry.NAME)){
                                                    values.put(TblEmpBrandsDefinition.Entry.NAME, item.getString(TblEmpBrandsDefinition.Entry.NAME));
                                                }
                                                EmpBrands.update(ID, values);
                                                break;
                                            }
                                        }
                                        if(addItem){
                                            values = new ContentValues();
                                            values.put(TblEmpBrandsDefinition.Entry.ID, item.getInt(TblEmpBrandsDefinition.Entry.ID));
                                            values.put(TblEmpBrandsDefinition.Entry.NAME, item.getString(TblEmpBrandsDefinition.Entry.NAME));
                                            EmpBrands.insert(values);
                                        }
                                    }
                                    CursorEmpBrands.close();
                                    SyncReady();

                                    /*Cursor cursor = EmpBrands.getAll();
                                    while(cursor.moveToNext()) {
                                        ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.ID));
                                        NAME = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.NAME));
                                        Log.d("ID", ID.toString());
                                        Log.d("NAME", NAME.toString());
                                        Log.d("----------", "--------------");
                                    }*/
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
                CheckErrorToExit(CursorEmpBrands, "Ha habido un error de sincronización con el servidor (ERROR). Si el problema persiste por favor contáctenos.");
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
                                            EmpProducts.insert(values);
                                        }
                                    }
                                    CursorEmpProducts.close();
                                    SyncReady();

                                    Cursor cursor = EmpProducts.getAll();
                                    while(cursor.moveToNext()) {
                                        ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.ID));
                                        NAME = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.NAME));
                                        CODE = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.CODE));
                                        YEAR = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.YEAR));
                                        Log.d("ID", ID.toString());
                                        Log.d("NAME", NAME.toString());
                                        Log.d("CODE", CODE.toString());
                                        Log.d("YEAR", YEAR.toString());
                                        Log.d("----------", "--------------");
                                    }
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
                CheckErrorToExit(CursorEmpProducts, "Ha habido un error de sincronización con el servidor (ERROR). Si el problema persiste por favor contáctenos.");
            }
        }, headers);

    }


    private void CheckErrorToExit(Cursor CursorAreaNegocio, String message){
        if(CursorAreaNegocio.getCount() == 0){
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
        }
    }

}
