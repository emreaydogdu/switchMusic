package com.emreay.music.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class VolumeProgress extends Drawable {

    private static final int NUM_SEGMENTS = 8;
    private final int mForeground;
    private final int mBackground;
    private final Paint mPaint = new Paint();
    private final RectF mSegment = new RectF();

    public VolumeProgress(int fgColor, int bgColor) {
        mForeground = fgColor;
        mBackground = bgColor;
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        float level = getLevel() / 1900f;
        Rect b = getBounds();

        mPaint.setAntiAlias(true);

        float gapWidth = b.height() / 4f;
        float segmentWidth = (b.width() - (NUM_SEGMENTS - 1) * gapWidth) / NUM_SEGMENTS;

        mSegment.set(0, 15, segmentWidth, b.height()/ 1.1f);
        mPaint.setColor(mForeground);

        for (int i = 0; i < NUM_SEGMENTS; i++) {
            float loLevel = i / (float) NUM_SEGMENTS;
            float hiLevel = (i + 1) / (float) NUM_SEGMENTS;

            if (loLevel <= level && level <= hiLevel) {
                float middle = mSegment.left + NUM_SEGMENTS * segmentWidth * (level - loLevel);
                canvas.drawRoundRect(mSegment, 20, 20, mPaint);
                //canvas.drawRect(mSegment.left, mSegment.top, middle, mSegment.bottom, mPaint);
                mPaint.setColor(mBackground);
                canvas.drawRoundRect(mSegment, 20, 20, mPaint);
                //canvas.drawRect(middle, mSegment.top, mSegment.right, mSegment.bottom, mPaint);
            } else {
                //canvas.drawRect(mSegment, mPaint);
                canvas.drawRoundRect(mSegment, 20, 20, mPaint);
            }
            mSegment.offset(mSegment.width() + gapWidth, 0);
        }
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter cf) {}

    @Override
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}