package cl.pingon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cl.pingon.Libraries.RESTService;

public class LoginActivity extends AppCompatActivity {

    Button ButtonSignin;
    AutoCompleteTextView EditTextUser;
    EditText EditTextPassword;
    Intent IntentMain;
    RESTService REST;
    AlertDialog.Builder alert;
    SharedPreferences session;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        alert = new AlertDialog.Builder(this);

        IntentMain = new Intent(this, MainActivity.class);

        progress = new ProgressDialog(this);

        REST = new RESTService(this);

        ButtonSignin = (Button) findViewById(R.id.ButtonSignin);
        EditTextUser = (AutoCompleteTextView) findViewById(R.id.EditTextUser);
        EditTextPassword = (EditText) findViewById(R.id.EditTextPassword);

        ButtonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    /*startActivity(IntentMain);
                    finish();*/

                if (EditTextUser.getText().toString().isEmpty()) {
                    EditTextUser.setError("Debe ingresar su nombre de usuario");
                } else if (EditTextPassword.getText().toString().isEmpty()) {
                    EditTextPassword.setError("Debe ingresar su contraseña");
                } else {
                    progress.setCancelable(false);
                    progress.setTitle("Iniciando sesión");
                    progress.setMessage("Por favor espere...");
                    progress.show();

                    String url = getResources().getString(R.string.url_signin) + "?user=" + EditTextUser.getText().toString() + "&pass=" + EditTextPassword.getText().toString();
                    JSONObject params = new JSONObject();
                    try {
                        params.put("user", EditTextUser.getText().toString());
                        params.put("pass", EditTextPassword.getText().toString());


                    } catch(Exception e){

                    }
                    REST.post(url, params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        //Log.d("LOGIN RESPONSE", response.toString());
                                        if (response.getInt("ok") == 1) {

                                            SharedPreferences.Editor editor = session.edit();
                                            editor.putString("token", response.getString("token"));
                                            editor.putString("user_id", response.getString("id"));
                                            editor.putString("arn_id", response.getString("arn_id"));
                                            editor.putString("first_name", response.getString("first_name"));
                                            editor.putString("last_name", response.getString("last_name"));
                                            editor.putString("run", response.getString("run"));
                                            editor.putString("email", response.getString("email"));
                                            editor.putString("sign", response.getString("firma"));
                                            editor.putString("u", EditTextUser.getText().toString());
                                            editor.putString("p", EditTextPassword.getText().toString());
                                            editor.commit();

                                            startActivity(IntentMain);
                                            finish();
                                        } else {
                                            alert.setTitle("Error");
                                            alert.setMessage(response.getString("message"));
                                            alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            alert.create();
                                            alert.show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progress.hide();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progress.hide();
                                    alert.setTitle("Error de conexión");
                                    alert.setMessage("Ha habido un error de conexión al servidor.\n\nCompruebe que posee una conexión a Internet activa.\n\nSi el problema persiste, puede que los servicios se encuentren desactivados. En este caso contáctenos para notificarnos sobre este problema.");
                                    alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alert.create();
                                    alert.show();
                                }
                            });

                }
            }
        });

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        if(session.getString("token","") != ""){

            if(detectInternet()){
                String u = session.getString("u","");
                String p = session.getString("p","");
                String url = getResources().getString(R.string.url_signin) + "?user=" + u + "&pass=" + p;
                JSONObject params = new JSONObject();
                if(!u.isEmpty() && !p.isEmpty()){
                    try {
                        progress.setCancelable(false);
                        progress.setTitle("Iniciando sesión");
                        progress.setMessage("Por favor espere...");
                        progress.show();

                        params.put("user", u);
                        params.put("pass", p);

                        REST.post(url, params,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            //Log.d("LOGIN RESPONSE", response.toString());
                                            if (response.getInt("ok") == 1) {

                                                SharedPreferences.Editor editor = session.edit();
                                                editor.putString("token", response.getString("token"));
                                                editor.putString("user_id", response.getString("id"));
                                                editor.putString("arn_id", response.getString("arn_id"));
                                                editor.putString("first_name", response.getString("first_name"));
                                                editor.putString("last_name", response.getString("last_name"));
                                                editor.putString("run", response.getString("run"));
                                                editor.putString("email", response.getString("email"));
                                                editor.putString("sign", response.getString("firma"));
                                                editor.putString("u", session.getString("u",""));
                                                editor.putString("p", session.getString("p",""));
                                                editor.commit();

                                                startActivity(IntentMain);
                                                finish();
                                            } else {
                                                alert.setTitle("Error");
                                                alert.setMessage(response.getString("message"));
                                                alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                alert.create();
                                                alert.show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        progress.hide();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progress.hide();
                                        alert.setTitle("Error de conexión");
                                        alert.setMessage("Ha habido un error de conexión al servidor.\n\nCompruebe que posee una conexión a Internet activa.\n\nSi el problema persiste, puede que los servicios se encuentren desactivados. En este caso contáctenos para notificarnos sobre este problema.");
                                        alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        alert.create();
                                        alert.show();
                                    }
                                });

                    } catch (Exception e) {

                    }
                }
            } else {
                startActivity(IntentMain);
                finish();
            }

        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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

}

