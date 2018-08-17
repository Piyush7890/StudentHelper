package com.example.mamid.studenthelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class CustomSubjectView extends CardView {

    Paint Stroke;
    private float progress = 0f;
    private int color;
    float offset;

    public CustomSubjectView(@NonNull Context context) {
        super(context, null);
    }

    public CustomSubjectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Stroke = new Paint();
        offset = 4*context.getResources().getDisplayMetrics().density/2;
        Stroke.setStrokeWidth(offset*2);
        Stroke.setStrokeCap(Paint.Cap.ROUND);
        Stroke.setColor(Color.parseColor("#3ab9f4"));

    }

    public void setProgress(float progress,int color)
    {this.color=color;
    Stroke.setColor(color);
        this.progress = progress;
        invalidate();
    }

    public void setColor(int color)
    {
        this.color = color;
        Stroke.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0,offset,canvas.getWidth()*progress,offset, Stroke);

    }
}
