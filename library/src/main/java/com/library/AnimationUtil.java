package com.library;

import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class AnimationUtil {
    public static final int ANIMATION_DURATION_SHORT = 150;
    public static final int ANIMATION_DURATION_LONG = 500;

    public static void animateFadeIn(View view) {
        view.animate().alpha(1).setDuration(ANIMATION_DURATION_SHORT).start();
    }

    public static void animateFadeOut(View view, Runnable endAction) {
        view.animate().alpha(0).setDuration(ANIMATION_DURATION_SHORT).withEndAction(endAction).start();
    }

    public static void animateTranslateAnimation(final View view, float x, final Runnable endAction) {
        view.animate().x(x).setDuration(ANIMATION_DURATION_LONG).withEndAction(endAction).start();
    }

    public static void animateTranslateAndAlphaAnimation(View view, float y){
        view.animate().y(y).alpha(1).setInterpolator(new AccelerateInterpolator()).setDuration(AnimationUtil.ANIMATION_DURATION_SHORT).start();
    }
}
