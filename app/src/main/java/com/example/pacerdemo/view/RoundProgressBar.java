package com.example.pacerdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.pacerdemo.R;

public class RoundProgressBar extends View {

    public static final String KEY_PROGRESS="key_progress";
    public static final String INSTANCE="instance";

    private int mRadius;
    private int mColor;
    private int mLineWidth;
    private int mProgress;


    private Paint mPaint;
    private RectF mRectF;

    private int mBackgroudColor = 0xFFF0EEDF;
    private int mBorderColor = 0xFFD2D1C4;
    private int[] mArcColor = new int[]{0xFF02C016, 0xFF3DF346, 0xFF40F1D5, 0xFF02C016};
    private SweepGradient mSweepGradient;

    private EmbossMaskFilter mEmboss;
    private BlurMaskFilter mBlur;
    float[] direction = new float[] {1,1,1};
    float light = 0.4f;
    float specular = 6;
    float blur = 3.5f;




    public RoundProgressBar(Context context) {
        super(context);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);


        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBar_radius,dp2dx(30));
        mColor = ta.getColor(R.styleable.RoundProgressBar_color,0xffff0000);
        mLineWidth = (int) ta.getDimension(R.styleable.RoundProgressBar_lineWidth,dp2dx(3));
        mProgress = (int) ta.getInt(R.styleable.RoundProgressBar_android_progress,30);


        ta.recycle();
        initPaint();
    }

    private float dp2dx(int dpVal) {

        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics()
        );
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        mEmboss = new EmbossMaskFilter(direction,light,specular,blur);
        mBlur = new BlurMaskFilter(20.0f, BlurMaskFilter.Blur.NORMAL);




    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int widthMeasured =measureWidth()+getPaddingLeft()+getPaddingRight();
            if (widthMode==MeasureSpec.AT_MOST) {
                width = Math.min(widthSize,widthMeasured);
            } else {
                width = widthMeasured;
            }
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height =0;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int heightMeasured = measureHeight() + getPaddingTop()+getPaddingBottom();
            if (heightMode==MeasureSpec.AT_MOST) {
                height = Math.min(heightSize,heightMeasured);
            } else {
                height = heightMeasured;
            }
        }


        width =Math.min(width,height);
        setMeasuredDimension(width,width);


    }

    private int measureHeight() {
        return mRadius*2;
    }

    private int measureWidth() {
        return mRadius*2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        mPaint.setShader(null);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(mBackgroudColor);
        mPaint.setMaskFilter(mEmboss);
        canvas.drawCircle(width/2.0f,height/2.0f,
                width/2.0f-getPaddingLeft()-mPaint.getStrokeWidth()/2.0f,mPaint);

        mPaint.setStrokeWidth(1.0f);
        mPaint.setColor(mBorderColor);
        canvas.drawCircle(width/2.0f,height/2.0f,
                width/2.0f-getPaddingLeft()-mPaint.getStrokeWidth()-0.5f,mPaint);
        canvas.drawCircle(width/2.0f,height/2.0f,
                width/2.0f-getPaddingLeft()-0.5f,mPaint);

        mSweepGradient = new SweepGradient(width/2.0f,height/2.0f,mArcColor,null);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setShader(mSweepGradient);
//        mPaint.setMaskFilter(mBlur);
        canvas.save();
        canvas.translate(getPaddingLeft(),getPaddingTop());
        float angle = mProgress*1.0f/100*360;
        canvas.drawArc(mRectF,-90,angle,false,mPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(mLineWidth/2.0f,mLineWidth/2.0f,
                w-getPaddingLeft()-getPaddingRight()-mLineWidth/2.0f,
                h-getPaddingTop()-getPaddingBottom()-mLineWidth/2.0f);
    }

    public void setProgress(int progress){
        mProgress = progress;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PROGRESS,mProgress);
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Parcelable parcelable = bundle.getParcelable(INSTANCE);
            super.onRestoreInstanceState(parcelable);
            mProgress = bundle.getInt(KEY_PROGRESS);
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
