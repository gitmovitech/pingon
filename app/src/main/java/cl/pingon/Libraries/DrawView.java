package cl.pingon.Libraries;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import cl.pingon.Model.SignPoints;

public class DrawView extends View {

    private Paint paint;
    private Path path = new Path();
    private Canvas Canvas;
    private ArrayList<SignPoints> SignPoints;

    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        SignPoints = new ArrayList<SignPoints>();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
        Canvas = canvas;
    }

    public ArrayList<SignPoints> getPath(){
        return SignPoints;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                SignPoints.add(new SignPoints(0, 0));
                SignPoints.add(new SignPoints(pointX, pointY));
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                SignPoints.add(new SignPoints(pointX, pointY));
                break;
            default:
                return false;
        }

        postInvalidate();
        return true;
    }

}