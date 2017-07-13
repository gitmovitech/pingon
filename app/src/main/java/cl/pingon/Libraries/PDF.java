package cl.pingon.Libraries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
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

import cl.pingon.R;
import repack.org.bouncycastle.jce.exception.ExtIOException;

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
        this.documento = new Document();
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

    public Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        newHeight = (newWidth*height) / width;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public Image addPhoto(String path, int width, int height) throws DocumentException, IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = getResizedBitmap(bitmap, 480, 640);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Image imagen = null;
        try{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imagen = Image.getInstance(stream.toByteArray());
            if(bitmap.getWidth() > bitmap.getHeight()){
                height = (int) ((width*imagen.getHeight())/ imagen.getWidth());
            } else {
                width = (int) ((height*imagen.getWidth())/imagen.getHeight());
            }
            imagen.scaleAbsoluteWidth(width);
            imagen.scaleAbsoluteHeight(height);
        } catch(Exception e){
            Log.e("ERROR IMAGE", e.toString());
        }
        //documento.add(imagen);
        return imagen;
    }

    public Image addSign(ImageView ImageView, String points, int width, int height) throws DocumentException, IOException {
        DrawSign DrawSign = new DrawSign(points);
        DrawSign.DrawToImageView(ImageView);

        BitmapDrawable drawable = (BitmapDrawable) ImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Image imagen = null;
        try{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imagen = Image.getInstance(stream.toByteArray());
            imagen.scaleAbsoluteWidth(width);
            imagen.scaleAbsoluteHeight(height);
        } catch(Exception e){
            Log.e("ERROR IMAGE", e.toString());
        }

        File filename = new File(getRuta(), "firma.jpg");
        FileOutputStream fos = new FileOutputStream(filename);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.flush();
        fos.close();

        imagen.setAlignment(Image.LEFT);
        return imagen;
        //documento.add(imagen);
    }

    public PdfPCell addCellColor(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorderColor(harmony.java.awt.Color.LIGHT_GRAY);
        cell.setBackgroundColor(harmony.java.awt.Color.LIGHT_GRAY);
        cell.setPaddingLeft(10);
        return cell;
    }

    public PdfPCell addCell(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorderColor(harmony.java.awt.Color.WHITE);
        cell.setBackgroundColor(harmony.java.awt.Color.WHITE);
        cell.setPaddingLeft(10);
        return cell;
    }

    public PdfPCell addCell(Image image){
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(harmony.java.awt.Color.WHITE);
        cell.setBackgroundColor(harmony.java.awt.Color.WHITE);
        cell.addElement(image);
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

    public void add(Chunk el) throws DocumentException {
        documento.add(el);
    }

    public String getPath(){
        return getRuta()+"/"+NOMBRE_DOCUMENTO;
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
            ruta = new File(Environment.getExternalStorageDirectory() + "/Pingon/pdfs");

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
