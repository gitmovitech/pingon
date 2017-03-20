package cl.pingon;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import cl.pingon.Libraries.DrawView;

import static cl.pingon.R.layout.activity_sign_draw;

public class SignDraw extends AppCompatActivity {

    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sign_draw);

        RelativeLayout activity_sign_draw = (RelativeLayout) findViewById(R.id.activity_sign_draw);

        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        activity_sign_draw.addView(drawView);
    }
}
