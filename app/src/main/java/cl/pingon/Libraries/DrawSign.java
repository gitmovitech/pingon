package cl.pingon.Libraries;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;

public class DrawSign {

    String sign;
    Integer minX = 5000;
    Integer maxX = 0;
    Integer minY = 5000;
    Integer maxY = 0;

    public DrawSign(String sign){
        this.sign = cleanString(sign);
    }

    public void DrawToImageView(ImageView imageview){
        Path path = convertToPath(sign);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        int width = maxX;
        int height = maxY;

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawPath(path, paint);

        imageview.setImageBitmap(image);
    }

    private Path convertToPath(String sign){
        int x;
        int y;
        String[] signArray = sign.split(",");
        String[] signItem;

        Path path = new Path();

        int next = 0;
        for(int i = 0; i < signArray.length;i++){
            signItem = signArray[i].split(":");
            x = Integer.parseInt(signItem[0]);
            y = Integer.parseInt(signItem[1]);
            if(y == 0 && x == 0){
                next = 1;
            } else {
                if(next == 1){
                    path.moveTo(x, y);
                    next = 0;
                }
                path.lineTo(x, y);
                setMinMax(x, y);
            }
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
