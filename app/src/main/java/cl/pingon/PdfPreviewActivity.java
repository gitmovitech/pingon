package cl.pingon;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.PDF;
import cl.pingon.Libraries.TimerUtils;
import cl.pingon.Model.ModelKeyPairs;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblEmpProjectsDefinition;
import cl.pingon.SQLite.TblEmpProjectsHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;
import harmony.java.awt.Color;

public class PdfPreviewActivity extends AppCompatActivity {

    SharedPreferences session;
    private int ARN_ID;
    private int USU_ID;
    private String USU_NAME;
    private int LOCAL_DOC_ID;
    ArrayList<ModelKeyPairs> header = new ArrayList<>();

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
        TblEmpProjectsHelper Projectos = new TblEmpProjectsHelper(this);

        ArrayList<ModelKeyPairs> registro = new ArrayList<>();

        Cursor cr;
        Cursor cursor = Documento.getById(ID);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            Integer FRM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
            String DOC_FECHA_CREACION = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION));
            Integer DOC_EXT_ID_CLIENTE = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE));
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

            cursor = Projectos.getByCompanyId(DOC_EXT_ID_CLIENTE);
            cursor.moveToFirst();
            String coords = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.COORDINATES));
            if(coords != null){
                if(!coords.isEmpty()){
                    coords = "http://maps.google.com/maps?q="+coords;
                } else {
                    coords = "Desconocida";
                }
            } else {
                coords = "Desconocida";
            }
            header.add(new ModelKeyPairs("Ubicación",coords,"texto"));
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
        }

        return registro;

    }
    PDF pdf;
    private void genPDF(final ArrayList<ModelKeyPairs> registros){
        try {

            /**
             * OBTENER NOMBRE DEL AREA DE NEGOCIO
             */
            TblFormulariosHelper Formularios = new TblFormulariosHelper(getApplicationContext());
            Cursor cursor = Formularios.getByArnId(ARN_ID);
            cursor.moveToFirst();
            String ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
            cursor.close();

            /**
             * OBTENER NOMBRE DEL CLIENTE
             */
            TblDocumentoHelper Documentos = new TblDocumentoHelper(getApplicationContext());
            cursor = Documentos.getById(LOCAL_DOC_ID);
            cursor.moveToFirst();
            String NOMBRE_CLIENTE = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
            String NOMBRE_OBRA = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));
            String NOMBRE_EQUIPO = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO));
            cursor.close();

            pdf = new PDF(this, LOCAL_DOC_ID+"__"+ARN_NOMBRE+" - "+NOMBRE_CLIENTE+" - "+NOMBRE_OBRA+" - "+NOMBRE_EQUIPO+".pdf");
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

                    } else {
                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));
                        tabla.addCell(pdf.addCell(registros.get(i).getValue()));
                    }
                }
            }
            pdf.add(tabla);
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
