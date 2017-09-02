package cl.pingon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cl.pingon.Libraries.RESTService;
import cl.pingon.Sync.SyncBrands;
import cl.pingon.Sync.SyncChecklist;
import cl.pingon.Sync.SyncCompany;
import cl.pingon.Sync.SyncFormularios;
import cl.pingon.Sync.SyncListOptions;
import cl.pingon.Sync.SyncListasGenerales;
import cl.pingon.Sync.SyncListasGeneralesItems;
import cl.pingon.Sync.SyncProducts;
import cl.pingon.Sync.SyncProjects;

import static repack.org.bouncycastle.crypto.tls.ContentType.alert;


public class MainActivity extends AppCompatActivity {

    Intent IntentBuzon;
    public static Activity activity;
    public MainActivity mainactivity;
    SharedPreferences session;
    RESTService REST;

    String ChecklistUrl;
    String ListOptionsUrl;
    String FormulariosUrl;
    String CompanyUrl;
    String ProjectsUrl;
    String ProductsUrl;
    String BrandsUrl;
    String ListasGeneralesUrl;
    String ListasGeneralesItemsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        activity = this;
        mainactivity = this;

        IntentBuzon = new Intent(this, BuzonActivity.class);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        REST = new RESTService(this);
        FormulariosUrl = getResources().getString(R.string.url_sync_formularios).toString()+"/"+session.getString("token","");
        ChecklistUrl = getResources().getString(R.string.url_sync_checklist).toString()+"/"+session.getString("token","");
        ListOptionsUrl = getResources().getString(R.string.url_sync_list_options).toString()+"/"+session.getString("token","");
        CompanyUrl = getResources().getString(R.string.url_sync_emp_company).toString()+"/"+session.getString("token","");
        ProjectsUrl = getResources().getString(R.string.url_sync_emp_projects).toString()+"/"+session.getString("token","");
        ProductsUrl = getResources().getString(R.string.url_sync_emp_products).toString()+"/"+session.getString("token","");
        BrandsUrl = getResources().getString(R.string.url_sync_emp_brands).toString()+"/"+session.getString("token","");
        ListasGeneralesUrl = getResources().getString(R.string.url_sync_listas_generales).toString()+"/"+session.getString("token","");
        ListasGeneralesItemsUrl = getResources().getString(R.string.url_sync_listas_generales_items).toString()+"/"+session.getString("token","");

        if(session.getString("token","") != "") {

            if(detectInternet()){

                SyncCompany Company = new SyncCompany(getApplicationContext(), mainactivity, CompanyUrl);
                Company.Sync(new CallbackSync(){
                    @Override
                    public void success() {
                        SyncProjects Projects = new SyncProjects(getApplicationContext(), mainactivity, ProjectsUrl);
                        Projects.Sync(new CallbackSync(){
                            @Override
                            public void success() {
                                SyncBrands Brands = new SyncBrands(getApplicationContext(), mainactivity, BrandsUrl);
                                Brands.Sync(new CallbackSync(){
                                    @Override
                                    public void success() {
                                        SyncProducts Products = new SyncProducts(getApplicationContext(), mainactivity, ProductsUrl);
                                        Products.Sync(new CallbackSync(){
                                            @Override
                                            public void success() {
                                                SyncFormularios Formularios = new SyncFormularios(mainactivity, FormulariosUrl);
                                                Formularios.Sync(new CallbackSync(){
                                                    @Override
                                                    public void success() {
                                                        SyncChecklist Checklist = new SyncChecklist(mainactivity, ChecklistUrl);
                                                        Checklist.Sync(new CallbackSync(){
                                                            @Override
                                                            public void success() {
                                                                SyncListOptions ListOptions = new SyncListOptions(mainactivity, ListOptionsUrl);
                                                                ListOptions.Sync(new CallbackSync(){
                                                                    @Override
                                                                    public void success() {
                                                                        SyncListasGenerales ListasGenerales = new SyncListasGenerales(getApplicationContext(), mainactivity, ListasGeneralesUrl);
                                                                        ListasGenerales.Sync(new CallbackSync(){
                                                                            @Override
                                                                            public void success() {
                                                                                SyncListasGeneralesItems ListasGeneralesItems = new SyncListasGeneralesItems(getApplicationContext(), mainactivity, ListasGeneralesItemsUrl);
                                                                                ListasGeneralesItems.Sync(new CallbackSync(){
                                                                                    @Override
                                                                                    public void success() {
                                                                                        SyncReady();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

            } else {
                //TODO verificar si hay datos en base de datos
                Message(getResources().getString(R.string.no_internet), getResources().getString(R.string.first_time_no_internet));
                //finish();
            }

        }
    }

    public class CallbackSync{
        public void success(){
            Log.d("CALLBACK", "SUCCESS");
        }
        public void error(){
            finish();
        }
    }

    /**
     * DETECCIÓN DE CONEXIÓN A INTERNET
     * @return
     */
    private boolean detectInternet(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            return activeNetwork.isConnected();
        } else{
            return false;
        }
    }


    public void SyncReady(){
        startActivity(IntentBuzon);
        finish();
    }


    public void CheckErrorToExit(Cursor CursorSync, String message){
        try {
            if (CursorSync.getCount() == 0) {
                Message("Error de sincronización", message);
            } else {
                SyncReady();
            }
        } catch (Exception e){
            Message("Error de sincronización", message);
        }
    }

    private void Message(String title, String message){

        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
        alert.setTitle(title);
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
