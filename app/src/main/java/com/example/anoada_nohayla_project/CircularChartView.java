package com.example.anoada_nohayla_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


public class CircularChartView extends View
{
    private Paint segmentPaint;
    private Paint backgroundPaint;
    private float percentage = 0f;
    private int color = R.color.red;
    private int backgroundColor = R.color.light_gray;

    public CircularChartView(Context context)
    {
        super(context);
        init();
    }

    public CircularChartView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CircularChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        segmentPaint.setStyle(Paint.Style.STROKE);
        segmentPaint.setStrokeCap(Paint.Cap.ROUND);
        segmentPaint.setStrokeWidth(45);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setStrokeWidth(45);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), backgroundColor));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas)
    {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2 - 40;

        RectF oval = new RectF(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        float startAngle = -90;
        float sweepAngle = (percentage / 100) * 360;

        // Draw the background part of the cycle first
        canvas.drawArc(oval, startAngle, 360, false, backgroundPaint);

        // Draw the segment representing the percentage on top
        segmentPaint.setColor(ContextCompat.getColor(getContext(), color));
        canvas.drawArc(oval, startAngle, sweepAngle, false, segmentPaint);
    }

    @SuppressLint("ResourceAsColor")
    public void setPercentageAndColor(float percentage, int colorResource, int backgroundColorResource)
    {
        if (percentage < 0)
        {
            this.percentage = 0;
        }
        else if (percentage > 100)
        {
            this.percentage = 100;
        }
        else
        {
            this.percentage = percentage;
        }
        this.color = colorResource;
        this.backgroundColor = backgroundColorResource;
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), backgroundColorResource));
        invalidate();
    }
}


/*
public class CircularChartView extends View
{
    private Paint segmentPaint;
    private Paint backgroundPaint;
    private float percentage = 0f;
    private int color = R.color.red;

    public CircularChartView(Context context)
    {
        super(context);
        init();
    }

    public CircularChartView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CircularChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        segmentPaint.setStyle(Paint.Style.STROKE);
        segmentPaint.setStrokeCap(Paint.Cap.ROUND);
        segmentPaint.setStrokeWidth(40);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.light_gray));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(40);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas)
    {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2 - 40;

        RectF oval = new RectF(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        float startAngle = -90;
        canvas.drawArc(oval, startAngle, 360, false, backgroundPaint);

        float sweepAngle = (percentage / 100) * 360;
        segmentPaint.setColor(ContextCompat.getColor(getContext(), color));
        canvas.drawArc(oval, startAngle, sweepAngle, false, segmentPaint);
    }

    @SuppressLint("ResourceAsColor")
    public void setPercentageAndColor(float percentage, int colorResource)
    {
        if (percentage < 0)
        {
            this.percentage = 0;
        }
        else if (percentage > 100)
        {
            this.percentage = 100;
        }
        else
        {
            this.percentage = percentage;
        }
        this.color = colorResource;
        invalidate();
    }
}*/
