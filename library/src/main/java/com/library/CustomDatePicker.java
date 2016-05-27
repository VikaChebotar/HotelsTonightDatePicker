package com.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomDatePicker extends LinearLayout {
    private static final int DATES_NUMBER = 8;
    private static final int ANIMATION_DURATION_SHORT = 150;
    private static final int ANIMATION_DURATION_LONG = 500;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("E", Locale.getDefault());

    private TextView title;
    private FrameLayout datesContainer;

    private boolean isFirstState = true;
    private int firstDatePosition;
    private float firstDatePositionX;
    private Date firstDate;
    private Date secondDate;
    private int dateItemWidth;

    private DatePickerListener listener;

    public CustomDatePicker(Context context) {
        super(context);
        init();
    }

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TODO add attributes
        init();
    }

    public CustomDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.date_picker_background));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.date_picker, this);
        title = (TextView) view.findViewById(R.id.title);
        datesContainer = (FrameLayout) view.findViewById(R.id.datesContainter);
        initFirstState();
    }

    private void initFirstState() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < DATES_NUMBER; i++) {
            View dateItem = LayoutInflater.from(getContext()).inflate(R.layout.date_picker_item, datesContainer, false);
            TextView dayOfTheMonth = (TextView) dateItem.findViewById(R.id.dayOfTheMonth);
            TextView dayOfTheWeek = (TextView) dateItem.findViewById(R.id.dayOfTheWeek);
            if (i != 0) {
                calendar.add(Calendar.DATE, 1);
            }
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dayOfTheMonth.setText(String.format("%02d", dayOfMonth));
            dayOfTheWeek.setText(monthFormat.format(calendar.getTime()));
            dateItem.setOnClickListener(firstDateItemClickListener);
            datesContainer.addView(dateItem);
        }
    }

    private void returnToFirstState() {
        for (int i = 0; i < datesContainer.getChildCount(); i++) {
            View view = datesContainer.getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                view.setAlpha(0);
                view.setVisibility(View.VISIBLE);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1);
                alphaAnimation.setDuration(ANIMATION_DURATION_SHORT).start();
            }
        }
        datesContainer.setClickable(true);
        resetView();
    }

    private void initSecondState() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        for (int i = 0; i < DATES_NUMBER - 1; i++) {
            View dateItem;
            if (i == 0) {
                dateItem = LayoutInflater.from(getContext()).inflate(R.layout.date_picker_divider, datesContainer, false);
            } else {
                dateItem = LayoutInflater.from(getContext()).inflate(R.layout.date_picker_item, datesContainer, false);
                TextView dayOfTheMonthTextView = (TextView) dateItem.findViewById(R.id.dayOfTheMonth);
                TextView dayOfTheWeekTextView = (TextView) dateItem.findViewById(R.id.dayOfTheWeek);
                calendar.add(Calendar.DATE, 1);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                dayOfTheMonthTextView.setText(String.format("%02d", dayOfMonth));
                dayOfTheWeekTextView.setText(monthFormat.format(calendar.getTime()));
                dateItem.setOnClickListener(checkoutItemClickListener);
            }
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(dateItemWidth, i == 0 ? datesContainer.getMeasuredHeight() : LayoutParams.WRAP_CONTENT);
            dateItem.setLayoutParams(param);
            dateItem.setX((i + 1) * dateItemWidth);
            dateItem.setAlpha(0);
            datesContainer.addView(dateItem);
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(dateItem, View.ALPHA, 0, 1);
            alphaAnimation.setDuration(ANIMATION_DURATION_SHORT).start();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw != w) {
            dateItemWidth = (w - datesContainer.getPaddingLeft() - datesContainer.getPaddingRight()) / DATES_NUMBER;
            if (isFirstState) {
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    View child = datesContainer.getChildAt(i);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    lp.width = dateItemWidth;
                    child.setX(i * dateItemWidth);
                }
            } else {
                //todo refactor
                int checkInItemsIterator = 1;
                int checkOutItemsIterator = 0;
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    View child = datesContainer.getChildAt(i);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) child.getLayoutParams();
                    lp.width = dateItemWidth;
                    if (child.getVisibility() == View.VISIBLE) {
                        child.setX(checkOutItemsIterator * dateItemWidth);
                        checkOutItemsIterator++;
                    } else {
                        child.setX(checkInItemsIterator * dateItemWidth);
                        checkInItemsIterator++;
                    }
                }
            }
        }
    }


    private void animateFadeOutCheckInViews(final View view) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0);
        fadeOut.setDuration(ANIMATION_DURATION_SHORT);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isFirstState) {
                    datesContainer.removeView(view);
                } else {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeOut.start();
    }


    private void animateSelectedViewToCheckout(View view) {
        ObjectAnimator translateX = ObjectAnimator.ofFloat(view, "x", datesContainer.getPaddingLeft());
        translateX.setDuration(ANIMATION_DURATION_LONG);
        datesContainer.setClickable(false);
        translateX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                initSecondState();
                datesContainer.setClickable(true);
                isFirstState = false;
                title.setText(getContext().getString(R.string.secondDay));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translateX.start();
    }

    private void animateSelectedViewToCheckin(final View view) {
        ObjectAnimator translateX = ObjectAnimator.ofFloat(view, "x", firstDatePositionX);
        translateX.setDuration(ANIMATION_DURATION_LONG);
        datesContainer.setClickable(false);
        translateX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                returnToFirstState();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translateX.start();
    }

    private OnClickListener firstDateItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setSelected(true);
            if (isFirstState) {
                animateSelectedViewToCheckout(v);
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    if (datesContainer.getChildAt(i) != v) {
                        animateFadeOutCheckInViews(datesContainer.getChildAt(i));
                    } else {
                        firstDatePosition = i;
                        firstDatePositionX = v.getX();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, firstDatePosition);
                        firstDate = calendar.getTime();
                    }
                }
            } else {
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    if (datesContainer.getChildAt(i) != v && datesContainer.getChildAt(i).getVisibility() == View.VISIBLE) {
                        datesContainer.getChildAt(i).setSelected(false);
                        animateFadeOutCheckInViews(datesContainer.getChildAt(i));
                    }
                }
                v.setSelected(false);
                animateSelectedViewToCheckin(v);
            }
        }
    };

    private OnClickListener checkoutItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setSelected(true);
            for (int i = 0; i < datesContainer.getChildCount(); i++) {
                if (datesContainer.getChildAt(i) == v && i != datesContainer.getChildCount() - 1) {
                    int selectedCheckoutPosition = i - DATES_NUMBER;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(firstDate);
                    calendar.add(Calendar.DATE, selectedCheckoutPosition);
                    secondDate = calendar.getTime();
                    break;
                }
            }
            if (listener != null) {
                listener.onDateRangeSelected(firstDate, secondDate);
            }
        }
    };

    public void setListener(DatePickerListener listener) {
        this.listener = listener;
    }

    public void resetView() {
        isFirstState = true;
        firstDatePosition = 0;
        firstDatePositionX = 0;
        firstDate = null;
        secondDate = null;
        //   datesContainer.removeAllViews();
        title.setText(getContext().getString(R.string.firstDay));
        // initFirstState();
    }
}
