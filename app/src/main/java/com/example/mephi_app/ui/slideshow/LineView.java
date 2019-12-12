package com.example.mephi_app.ui.slideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;

public class LineView extends View {

    private int mHeight;
    private int mWidth;
    MainActivity ma;
    float [] points = null;

    private Paint mPaint = new Paint();

    public LineView(Context context) {
        super(context);
        ma = (MainActivity)context;
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void MyInvalidate(float [] pts){
        this.points = pts;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);




        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        Rect s = new Rect (0,0,700,577);
        Rect r = new Rect(0, 0,720,590);
        canvas.drawBitmap(image,null,r,null);
        mPaint.setStrokeWidth(7);
        //canvas.drawLine(0, 0, 720, 590, mPaint);
        if (points != null){
            canvas.drawLines(points, mPaint);
            canvas.drawLines(points, 2, points.length-2, mPaint);
        }
    }
}