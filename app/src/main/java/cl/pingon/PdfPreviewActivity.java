package cl.pingon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.ArrayList;

import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.PDF;
import cl.pingon.Libraries.TimerUtils;
import cl.pingon.Model.ModelKeyPairs;
import cl.pingon.SQLite.TblChecklistDefinition;
import cl.pingon.SQLite.TblChecklistHelper;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblRegistroDefinition;
import cl.pingon.SQLite.TblRegistroHelper;
import harmony.java.awt.Color;

public class PdfPreviewActivity extends AppCompatActivity {

    SharedPreferences session;
    private int ARN_ID;
    private int USU_ID;
    private int LOCAL_DOC_ID;

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
        LOCAL_DOC_ID = getIntent().getIntExtra("LOCAL_DOC_ID", 0);
        LOCAL_DOC_ID = 4;
        ArrayList<ModelKeyPairs> registros = getDocumentData(LOCAL_DOC_ID);
        genPDF(registros);
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
        inflater.inflate(R.menu.menu_preview_pdf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ButtonSend:
                //TODO: Cambiar status de documentos y registros para proceder a enviar
                Snackbar.make(findViewById(R.id.preview_pdf), "Este botón enviara el informe a la seccion de la bandeja de salida, detectara si hay conexión y comenzara el proceso de sincronizacion para subir toda la información", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<ModelKeyPairs> getDocumentData(int ID){
        TblDocumentoHelper Documento = new TblDocumentoHelper(this);
        TblRegistroHelper Registro = new TblRegistroHelper(this);
        TblChecklistHelper Checklist = new TblChecklistHelper(this);

        ArrayList<ModelKeyPairs> registro = new ArrayList<>();

        Cursor cr;
        Cursor cursor = Documento.getById(ID);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            Integer FRM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
            cursor.close();

            Integer CAM_ID;
            String CAM_NAME;
            String CAM_TIPO;
            cursor = Checklist.getByFrmId(FRM_ID);
            //TODO: Programar cursor checklist y luego combinar con los registros
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

    private void genPDF(ArrayList<ModelKeyPairs> registros){
        try {
            final PDF pdf = new PDF(this, "informe.pdf");
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
            tabla.addCell(pdf.addCellColor("Nombre del formulario"));
            tabla.addCell(pdf.addCellColor("Orden de Trabajo de Grúa"));
            tabla.addCell(pdf.addCellColor("Remitente"));
            tabla.addCell(pdf.addCellColor("Jorge Ramirez"));
            tabla.addCell(pdf.addCellColor("Número de referencia"));
            tabla.addCell(pdf.addCellColor("OTAOTE"));
            tabla.addCell(pdf.addCellColor("Ubicación"));
            tabla.addCell(pdf.addCellColor("Desconocida"));
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
                        pdf.addSignToCell((ImageView) findViewById(R.id.ImageViewFirma), registros.get(i).getValue(), 150, 150);
                    } else{
                        tabla.addCell(pdf.addCell(registros.get(i).getKey()));
                        tabla.addCell(pdf.addCell(registros.get(i).getValue()));
                    }
                }
            }
            pdf.add(tabla);

            pdf.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
