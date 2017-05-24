package cl.pingon.Libraries;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class DrawSign {

    String sign;
    Integer minX = 5000;
    Integer maxX = 0;
    Integer minY = 5000;
    Integer maxY = 0;

    Bitmap image;

    public DrawSign(String sign){
        this.sign = cleanString(sign);
    }

    public DrawSign(){

    }

    public String base64FromFile(String path){
        Bitmap bm = BitmapFactory.decodeFile(path);
        bm = getResizedBitmap(bm, 0, 1024);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
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

    public Bitmap toBitmap(){
        return createSign();
    }

    public void DrawToImageView(ImageView imageview){
        imageview.setImageBitmap(createSign());
    }

    public Bitmap createSign(){
        Path path = convertToPath(sign);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        int width = maxX + minX;
        int height = maxY + minY;

        try {
            image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            canvas.drawColor(Color.WHITE);
            canvas.drawPath(path, paint);
        } catch (Exception e){ }

        return image;
    }

    public String convertToBase64(){
        String out = "";
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            out = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e){}

        return out;
    }

    private Path convertToPath(String sign){
        int x;
        int y;
        String[] signArray = sign.split(",");
        String[] signItem;

        Path path = new Path();

        try {
            int next = 0;
            for (int i = 0; i < signArray.length; i++) {
                signItem = signArray[i].split(":");
                x = Integer.parseInt(signItem[0]);
                y = Integer.parseInt(signItem[1]);
                if (y == 0 && x == 0) {
                    next = 1;
                } else {
                    if (next == 1) {
                        path.moveTo(x, y);
                        next = 0;
                    }
                    path.lineTo(x, y);
                    setMinMax(x, y);
                }
            }
        }catch (Exception e){

        }
        return path;
    }

    private void setMinMax(int x, int y){
        if(x < minX){
            minX = x;
        }
        if(y < minY){
            minY = y;
        }
        if(x > maxX){
            maxX = x;
        }
        if(y > maxY){
            maxY = y;
        }
    }

    private String cleanString(String sign){
        sign = sign.replace("[","");
        sign = sign.replace("]","");
        sign = sign.replace("{","");
        sign = sign.replace("}","");
        sign = sign.replace("\"","");
        return sign;
    }

}
