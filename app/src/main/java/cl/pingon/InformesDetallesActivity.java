package cl.pingon;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import cl.pingon.Adapter.AdapterChecklist;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

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
    TextView TextViewSegundos;
    CountDownTimer countDowntimer;

    SharedPreferences session;
    TblChecklistHelper Checklist;
    AdapterChecklist AdapterChecklist;
    FloatingActionButton fabsave;
    AlertDialog.Builder alert;

    Integer FRM_ID;
    Integer CHK_ID;
    Integer ARN_ID;

    private int CAM_ID;
    private int CAM_POSICION;
    private String CAM_NOMBRE_INTERNO;
    private String CAM_NOMBRE_EXTERNO;
    private String CAM_TIPO;
    private String CAM_MANDATORIO;
    private String CAM_VAL_DEFECTO;
    private String CAM_PLACE_HOLDER;

    private int LOCAL_DOC_ID;
    private int REG_ID;
    private int USU_ID;

    Integer DOC_EXT_ID_CLIENTE;
    Integer DOC_EXT_ID_PROYECTO;
    String DOC_EXT_OBRA;
    String DOC_EXT_EQUIPO;
    String DOC_EXT_MARCA_EQUIPO;
    String DOC_EXT_NUMERO_SERIE;
    String DOC_EXT_NOMBRE_CLIENTE;

    private EditText EditText;

    TblDocumentoHelper Documentos;
    TblRegistroHelper Registros;
    ContentValues InsertValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes_detalles);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle(getIntent().getStringExtra("FRM_NOMBRE"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("CHK_NOMBRE"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        session = getSharedPreferences("session", Context.MODE_PRIVATE);

        alert = new AlertDialog.Builder(this);

        USU_ID = Integer.parseInt(session.getString("user_id", ""));
        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));
        FRM_ID = getIntent().getIntExtra("FRM_ID", 0);
        CHK_ID = getIntent().getIntExtra("CHK_ID", 0);
        LOCAL_DOC_ID = getIntent().getIntExtra("LOCAL_DOC_ID", 0);
        REG_ID = getIntent().getIntExtra("REG_ID", 0);

        DOC_EXT_ID_CLIENTE = getIntent().getIntExtra("DOC_EXT_ID_CLIENTE", 0);
        DOC_EXT_ID_PROYECTO = getIntent().getIntExtra("DOC_EXT_ID_PROYECTO", 0);
        DOC_EXT_OBRA = getIntent().getStringExtra("DOC_EXT_OBRA");
        DOC_EXT_EQUIPO = getIntent().getStringExtra("DOC_EXT_EQUIPO");
        DOC_EXT_MARCA_EQUIPO = getIntent().getStringExtra("DOC_EXT_MARCA_EQUIPO");
        DOC_EXT_NUMERO_SERIE = getIntent().getStringExtra("DOC_EXT_NUMERO_SERIE");
        DOC_EXT_NOMBRE_CLIENTE = getIntent().getStringExtra("DOC_EXT_NOMBRE_CLIENTE");

        Checklist = new TblChecklistHelper(this);
        Cursor cursor = Checklist.getAllByFrmIdAndChkId(FRM_ID, CHK_ID);
        ArrayList<ModelChecklistFields> ArrayChecklist = new ArrayList<ModelChecklistFields>();

        int index = 0;
        while (cursor.moveToNext()) {

            CAM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
            CAM_POSICION = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_POSICION));
            CAM_NOMBRE_INTERNO = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO));
            CAM_NOMBRE_EXTERNO = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO));
            CAM_TIPO = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_TIPO));
            CAM_MANDATORIO = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_MANDATORIO));
            CAM_VAL_DEFECTO = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_VAL_DEFECTO));
            CAM_PLACE_HOLDER = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_PLACE_HOLDER));

            ArrayChecklist.add(index, new ModelChecklistFields(
                    CAM_ID,
                    CAM_POSICION,
                    CAM_NOMBRE_INTERNO,
                    CAM_NOMBRE_EXTERNO,
                    CAM_TIPO,
                    CAM_MANDATORIO,
                    CAM_VAL_DEFECTO,
                    CAM_PLACE_HOLDER
            ));
            index++;
        }
        cursor.close();

        ListView ListViewInformesDetalles = (ListView) findViewById(R.id.ListViewInformesDetalles);
        AdapterChecklist = new AdapterChecklist(this, ArrayChecklist, this){};
        ListViewInformesDetalles.setAdapter(AdapterChecklist);

        ImageName = Environment.getExternalStorageDirectory() + "/pingon-foto-";

        Documentos = new TblDocumentoHelper(this);
        Registros = new TblRegistroHelper(this);
        InsertValues = new ContentValues();

        fabsave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InsertValues.put(TblDocumentoDefinition.Entry.USU_ID, USU_ID);
                InsertValues.put(TblDocumentoDefinition.Entry.FRM_ID, FRM_ID);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE, DOC_EXT_ID_CLIENTE);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO, DOC_EXT_ID_PROYECTO);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_OBRA, DOC_EXT_OBRA);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO, DOC_EXT_EQUIPO);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO, DOC_EXT_MARCA_EQUIPO);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE, DOC_EXT_NUMERO_SERIE);
                InsertValues.put(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE, DOC_EXT_NOMBRE_CLIENTE);
                InsertValues.put(TblDocumentoDefinition.Entry.SEND_STATUS, "DRAFT");

                int add = 1;
                ArrayList<ModelChecklistFields> data = AdapterChecklist.getChecklistData();
                for(int x = 0; x < data.size(); x++){
                    switch (data.get(x).getCAM_TIPO()){
                        case "email":
                            View WidgetView = data.get(x).getView();
                            EditText = (EditText) WidgetView.findViewById(R.id.texto_input);
                            if(data.get(x).getCAM_MANDATORIO().equals("S") && !EditText.getText().toString().contains("@")){
                                EditText.setError("Este campo es requerido y debe ser un correo válido");
                                EditText.requestFocus();
                                add = 0;
                            } else {
                                data.get(x).setValue(EditText.getText().toString());
                            }
                            break;
                        default:
                            Log.d("DATA FAB BUTTON", String.valueOf(data.get(x).getCAM_ID())+" - "+data.get(x).getCAM_NOMBRE_INTERNO()+" - "+data.get(x).getCAM_TIPO()+" - "+data.get(x).getCAM_MANDATORIO()+" - "+data.get(x).getValue());
                            break;
                    }
                }

                if(add == 1){
                    int ID = Documentos.insert(InsertValues);
                    session.edit().putInt("LOCAL_DOC_ID", ID).commit();

                    for(int x = 0; x < data.size(); x++){
                        if(data.get(x).getValue() != null){
                            InsertValues = new ContentValues();
                            InsertValues.put(TblRegistroDefinition.Entry.LOCAL_DOC_ID, ID);
                            InsertValues.put(TblRegistroDefinition.Entry.CAM_ID, data.get(x).getCAM_ID());
                            InsertValues.put(TblRegistroDefinition.Entry.FRM_ID, FRM_ID);
                            InsertValues.put(TblRegistroDefinition.Entry.REG_TIPO, data.get(x).getCAM_TIPO());
                            InsertValues.put(TblRegistroDefinition.Entry.SEND_STATUS, "DRAFT");
                            InsertValues.put(TblRegistroDefinition.Entry.REG_VALOR, data.get(x).getValue());
                            Registros.insert(InsertValues);
                        }
                    }

                    Cursor cursor = Registros.getAll();
                    while (cursor.moveToNext()){
                        Log.d("REGISTRO", cursor.getString(cursor.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
                    }
                    finish();
                }
            }
        });

        /*IntentSign = new Intent(this, SignDrawActivity.class);



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
         *
        Button ButtonFirma = (Button) ItemFirmaView.findViewById(R.id.item_firma);
        ButtonFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentSign);
            }
        });


        /**
         * VIDEO
         *
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
         *
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/tmp.3gp";

        TextViewSegundos = (TextView) ItemAudioView.findViewById(R.id.TextViewSegundos);
        btn_audio_start = (Button) ItemAudioView.findViewById(R.id.btn_audio_start);
        btn_audio_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] seconds = {0};
                countDowntimer = new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        seconds[0]++;
                        TextViewSegundos.setText(seconds[0]+" segundos grabados");
                    }
                    public void onFinish() {
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;

                        btn_audio_stop.setVisibility(View.GONE);
                        btn_audio_start.setVisibility(View.VISIBLE);
                        btn_audio_play.setVisibility(View.VISIBLE);
                        TextViewSegundos.setText("10 segundos grabados");
                    }};countDowntimer.start();
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(mFileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setMaxDuration(10000);
                try {
                    mRecorder.prepare();
                    btn_audio_start.setVisibility(View.GONE);
                    btn_audio_stop.setVisibility(View.VISIBLE);
                    TextViewSegundos.setVisibility(View.VISIBLE);
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



    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }*/
    }

    public void showPhoto(int index){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(ImageName+index+".jpg")), "image/*");
        startActivity(intent);
    }

    public void setCameraIntentAction(int index){
        setRowItemIndex(index);
        CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(ImageName+index+".jpg")));
        startActivityForResult(CameraIntent, TAKE_PICTURE);
    }

    int RowItemIndex = 0;
    public void setRowItemIndex(int index){
        this.RowItemIndex = index;
    }

    public int getRowItemIndex(){
        return this.RowItemIndex;
    }

    private Bitmap ImageThumb(Bitmap Image){
        int size = 400;
        int width = Image.getWidth();
        int height = Image.getHeight();
        int nwidth = 0;
        int nheight = 0;

        if(height >= width){
            nheight = (height*size) / width;
            nwidth = size;
        } else {
            nwidth = (width*size) / height;
            nheight = size;
        }

        Image = Bitmap.createScaledBitmap(Image, nwidth, nheight, false);
        return Image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == TAKE_PICTURE) {
            Bitmap ImageBitmapDecoded = ImageThumb(BitmapFactory.decodeFile(ImageName+RowItemIndex+".jpg"));
            AdapterChecklist.setImageButton(ImageBitmapDecoded, RowItemIndex);
        }

        /*if (requestCode == REQUEST_VIDEO_CAPTURE){
            Uri videoUri = data.getData();
            VideoViewItem.setVideoURI(videoUri);
            LinearLayoutVideo.setVisibility(View.VISIBLE);
            VideoViewItem.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
