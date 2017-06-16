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

import cl.pingon.Libraries.RESTService;
import cl.pingon.Sync.SyncBrands;
import cl.pingon.Sync.SyncChecklist;
import cl.pingon.Sync.SyncCompany;
import cl.pingon.Sync.SyncFormularios;
import cl.pingon.Sync.SyncListOptions;
import cl.pingon.Sync.SyncProducts;
import cl.pingon.Sync.SyncProjects;


public class MainActivity extends AppCompatActivity {

    Intent IntentBuzon;
    public static Activity activity;
    public MainActivity mainactivity;
    SharedPreferences session;
    RESTService REST;
    AlertDialog.Builder alert;

    String ChecklistUrl;
    String ListOptionsUrl;
    String FormulariosUrl;
    String CompanyUrl;
    String ProjectsUrl;
    String ProductsUrl;
    String BrandsUrl;

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
        alert = new AlertDialog.Builder(this);

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

            } else {
                Message(getResources().getString(R.string.no_internet), getResources().getString(R.string.first_time_no_internet));
                finish();
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
        if(CursorSync.getCount() == 0){
            Message("Error de sincronización", message);
        } else {
            SyncReady();
        }
    }

    private void Message(String title, String message){
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
