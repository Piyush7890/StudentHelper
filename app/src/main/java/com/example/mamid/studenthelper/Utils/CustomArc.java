package com.example.mamid.studenthelper.Utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

public class CustomArc extends View {
    Paint outlinePaint;
    Paint TopPaint;
    float progress=0;
    float outeroffset;

    public CustomArc(Context context) {
        this(context,null,0,0);
    }

    public CustomArc(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0,0);
    }

    public CustomArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public CustomArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        outlinePaint = new Paint();
        TopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setStyle(Paint.Style.STROKE);
        TopPaint.setStyle(Paint.Style.STROKE);
        TopPaint.setStrokeCap(Paint.Cap.ROUND);
        outlinePaint.setStrokeWidth(getResources().getDisplayMetrics().density*4);
        outeroffset = getResources().getDisplayMetrics().density*3;
        TopPaint.setStrokeWidth(getResources().getDisplayMetrics().density*6);
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setAlpha(30);
        TopPaint.setColor(Color.WHITE);


    }
    public void setProgress( float prog)
    {
        ValueAnimator anim = ValueAnimator.ofFloat(0,prog);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.setDuration(1000L);
        anim.setStartDelay(200L);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,
                getHeight()/2,
                getWidth()/2-(outeroffset),
                outlinePaint);


        canvas.drawArc(0+outeroffset,
                0+outeroffset,
                getWidth()-outeroffset,
                getBottom()-outeroffset,
                -90,
                progress*360,
                false,TopPaint);
    }

}


