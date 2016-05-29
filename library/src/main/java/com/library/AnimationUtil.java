package com.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

public class AnimationUtil {
    public static final int ANIMATION_DURATION_SHORT = 150;
    public static final int ANIMATION_DURATION_LONG = 500;

    public static void animateFadeIn(View view) {
        view.animate().alpha(1).setDuration(ANIMATION_DURATION_SHORT).start();
    }

    public static void animateFadeOut(View view, Runnable endAction) {
        view.animate().alpha(0).setDuration(ANIMATION_DURATION_SHORT).withEndAction(endAction).start();
    }

    public static void animateTranslateAnimation(View view, float x, Runnable endAction) {
        view.animate().x(x).setDuration(ANIMATION_DURATION_LONG).withEndAction(endAction).start();
    }
}
