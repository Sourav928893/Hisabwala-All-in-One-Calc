package com.example.hisabwalaallinonecalc.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Touch animation utility that scales a view on press and restores it on release.
 * Provides haptic feedback for better user experience.
 *
 * @author 30415
 */
public class TouchAnimation implements View.OnTouchListener {
    private final View view;
    private final float scaleDownFactor;
    private final long animationDuration;

    private AnimatorSet animatorSet;

    public TouchAnimation(View view) {
        this(view, 0.85f, 120); // Default values
    }

    public TouchAnimation(View view, float scaleDownFactor, long animationDuration) {
        this.view = view;
        this.scaleDownFactor = scaleDownFactor;
        this.animationDuration = animationDuration;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    startScaleAnimation(scaleDownFactor);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
                    startScaleAnimation(1.0f);
                    break;

                default:
                    break;
            }
        } catch (Exception ignored) {
        }
        return false;
    }


    private void startScaleAnimation(float targetScale) {
        cancelAnimations();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, targetScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, targetScale);

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(animationDuration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    private void cancelAnimations() {
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
        }
    }
}
