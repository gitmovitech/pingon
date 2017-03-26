package cl.pingon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import cl.pingon.Libraries.RESTService;

public class LoginActivity extends AppCompatActivity {

    Button ButtonSignin;
    AutoCompleteTextView EditTextUser;
    EditText EditTextPassword;
    Intent IntentMain;
    ProgressDialog progress;
    RESTService REST;
    AlertDialog.Builder alert;
    SharedPreferences session;

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

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        if(session.getString("token","") != ""){

            startActivity(IntentMain);
            finish();

        } else {

            progress = new ProgressDialog(this);
            progress.setCancelable(false);

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
                        progress.setTitle("Iniciando sesión");
                        progress.setMessage("Por favor espere...");
                        progress.show();

                        String url = getResources().getString(R.string.url_signin) + "?user=" + EditTextUser.getText().toString() + "&pass=" + EditTextPassword.getText().toString();

                        HashMap<String, String> cabeceras = new HashMap<>();
                        REST.get(url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if (response.getInt("ok") == 1) {

                                                SharedPreferences.Editor editor = session.edit();
                                                editor.putString("token", response.getString("token"));
                                                editor.putString("user_id", response.getString("id"));
                                                editor.putString("last_name", response.getString("last_name"));
                                                editor.putString("last_name", response.getString("last_name"));
                                                editor.putString("run", response.getString("run"));
                                                editor.putString("sign", response.getString("firma"));
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
                                    }
                                }, cabeceras);

                    }
                }
            });

            REST = new RESTService(this);

        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

}

