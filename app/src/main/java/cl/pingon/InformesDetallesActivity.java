package cl.pingon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;

public class InformesDetallesActivity extends AppCompatActivity {

    Intent IntentSign;
    Intent CameraIntent;
    ImageButton ImageButtonFoto;

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String ImageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes_detalles);
        getSupportActionBar();
        this.setTitle(getIntent().getStringExtra("InformeSubtitle"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("TabSeccion"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        IntentSign = new Intent(this, SignDrawActivity.class);
        CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ImageName = Environment.getExternalStorageDirectory() + "/tmp.jpg";
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(ImageName)));

        //SOLUCION A LOS DIFERENTES TIPOS DE ITEMS http://android.amberfog.com/?p=296

        LinearLayout LinearLayoutInformesDetalles = (LinearLayout) findViewById(R.id.LinearLayoutInformesDetalles);
        LayoutInflater Inflater = LayoutInflater.from(this);
        View ItemFechaView = Inflater.inflate(R.layout.item_fecha, null);
        View ItemHoraView = Inflater.inflate(R.layout.item_hora, null);
        View ItemSelectView = Inflater.inflate(R.layout.item_select, null);
        View ItemTextareaView = Inflater.inflate(R.layout.item_textarea, null);
        View ItemNumeroView = Inflater.inflate(R.layout.item_numero, null);
        View ItemEmailView = Inflater.inflate(R.layout.item_email, null);
        View ItemRadioView = Inflater.inflate(R.layout.item_radio, null);
        View ItemFotoView = Inflater.inflate(R.layout.item_foto, null);
        View ItemVideoView = Inflater.inflate(R.layout.item_video, null);
        View ItemAudioView = Inflater.inflate(R.layout.item_audio, null);
        View ItemFirmaView = Inflater.inflate(R.layout.item_firma, null);


        LinearLayoutInformesDetalles.addView(ItemFotoView);
        LinearLayoutInformesDetalles.addView(ItemFechaView);
        LinearLayoutInformesDetalles.addView(ItemHoraView);
        LinearLayoutInformesDetalles.addView(ItemSelectView);
        LinearLayoutInformesDetalles.addView(ItemTextareaView);
        LinearLayoutInformesDetalles.addView(ItemNumeroView);
        LinearLayoutInformesDetalles.addView(ItemEmailView);
        LinearLayoutInformesDetalles.addView(ItemRadioView);
        LinearLayoutInformesDetalles.addView(ItemVideoView);
        LinearLayoutInformesDetalles.addView(ItemAudioView);
        LinearLayoutInformesDetalles.addView(ItemFirmaView);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button ButtonFirma = (Button) ItemFirmaView.findViewById(R.id.item_firma);
        ButtonFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentSign);
            }
        });

        Button ButtonCamera = (Button) ItemFotoView.findViewById(R.id.item_foto);
        ButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(CameraIntent, TAKE_PICTURE);
            }
        });

        ImageButtonFoto = (ImageButton) ItemFotoView.findViewById(R.id.ImageViewFoto);
        ImageButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhoto(Uri.fromFile(new File(ImageName)));
            }
        });
    }

    private void showPhoto(Uri photoUri){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(photoUri, "image/*");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap ImageBitmapDecoded = BitmapFactory.decodeFile(ImageName);
        ImageButtonFoto.setImageBitmap(ImageBitmapDecoded);
        ImageButtonFoto.setVisibility(View.VISIBLE);
    }
}
