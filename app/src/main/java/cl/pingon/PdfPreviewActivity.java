package cl.pingon;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.pingon.Libraries.PDF;
import cl.pingon.Libraries.TimerUtils;
import cl.pingon.Model.ModelKeyPairs;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblEmpCompanyDefinition;
import cl.pingon.SQLite.TblEmpCompanyHelper;
import cl.pingon.SQLite.TblEmpProjectsDefinition;
import cl.pingon.SQLite.TblEmpProjectsHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;

public class PdfPreviewActivity extends AppCompatActivity {

    SharedPreferences session;
    private int ARN_ID;
    private int USU_ID;
    private String USU_NAME;
    private int LOCAL_DOC_ID;
    ArrayList<ModelKeyPairs> header = new ArrayList<>();
    private String android_id;
    String DOC_EXT_OBRA;
    String DOC_EXT_NOMBRE_CLIENTE;
    String RUT_CLIENTE;
    String COMUNA_OBRA;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_preview);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        this.setTitle("Previsualización del informe");
        getSupportActionBar().setSubtitle("Generación del documento PDF");

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        alert = new AlertDialog.Builder(this);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        ARN_ID = Integer.parseInt(session.getString("arn_id", ""));
        USU_ID = Integer.parseInt(session.getString("user_id", ""));
        USU_NAME = session.getString("first_name", "")+" "+session.getString("last_name", "");
        LOCAL_DOC_ID = getIntent().getIntExtra("LOCAL_DOC_ID", 0);
        Log.d("LOCALDOCID",":"+ LOCAL_DOC_ID);
        //LOCAL_DOC_ID = 1;
        final ArrayList<ModelKeyPairs> registros = getDocumentData(LOCAL_DOC_ID);

        TimerUtils.TaskHandle handle = TimerUtils.setTimeout(new Runnable() {
            public void run() {
                genPDF(registros);
            }
        }, 500);

        Button EnviarInforme = (Button) findViewById(R.id.EnviarInforme);
        EnviarInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TblDocumentoHelper Documento = new TblDocumentoHelper(getApplicationContext());
                TblRegistroHelper Registro = new TblRegistroHelper(getApplicationContext());
                ContentValues cv = new ContentValues();
                cv.put(TblDocumentoDefinition.Entry.SEND_STATUS, "SYNC");
                Documento.update(LOCAL_DOC_ID, cv);
                cv = new ContentValues();
                cv.put(TblRegistroDefinition.Entry.SEND_STATUS, "SYNC");
                Registro.updateLocalDocId(LOCAL_DOC_ID, cv);

                Intent intent = new Intent(getApplicationContext(), BuzonActivity.class);
                intent.putExtra("GOTO", "PendientesEnvioActivity");
                startActivity(intent);
                finish();

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

    private ArrayList<ModelKeyPairs> getDocumentData(int ID){
        TblDocumentoHelper Documento = new TblDocumentoHelper(this);
        TblRegistroHelper Registro = new TblRegistroHelper(this);
        TblChecklistHelper Checklist = new TblChecklistHelper(this);
        TblFormulariosHelper Formularios = new TblFormulariosHelper(this);
        TblEmpCompanyHelper Clientes = new TblEmpCompanyHelper(this);
        TblEmpProjectsHelper Projectos = new TblEmpProjectsHelper(this);

        ArrayList<ModelKeyPairs> registro = new ArrayList<>();

        Cursor cr;
        Cursor cursor = Documento.getById(ID);

        if(cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                Integer FRM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
                String DOC_FECHA_CREACION = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION));
                Integer DOC_EXT_ID_CLIENTE = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE));
                Integer PROJECT_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO));
                DOC_EXT_OBRA = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));
                DOC_EXT_NOMBRE_CLIENTE = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
                header.add(new ModelKeyPairs("Cliente", DOC_EXT_NOMBRE_CLIENTE, "texto"));
                header.add(new ModelKeyPairs("Obra", DOC_EXT_OBRA, "texto"));
                String DOC_EXT_MARCA_EQUIPO = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO));
                String DOC_EXT_EQUIPO = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO));
                String DOC_EXT_NUMERO_SERIE = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE));
                header.add(new ModelKeyPairs("Marca", DOC_EXT_MARCA_EQUIPO, "texto"));
                header.add(new ModelKeyPairs("Modelo", DOC_EXT_EQUIPO, "texto"));
                header.add(new ModelKeyPairs("Serie", DOC_EXT_NUMERO_SERIE, "texto"));

                cursor.close();


                cursor = Clientes.getById(String.valueOf(DOC_EXT_ID_CLIENTE));
                cursor.moveToFirst();
                RUT_CLIENTE = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.RUT));
                cursor.close();


                cursor = Formularios.getByArnIdFrmId(ARN_ID, FRM_ID);
                cursor.moveToFirst();
                String FRM_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE));
                header.add(new ModelKeyPairs("Nombre del formulario",FRM_NOMBRE,"texto"));
                cursor.close();
                header.add(new ModelKeyPairs("Remitente",USU_NAME,"texto"));


                String[] nombre_array = FRM_NOMBRE.split(" ");
                String numero_referencia = "";
                for(int f = 0; f < nombre_array.length; f++){
                    numero_referencia += nombre_array[f].charAt(0);
                }
                Date time = new Date();
                header.add(new ModelKeyPairs("Número de referencia",numero_referencia+"-"+DOC_FECHA_CREACION.replace("-","")+"-"+String.valueOf(time.getTime()),"texto"));

                cursor = Projectos.getById(PROJECT_ID);
                cursor.moveToFirst();
                COMUNA_OBRA = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ADDRESS));
                header.add(new ModelKeyPairs("Ubicación",COMUNA_OBRA,"texto"));
                cursor.close();


                Integer CAM_ID;
                String CAM_NAME;
                String CAM_TIPO;
                cursor = Checklist.getByFrmId(FRM_ID);
                while(cursor.moveToNext()){
                    CAM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_ID));
                    CAM_NAME = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO));
                    CAM_TIPO = cursor.getString(cursor.getColumnIndexOrThrow(TblChecklistDefinition.Entry.CAM_TIPO));

                    if(CAM_TIPO.contains("etiqueta")){
                        registro.add(new ModelKeyPairs(CAM_NAME, "", CAM_TIPO));
                    } else {
                        cr = Registro.getDraftsByFrmId(FRM_ID);
                        if (cr.getCount() > 0) {
                            while (cr.moveToNext()) {
                                if (CAM_ID == cr.getInt(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID))) {
                                    registro.add(new ModelKeyPairs(
                                            CAM_NAME,
                                            cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)),
                                            cr.getString(cr.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO))
                                    ));
                                    break;
                                }
                            }
                        }
                        cr.close();
                    }
                }
                cursor.close();

            } catch (Exception e){

                alert.setTitle("Error de Base de datos");
                alert.setMessage(e.toString());
                alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create();
                alert.show();

            }
        }

        Documento.close();
        Clientes.close();
        Checklist.close();
        Formularios.close();
        Projectos.close();
        Registro.close();

        return registro;

    }
    PDF pdf;
    String pdfFilename;
    private void genPDF(final ArrayList<ModelKeyPairs> registros){
        try {

            String[] videofile;
            String responsable = "";
            String rut_responsable = "";

            /**
             * OBTENER NOMBRE DEL AREA DE NEGOCIO
             */
            /*TblFormulariosHelper Formularios = new TblFormulariosHelper(getApplicationContext());
            Cursor cursor = Formularios.getByArnId(ARN_ID);
            cursor.moveToFirst();
            String FRM_DECLARACION = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_DECLARACION));
            String ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
            cursor.close();
            Formularios.close();*/

            /**
             * OBTENER NOMBRE DEL CLIENTE
             */
            TblDocumentoHelper Documentos = new TblDocumentoHelper(getApplicationContext());
            Cursor cursor = Documentos.getById(LOCAL_DOC_ID);
            cursor.moveToFirst();
            int FRM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
            /*String NOMBRE_CLIENTE = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
            int DOC_EXT_ID_CLIENTE = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE));
            String NOMBRE_OBRA = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));
            int DOC_EXT_ID_PROYECTO = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO));
            String NOMBRE_EQUIPO = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO));*/
            cursor.close();
            Documentos.close();

            TblFormulariosHelper Formularios = new TblFormulariosHelper(getApplicationContext());
            cursor = Formularios.getByArnIdFrmId(ARN_ID, FRM_ID);
            cursor.moveToFirst();
            String FRM_DECLARACION = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_DECLARACION));
            cursor.close();
            Formularios.close();


            pdfFilename = LOCAL_DOC_ID+".pdf";
            pdf = new PDF(this, pdfFilename);
            pdf.open();
            pdf.addImage(R.drawable.pingon_pdf, 100, 80);

            Paragraph p = new Paragraph("Información del formulario");
            LineSeparator line = new LineSeparator();
            line.setOffset(-2);
            p.add(line);
            pdf.add(p);

            PdfPTable tabla = pdf.createTable(2);

            pdf.add(Chunk.NEWLINE);

            tabla = pdf.createTable(2);
            tabla.setWidthPercentage(98);
            tabla.getDefaultCell().setPadding(10);
            tabla.addCell(pdf.addCellColor(""));
            tabla.addCell(pdf.addCellColor(""));
            for(int c = 0; c < header.size(); c++){
                tabla.addCell(pdf.addCellColor(header.get(c).getKey()));
                tabla.addCell(pdf.addCellColor(header.get(c).getValue()));
            }
            tabla.addCell(pdf.addCellColor(""));
            tabla.addCell(pdf.addCellColor(""));
            pdf.add(tabla);

            pdf.add(Chunk.NEWLINE);

            tabla = pdf.createTable(2);
            tabla.setWidthPercentage(100);

            for(int i = 0; i < registros.size(); i++){
                if(registros.get(i).getType().contains("etiqueta")){
                    pdf.add(tabla);
                    pdf.add(Chunk.NEWLINE);

                    p = new Paragraph(registros.get(i).getKey());
                    line = new LineSeparator();
                    line.setOffset(-2);
                    p.add(line);
                    pdf.add(p);
                    pdf.add(Chunk.NEWLINE);

                    tabla = pdf.createTable(2);
                    tabla.setWidthPercentage(100);
                } else {
                    if(registros.get(i).getType().contains("firma")){

                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));
                        tabla.addCell(pdf.addCell(pdf.addSign((ImageView) findViewById(R.id.ImageViewFirma), registros.get(i).getValue(), 150, 150)));

                    } else if(registros.get(i).getType().contains("foto")){

                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));
                        tabla.addCell(pdf.addCell(pdf.addPhoto(registros.get(i).getValue(), 250, 250)));

                    } else if(registros.get(i).getType().contains("video") || registros.get(i).getType().contains("audio")){
                        /*
                        videofile = registros.get(i).getValue().split("/");

                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));
                        tabla.addCell(pdf.addCell(getResources().getString(R.string.url_download)+"?a="+android_id+"&v="+videofile[videofile.length-1]));
                        */
                    } else if(registros.get(i).getType().equals("fecha")){

                        pdf.add(tabla);
                        pdf.add(Chunk.NEWLINE);
                        tabla = pdf.createTable(2);
                        tabla.setWidthPercentage(100);

                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));

                        String sdate = registros.get(i).getValue();
                        SimpleDateFormat spf=new SimpleDateFormat("dd-MM-yyyy");
                        Date newDate= null;
                        try {
                            newDate = spf.parse(sdate);
                            spf= new SimpleDateFormat("E, dd-MM-yyyy");
                            sdate = spf.format(newDate);

                            tabla.addCell(pdf.addCell(sdate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            tabla.addCell(pdf.addCell(registros.get(i).getValue()));
                        }

                    } else if(!registros.get(i).getType().equals("hora_total_diaria") && !registros.get(i).getType().equals("hora_total_semanal")) {
                        Log.e("TYPE TYPE", registros.get(i).getType());
                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));
                        tabla.addCell(pdf.addCell(registros.get(i).getValue()));
                    }
                }

                if(registros.get(i).getType().contains("rut_responsable")){
                    rut_responsable = registros.get(i).getValue();
                } else if(registros.get(i).getType().contains("responsable")){
                    responsable = registros.get(i).getValue();
                }
            }


            /**
             * Firma de usuario de app
             */
            String nombre = session.getString("first_name","")+" "+session.getString("last_name","");
            String firma = session.getString("sign","");
            try {
                tabla.addCell(pdf.addCell(nombre));
                ImageView IVFirma = (ImageView) findViewById(R.id.ImageViewFirma);
                byte[] decodedString = Base64.decode(firma, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                IVFirma.setImageBitmap(decodedByte);
                BitmapDrawable drawable = (BitmapDrawable) IVFirma.getDrawable();
                Bitmap bitmapsign = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapsign.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleAbsoluteWidth(150);
                imagen.scaleAbsoluteHeight(150);
                imagen.setAlignment(Image.LEFT);
                tabla.addCell(pdf.addCell(imagen));
            } catch (Exception e){
                Log.e("ERROR FIRMA", e.toString());
            }


            pdf.add(tabla);

            /**
             * Declaracion
             */

            pdf.add(Chunk.NEWLINE);
            pdf.add(Chunk.NEWLINE);


            //FECHA
            Date fecha = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
            FRM_DECLARACION = FRM_DECLARACION.replace("[fecha]", ft.format(fecha));
            //OBRA
            FRM_DECLARACION = FRM_DECLARACION.replace("[obra]", DOC_EXT_OBRA);
            FRM_DECLARACION = FRM_DECLARACION.replace("[nombre_cliente]", DOC_EXT_NOMBRE_CLIENTE);
            FRM_DECLARACION = FRM_DECLARACION.replace("[rut_cliente]", RUT_CLIENTE);
            FRM_DECLARACION = FRM_DECLARACION.replace("[comuna_obra]", COMUNA_OBRA);
            FRM_DECLARACION = FRM_DECLARACION.replace("[responsable]", responsable);
            FRM_DECLARACION = FRM_DECLARACION.replace("[rut]", rut_responsable);
            FRM_DECLARACION = FRM_DECLARACION.replace("null", "");

            p = new Paragraph(FRM_DECLARACION);
            pdf.add(p);

            pdf.close();

            openPDF();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void openPDF(){
        File file = new File(pdf.getPath());
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");

        RelativeLayout rll = (RelativeLayout) findViewById(R.id.RelativeLayoutLoading);
        rll.setVisibility(View.GONE);
        LinearLayout llo = (LinearLayout) findViewById(R.id.LinearLayoutOK);
        llo.setVisibility(View.VISIBLE);

        try {
            startActivityForResult(target, 1);
        } catch (ActivityNotFoundException e) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Error");
            alert.setMessage("No se ha podido abrir el documento. \nDescargue e instale un lector de documentos PDF.");
            alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.create();
            alert.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            ImageButton ImageButtonVerPdf = (ImageButton) findViewById(R.id.ImageButtonVerPdf);
            ImageButtonVerPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPDF();
                }
            });
        }
    }

}
