package cl.pingon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.ImageUtils;
import cl.pingon.Libraries.RESTService;
import cl.pingon.Libraries.TimerUtils;
import cl.pingon.Model.ModelChecklistFields;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences session;
    String email;
    String sign;
    String user_id;
    Intent IntentSign;
    ImageView Firma;
    DrawSign firma;
    RESTService REST;
    EditText Email;
    EditText Password;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        email = session.getString("email", "");
        sign = session.getString("sign", "");
        user_id = session.getString("user_id", "");

        this.setTitle(getResources().getString(R.string.profile_title));

        Button ChangeSign = (Button) findViewById(R.id.change_sign);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        Firma = (ImageView) findViewById(R.id.firma);

        byte[] decodedString = Base64.decode(sign, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
        Firma.setImageBitmap(decodedByte);

        IntentSign = new Intent(this, SignDrawActivity.class);

        REST = new RESTService(this);

        alert = new AlertDialog.Builder(this);

        Email.setText(email);
        ChangeSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(IntentSign, 10);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Save:
                String url = getResources().getString(R.string.url_profile_update) +"/"+session.getString("token","")+"/"+user_id;
                JSONObject params = new JSONObject();
                try {
                    params.put("SIGN", sign);
                    params.put("EMAIL", Email.getText().toString());
                    params.put("PASSWORD", Password.getText().toString());
                } catch(Exception e){}
                REST.post(url, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                SharedPreferences.Editor editor = session.edit();
                                editor.putString("email", Email.getText().toString());
                                editor.putString("sign", sign);
                                editor.commit();

                                alert.setTitle(getResources().getString(R.string.profile_title));
                                alert.setMessage(getResources().getString(R.string.profile_save_message));
                                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert.create();
                                alert.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alert.setTitle(getResources().getString(R.string.profile_title));
                                alert.setMessage(getResources().getString(R.string.profile_save_message_error));
                                alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert.create();
                                alert.show();
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //FIRMA
        if(requestCode == 10){
            if(resultCode == RESULT_OK) {
                String nsign = data.getStringExtra("sign");
                sign = nsign;
                firma = new DrawSign(nsign);
                firma.DrawToImageView(Firma);
            }
        }
    }
}
