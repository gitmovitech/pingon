package cl.pingon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.pingon.Libraries.DrawView;
import cl.pingon.Model.SignPoints;

public class SignDrawActivity extends AppCompatActivity {

    DrawView drawView;
    Bitmap bitmap;
    Activity activity;
    CoordinatorLayout activity_sign_draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_draw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Haga su firma");

        activity = this;

        activity_sign_draw = (CoordinatorLayout) findViewById(R.id.activity_sign_draw);

        drawView = new DrawView(activity);
        drawView.setBackgroundColor(Color.WHITE);
        activity_sign_draw.addView(drawView);

        //bitmap = Bitmap.createBitmap(drawView.getWidth(), drawView.getHeight(), Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_firma, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Borrar:
                drawView = new DrawView(activity);
                drawView.setBackgroundColor(Color.WHITE);
                activity_sign_draw.addView(drawView);
                return true;
            case R.id.Guardar:
                ArrayList<SignPoints> points = drawView.getPoints();
                JSONObject JsonPoints;
                JSONArray JsonArrayPoints = new JSONArray();
                String PointX;
                String PointY;

                for(int x = 0;x < points.size(); x++){
                    PointX = String.valueOf(Integer.parseInt(String.valueOf(Math.round(points.get(x).getX()))));
                    PointY = String.valueOf(Integer.parseInt(String.valueOf(Math.round(points.get(x).getY()))));
                    try {
                        JsonPoints = new JSONObject();
                        JsonPoints.put(PointX, PointY);
                        JsonArrayPoints.put(JsonPoints);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(getApplicationContext(), "Firma Guardada", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtras(getIntent().getExtras());
                intent.putExtra("sign", JsonArrayPoints.toString());
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
