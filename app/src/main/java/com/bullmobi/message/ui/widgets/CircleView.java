/*
 * Copyright (C) 2014 AChep@xda <ynkr.wang@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package com.bullmobi.message.ui.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Property;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.bullmobi.message.Config;
import com.bullmobi.message.R;
import com.bullmobi.message.ui.preferences.ColorPickerPreference;
import com.bullmobi.base.async.WeakHandler;
import com.bullmobi.base.tests.Check;
import com.bullmobi.base.utils.FloatProperty;
import com.bullmobi.base.utils.ResUtils;

/**
 * Created by achep on 19.04.14.
 */
public class CircleView extends View {

    public static final int ACTION_START = 0;
    public static final int ACTION_UNLOCK = 1;
    public static final int ACTION_UNLOCK_START = 2;
    public static final int ACTION_UNLOCK_CANCEL = 3;
    public static final int ACTION_CANCELED = 4;

    private static final int MSG_CANCEL = -1;
    private static final Property<CircleView, Float> TRANSFORM =
            new FloatProperty<CircleView>("setRadius") {

                @Override
                public void setValue(CircleView cv, float value) {
                    cv.setRadius(value);
                }

                @Override
                public Float get(CircleView cv) {
                    return cv.mRadius;
                }

            };

    private float[] mPoint = new float[2];

    // Target
    private boolean mRadiusTargetAimed;
    private float mRadiusTarget;

    // Decreasing detection
    private float mRadiusDecreaseThreshold;
    private float mRadiusMaxPeak;

    /**
     * Real radius of the circle, measured by touch.
     */
    private float mRadius;

    /**
     * Radius of the drawn circle.
     *
     * @see #setRadiusDrawn(float)
     */
    private float mRadiusDrawn;


    private boolean mCanceled;
    private float mDarkening;

    private ColorFilter mInverseColorFilter;
    private Drawable mDrawable;
    private Paint mPaint;

    // animation
    private ObjectAnimator mAnimator;
    private int mShortAnimTime;
    private int mMediumAnimTime;

    private Callback mCallback;
    private Supervisor mSupervisor;

    private H mHandler = new H(this);

    private int mInnerColor;
    private int mOuterColor;

    public interface Callback {

        void onCircleEvent(float radius, float ratio, int event);
    }

    public interface Supervisor {

        boolean isAnimationEnabled();

        boolean isAnimationUnlockEnabled();

    }

    private static class H extends WeakHandler<CircleView> {

        public H(@NonNull CircleView cv) {
            super(cv);
        }

        @Override
        protected void onHandleMassage(@NonNull CircleView cv, Message msg) {
            switch (msg.what) {
                case MSG_CANCEL:
                    cv.cancelCircle();
                    break;
                case ACTION_START:
                case ACTION_UNLOCK:
                case ACTION_UNLOCK_START:
                case ACTION_UNLOCK_CANCEL:
                case ACTION_CANCELED:
                    if (cv.mCallback != null) {
                        cv.mCallback.onCircleEvent(cv.mRadius, cv.calculateRatio(), msg.what);
                    }
                    break;
            }
        }

    }

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Resources res = getResources();
        mRadiusTarget = res.getDimension(R.dimen.circle_radius_target);
        mRadiusDecreaseThreshold = res.getDimension(R.dimen.circle_radius_decrease_threshold);
        mShortAnimTime = res.getInteger(android.R.integer.config_shortAnimTime);
        mMediumAnimTime = res.getInteger(android.R.integer.config_mediumAnimTime);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        initColorFilter();

        // Load the drawable if needed
        mDrawable = ResUtils.getDrawable(getContext(), R.drawable.ic_settings_keyguard_white);
        mDrawable.setBounds(0, 0,
                mDrawable.getIntrinsicWidth(),
                mDrawable.getIntrinsicHeight());
        mDrawable = mDrawable.mutate(); // don't affect the original drawable

        setRadius(0);
    }

    private void initColorFilter() {
        final float v = -1;
        final float[] matrix = {
                v, 0, 0, 0, 0,
                0, v, 0, 0, 0,
                0, 0, v, 0, 0,
                0, 0, 0, 1, 0,
        };
        mInverseColorFilter = new ColorMatrixColorFilter(matrix);
    }

    public void setSupervisor(Supervisor supervisor) {
        mSupervisor = supervisor;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float ratio = calculateRatio();

        // Darkening background
        int alpha = (int) (mDarkening * 255);
        alpha += (int) ((255 - alpha) * ratio * 0.7f); // Change alpha dynamically
        canvas.drawColor(Color.argb(alpha,
                Color.red(mOuterColor),
                Color.green(mOuterColor),
                Color.blue(mOuterColor)));

        // Draw unlock circle
        mPaint.setColor(mInnerColor);
        mPaint.setAlpha((int) (255 * Math.pow(ratio, 1f / 3f)));
        canvas.drawCircle(mPoint[0], mPoint[1], mRadiusDrawn, mPaint);

        if (ratio >= 0.5f) {
            // Draw unlock icon at the center of circle
            float scale = 0.5f + 0.5f * ratio;
            canvas.save();
            canvas.translate(
                    mPoint[0] - mDrawable.getMinimumWidth() / 2 * scale,
                    mPoint[1] - mDrawable.getMinimumHeight() / 2 * scale);
            canvas.scale(scale, scale);
            mDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAnimator();
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private void setInnerColor(int color) {
        if (mInnerColor == (mInnerColor = color)) return;

        // Inverse the drawable if needed
        float[] innerHsv = new float[3];
        Color.colorToHSV(mInnerColor, innerHsv);
        float innerHsvValue = innerHsv[2];
        mDrawable.setColorFilter(innerHsvValue > 0.5f ? mInverseColorFilter : null);
    }

    private void setOuterColor(int color) {
        mOuterColor = color;
    }

    public boolean sendTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getActionMasked();

        // If current circle is canceled then
        // ignore all actions except of touch down (to reset state.)
        if (mCanceled && action != MotionEvent.ACTION_DOWN) return false;

        // Cancel the current circle on two-or-more-fingers touch.
        if (event.getPointerCount() > 1) {
            cancelCircle();
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                clearAnimation();

                // Update colors
                Config config = Config.getInstance();
                setInnerColor(ColorPickerPreference.getColor(config.getCircleInnerColor()));
                setOuterColor(ColorPickerPreference.getColor(config.getCircleOuterColor()));

                // Initialize circle
                mRadiusTargetAimed = false;
                mRadiusMaxPeak = 0;
                mPoint[0] = x;
                mPoint[1] = y;
                mCanceled = false;

                if (mHandler.hasMessages(ACTION_UNLOCK)) {
                    // Cancel unlocking process.
                    mHandler.sendEmptyMessage(ACTION_UNLOCK_CANCEL);
                }

                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(MSG_CANCEL, 1000);
                mHandler.sendEmptyMessage(ACTION_START);
                break;
            case MotionEvent.ACTION_MOVE:
                setRadius(x, y);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!mRadiusTargetAimed || action == MotionEvent.ACTION_CANCEL) {
                    cancelCircle();
                    break;
                }

                unlockCircle();
                break;
        }
        return true;
    }

    private void clearAnimator() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private void cancelCircle() {
        Check.getInstance().isFalse(mCanceled);

        mCanceled = true;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(ACTION_CANCELED);
        startAnimator(mRadius, 0f, mMediumAnimTime);
    }

    private void unlockCircle() {
        boolean animate = mSupervisor.isAnimationUnlockEnabled();

        if (animate) {
            // Calculate longest distance between center of
            // the circle and view's corners.
            float distance = 0f;
            int[] corners = new int[]{
                    0, 0, // top left
                    0, getHeight(), // bottom left
                    getWidth(), getHeight(), // bottom right
                    getWidth(), 0 // top right
            };
            for (int i = 0; i < corners.length; i += 2) {
                double c = Math.hypot(
                        mPoint[0] - corners[i],
                        mPoint[1] - corners[i + 1]);
                if (c > distance) distance = (float) c;
            }

            distance = (float) (Math.pow(distance / 50f, 2) * 50f);
            startAnimator(mRadius, distance, mShortAnimTime);
        }

        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(ACTION_UNLOCK_START);
        mHandler.sendEmptyMessageDelayed(ACTION_UNLOCK, animate
                ? mShortAnimTime - 10
                : 0);
    }

    private void startAnimator(float from, float to, int duration) {
        clearAnimator();
        if (mSupervisor.isAnimationEnabled()) {
            mAnimator = ObjectAnimator.ofFloat(this, TRANSFORM, from, to);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.setDuration(duration);
            mAnimator.start();
        } else {
            setRadius(to);
        }
    }

    //-- BASICS ---------------------------------------------------------------

    private float calculateRatio() {
        return Math.min(mRadius / mRadiusTarget, 1f);
    }

    private void setRadius(float x, float y) {
        double radius = Math.hypot(x - mPoint[0], y - mPoint[1]);
        setRadius((float) radius);
    }

    /**
     * Sets the radius of fake circle.
     *
     * @param radius radius to set
     */
    private void setRadius(float radius) {
        mRadius = radius;

        if (!mCanceled) {
            // Save maximum radius for detecting
            // decreasing of the circle's size.
            if (mRadius > mRadiusMaxPeak) {
                mRadiusMaxPeak = mRadius;
            } else if (mRadiusMaxPeak - mRadius > mRadiusDecreaseThreshold) {
                cancelCircle();
                return; // Cancelling circle will recall #setRadius
            }

            boolean aimed = mRadius >= mRadiusTarget;
            if (mRadiusTargetAimed != aimed) {
                mRadiusTargetAimed = aimed;
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); // vibrate
            }
        }

        // Update unlock icon's transparency.
        float ratio = calculateRatio();
        mDrawable.setAlpha((int) (255 * Math.pow(ratio, 3)));

        // Update the size of the unlock circle.
        radius = (float) Math.sqrt(mRadius / 50f) * 50f;
        setRadiusDrawn(radius);
    }

    private void setRadiusDrawn(float radius) {
        mRadiusDrawn = radius;
        postInvalidateOnAnimation();
    }

}
