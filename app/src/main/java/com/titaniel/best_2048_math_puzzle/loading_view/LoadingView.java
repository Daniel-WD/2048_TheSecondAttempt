package com.titaniel.best_2048_math_puzzle.loading_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.titaniel.best_2048_math_puzzle.R;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

public class LoadingView extends View {

    private Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Drawable mContentDrawable;

    private int mWidth, mHeight;

    private float mArcOffsetAngle = 0, mArcSweepAngle = 300;

    private float mArcWidthRatio = 0.08f, mContentSizeRatio = 0.65f;

    private float mArcWidthPx;

    private int mColor;

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingView, 0, 0);

        try {
            mColor = a.getColor(R.styleable.LoadingView_color, Color.WHITE);
        } finally {
            a.recycle();
        }

        mContentDrawable = getResources().getDrawable(R.drawable.loading_globus);
        mContentDrawable.setColorFilter(mColor, PorterDuff.Mode.SRC_IN);

        mArcPaint.setColor(mColor);
        mArcPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float halfArcWidth = mArcWidthPx/2;

        canvas.drawArc(halfArcWidth, halfArcWidth, mWidth - halfArcWidth, mHeight - halfArcWidth,
                mArcOffsetAngle, mArcSweepAngle, false, mArcPaint);

        canvas.save();
        canvas.scale(mContentSizeRatio, mContentSizeRatio, mWidth/2, mHeight/2);

        mContentDrawable.setBounds(0, 0, mWidth, mHeight);

        mContentDrawable.draw(canvas);

        canvas.restore();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mArcWidthPx = mArcWidthRatio*(float) mWidth;

        mArcPaint.setStrokeWidth(mArcWidthPx);
    }

    @Keep
    public void setSweepAngle(float angle) {
        mArcSweepAngle = angle;
        invalidate();
    }

    @Keep
    public void setOffsetAngle(float angle) {
        mArcOffsetAngle = angle;
        invalidate();
    }

}
