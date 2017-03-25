package cl.pingon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

public class InformesDetallesActivity extends AppCompatActivity {

    Intent IntentSign;
    Intent CameraIntent;
    ImageButton ImageButtonFoto;
    VideoView VideoViewItem;
    Button ButtonVideoView;
    Button ButtonVideoPlay;
    Button ButtonVideoRewind;
    Button ButtonVideoStop;
    LinearLayout LinearLayoutVideo;

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String ImageName = "";

    private static final int REQUEST_VIDEO_CAPTURE = 1;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static String mFileName = null;
    Button btn_audio_start;
    Button btn_audio_stop;
    Button btn_audio_play;


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


        LinearLayoutInformesDetalles.addView(ItemAudioView);
        LinearLayoutInformesDetalles.addView(ItemVideoView);
        LinearLayoutInformesDetalles.addView(ItemFotoView);
        LinearLayoutInformesDetalles.addView(ItemFechaView);
        LinearLayoutInformesDetalles.addView(ItemHoraView);
        LinearLayoutInformesDetalles.addView(ItemSelectView);
        LinearLayoutInformesDetalles.addView(ItemTextareaView);
        LinearLayoutInformesDetalles.addView(ItemNumeroView);
        LinearLayoutInformesDetalles.addView(ItemEmailView);
        LinearLayoutInformesDetalles.addView(ItemRadioView);
        LinearLayoutInformesDetalles.addView(ItemFirmaView);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /**
         * FIRMA
         */
        Button ButtonFirma = (Button) ItemFirmaView.findViewById(R.id.item_firma);
        ButtonFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentSign);
            }
        });


        /**
         * FOTOS
         */
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


        /**
         * VIDEO
         */
        VideoViewItem = (VideoView) ItemVideoView.findViewById(R.id.VideoViewItem);
        LinearLayoutVideo = (LinearLayout) ItemVideoView.findViewById(R.id.LinearLayoutVideo);
        ButtonVideoView = (Button) ItemVideoView.findViewById(R.id.ButtonVideoView);
        ButtonVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });
        ButtonVideoPlay = (Button) ItemVideoView.findViewById(R.id.ButtonVideoPlay);
        ButtonVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewItem.start();
            }
        });
        ButtonVideoStop = (Button) ItemVideoView.findViewById(R.id.ButtonVideoStop);
        ButtonVideoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewItem.pause();
            }
        });
        ButtonVideoRewind = (Button) ItemVideoView.findViewById(R.id.ButtonVideoRewind);
        ButtonVideoRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewItem.seekTo(0);
            }
        });



        /**
         * GRABACION DE AUDIO
         */
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/tmp.3gp";

        btn_audio_start = (Button) ItemAudioView.findViewById(R.id.btn_audio_start);
        btn_audio_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(mFileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
                    mRecorder.prepare();
                    btn_audio_start.setVisibility(View.GONE);
                    btn_audio_stop.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Log.e("ERROR AL GRABAR", "prepare() failed");
                }
                mRecorder.start();
            }
        });

        btn_audio_stop = (Button) ItemAudioView.findViewById(R.id.btn_audio_stop);
        btn_audio_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                btn_audio_stop.setVisibility(View.GONE);
                btn_audio_start.setVisibility(View.VISIBLE);
                btn_audio_play.setVisibility(View.VISIBLE);
            }
        });

        btn_audio_play = (Button) ItemAudioView.findViewById(R.id.btn_audio_play);
        btn_audio_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e("ERROR AL REPR AUDIO", "prepare() failed");
                }

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
        if (requestCode == REQUEST_VIDEO_CAPTURE){
            Uri videoUri = data.getData();
            VideoViewItem.setVideoURI(videoUri);
            LinearLayoutVideo.setVisibility(View.VISIBLE);
            VideoViewItem.setVisibility(View.VISIBLE);
        }
        if(requestCode == TAKE_PICTURE) {
            Bitmap ImageBitmapDecoded = BitmapFactory.decodeFile(ImageName);
            ImageButtonFoto.setImageBitmap(ImageBitmapDecoded);
            ImageButtonFoto.setVisibility(View.VISIBLE);
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
}
