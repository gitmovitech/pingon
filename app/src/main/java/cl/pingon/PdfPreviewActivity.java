package cl.pingon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.Toast;

import com.lowagie.text.Cell;
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

import cl.pingon.Libraries.PDF;
import cl.pingon.SQLite.TblDocumentoHelper;
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

        getDocumentData(LOCAL_DOC_ID);

        genPDF();
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

    private void getDocumentData(int ID){
        TblDocumentoHelper Documento = new TblDocumentoHelper(this);
        Cursor cursor = Documento.getById(ID);
        cursor.moveToFirst();

        cursor.close();
    }

    private void genPDF(){
        try {
            PDF pdf = new PDF(this, "informe.pdf");
            pdf.open();
            pdf.addImage(R.drawable.pingon_pdf, 100, 80);

            Paragraph p = new Paragraph("Información del formulario");
            LineSeparator line = new LineSeparator();
            line.setOffset(-2);
            p.add(line);
            pdf.add(p);

            PdfPTable tabla = pdf.createTable(2);
            tabla.setWidthPercentage(98);

            tabla.addCell(pdf.addCell(""));
            tabla.addCell(pdf.addCell(""));
            tabla.addCell(pdf.addCell("Nombre del formulario"));
            tabla.addCell(pdf.addCell("Orden de Trabajo de Grúa"));
            tabla.addCell(pdf.addCell("Remitente"));
            tabla.addCell(pdf.addCell("Jorge Ramirez"));
            tabla.addCell(pdf.addCell("Número de referencia"));
            tabla.addCell(pdf.addCell("OTAOTE"));
            tabla.addCell(pdf.addCell("Ubicación"));
            tabla.addCell(pdf.addCell("Desconocida"));
            tabla.addCell(pdf.addCell(""));
            tabla.addCell(pdf.addCell(""));


            pdf.add(tabla);

            pdf.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
