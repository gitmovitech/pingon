package cl.pingon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button ButtonSignin;
    AutoCompleteTextView EditTextUser;
    EditText EditTextPassword;
    Intent IntentBuzon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MainActivity.activity.finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ButtonSignin = (Button) findViewById(R.id.ButtonSignin);
        EditTextUser = (AutoCompleteTextView) findViewById(R.id.EditTextUser);
        EditTextPassword = (EditText) findViewById(R.id.EditTextPassword);
        IntentBuzon = new Intent(this, BuzonActivity.class);

        ButtonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentBuzon);
            }
        });
    }

}

