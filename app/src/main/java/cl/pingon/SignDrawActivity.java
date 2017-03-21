package cl.pingon;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cl.pingon.Libraries.DrawView;

import static cl.pingon.R.layout.activity_sign_draw;

public class SignDrawActivity extends AppCompatActivity {

    DrawView drawView;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sign_draw);
        this.setTitle("Escriba su firma");


        CoordinatorLayout activity_sign_draw = (CoordinatorLayout) findViewById(R.id.activity_sign_draw);

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Firma Guardada", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Toast.makeText(getApplicationContext(), "Firma Guardada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        FloatingActionButton fabErase = (FloatingActionButton) findViewById(R.id.fabErase);
        fabErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        activity_sign_draw.addView(drawView);

        //bitmap = Bitmap.createBitmap(drawView.getWidth(), drawView.getHeight(), Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(bitmap);
    }
}
