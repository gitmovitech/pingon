package cl.pingon.Libraries;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Cell;
import com.lowagie.text.pdf.draw.DottedLineSeparator;
import com.lowagie.text.pdf.draw.LineSeparator;

public class PDF {

    private static String NOMBRE_DOCUMENTO = null;
    private final static String NOMBRE_DIRECTORIO = "PingonPDF";
    Document documento;
    Context context;

    /**
     * Crear nuevo documento PDF
     * @param context
     * @param docname
     * @throws IOException
     * @throws DocumentException
     */
    public PDF(Context context, String docname) throws IOException, DocumentException {
        NOMBRE_DOCUMENTO = docname;
        this. documento = new Document();
        this.context = context;
        File f = crearFichero(docname);
        FileOutputStream ficheroPdf = new FileOutputStream(f.getAbsolutePath());
        PdfWriter.getInstance(documento, ficheroPdf);
    }

    /**
     * Abrir documento para escritura
     */
    public void open(){
        documento.open();
    }

    /**
     * Cerrar documento y escribir en el archivo
     */
    public void close(){
        documento.close();
    }

    /**
     * Agregar una imagen al PDF
     * @param DrawableImagen
     * @param width
     * @param height
     * @throws DocumentException
     * @throws IOException
     */
    public void addImage(int DrawableImagen, int width, int height) throws DocumentException, IOException {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), DrawableImagen);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Image imagen = Image.getInstance(stream.toByteArray());
        imagen.scaleAbsoluteWidth(width);
        imagen.scaleAbsoluteHeight(height);
        documento.add(imagen);
    }

    public PdfPCell addCell(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorderColor(harmony.java.awt.Color.LIGHT_GRAY);
        cell.setBackgroundColor(harmony.java.awt.Color.LIGHT_GRAY);
        cell.setPaddingLeft(10);
        return cell;
    }


    public PdfPTable createTable(int columns) throws DocumentException {
        PdfPTable tabla = new PdfPTable(columns);
        return tabla;
    }

    public void add(Paragraph p) throws DocumentException {
        documento.add(p);
    }

    public void add(PdfPTable tabla) throws DocumentException {
        documento.add(tabla);
    }


    private static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
    }

    private static File getRuta() {

        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            ruta = new File(
                    Environment
                            .getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS),
                    NOMBRE_DIRECTORIO);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        } else {
        }

        return ruta;
    }
}
