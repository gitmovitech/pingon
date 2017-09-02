package cl.pingon.Libraries;


import android.graphics.Bitmap;

public class ImageUtils {

    public ImageUtils(){

    }

    public Bitmap ImageThumb(Bitmap Image){
        int size = 100;
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
}
