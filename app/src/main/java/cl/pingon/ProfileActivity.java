package cl.pingon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cl.pingon.Libraries.TimerUtils;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences session;
    String email;
    String sign;

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

        this.setTitle(getResources().getString(R.string.profile_title));

        Button ChangeSign = (Button) findViewById(R.id.change_sign);
        EditText Email = (EditText) findViewById(R.id.email);
        EditText Password = (EditText) findViewById(R.id.password);
        ImageView Firma = (ImageView) findViewById(R.id.firma);

        byte[] decodedString = Base64.decode(sign, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
        Firma.setImageBitmap(decodedByte);



        Email.setText(email);
        ChangeSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
