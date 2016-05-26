package com.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private TextView checkInCheckOutLabel;
    private FrameLayout datesContainer;
    private boolean checkInState = true;
    private int selectedPosition;
    private float selectedPositionX;
    private Date selectedCheckInDate;
    private Date selectedCheckOutDate;
    private OnDateSelectedListener dateSelectedListener;
    private int dateItemWidth;

    public interface OnDateSelectedListener {
        void onDateSelected(Date checkInDate, Date checkOuDate);
    }

    public CustomDatePicker(Context context) {
        super(context);
        initCheckInView();
    }

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCheckInView();
    }

    public void setDateSelectedListener(OnDateSelectedListener dateSelectedListener) {
        this.dateSelectedListener = dateSelectedListener;
    }

    private void initCheckInView() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.date_picker_background));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_date_picker, this);
        checkInCheckOutLabel = (TextView) view.findViewById(R.id.checkInCheckOutLabel);
        datesContainer = (FrameLayout) view.findViewById(R.id.datesContainter);
        populateCheckInDateViews();
    }


    public void resetView() {
        checkInState = true;
        selectedPosition = 0;
        selectedPositionX = 0;
        selectedCheckInDate = null;
        selectedCheckOutDate = null;
        datesContainer.removeAllViews();
        checkInCheckOutLabel.setText(getContext().getString(R.string.checkIn));
        populateCheckInDateViews();
    }

    private void populateCheckInDateViews() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < DATES_NUMBER; i++) {
            View dateItem = LayoutInflater.from(getContext()).inflate(R.layout.custom_date_picker_item, null);
            TextView dayOfTheMonthTextView = (TextView) dateItem.findViewById(R.id.dayOfTheMonth);
            TextView dayOfTheWeekTextView = (TextView) dateItem.findViewById(R.id.dayOfTheWeek);
            if (i != 0) {
                calendar.add(Calendar.DATE, 1);
            }
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dayOfTheMonthTextView.setText(String.format("%02d", dayOfMonth));
            dayOfTheWeekTextView.setText(monthFormat.format(calendar.getTime()));
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(dateItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dateItem.setLayoutParams(param);
            dateItem.setX(i * dateItemWidth);
            dateItem.setOnClickListener(checkinItemClickListener);
            datesContainer.addView(dateItem);
        }
    }

    private void reInitCheckInView() {
        for (int i = 0; i < datesContainer.getChildCount(); i++) {
            View view = datesContainer.getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                view.setAlpha(0);
                view.setVisibility(View.VISIBLE);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1);
                alphaAnimation.setDuration(ANIMATION_DURATION_SHORT).start();
            }
        }
    }

    private void initCheckoutView() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedCheckInDate);
        for (int i = 0; i < DATES_NUMBER - 1; i++) {
            View dateItem;
            RelativeLayout.LayoutParams param;
            if (i == 0) {
                dateItem = LayoutInflater.from(getContext()).inflate(R.layout.custom_date_picker_divider, null);
                param = new RelativeLayout.LayoutParams(dateItemWidth, datesContainer.getMeasuredHeight());
            } else {
                param = new RelativeLayout.LayoutParams(dateItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                dateItem = LayoutInflater.from(getContext()).inflate(R.layout.custom_date_picker_item, null);
                TextView dayOfTheMonthTextView = (TextView) dateItem.findViewById(R.id.dayOfTheMonth);
                TextView dayOfTheWeekTextView = (TextView) dateItem.findViewById(R.id.dayOfTheWeek);
                calendar.add(Calendar.DATE, 1);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                dayOfTheMonthTextView.setText(String.format("%02d", dayOfMonth));
                dayOfTheWeekTextView.setText(monthFormat.format(calendar.getTime()));
                dateItem.setOnClickListener(checkoutItemClickListener);
            }

            param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            param.addRule(RelativeLayout.CENTER_HORIZONTAL);
            dateItem.setX((i + 1) * dateItemWidth);
            dateItem.setLayoutParams(param);
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
            if (checkInState) {
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    View child = datesContainer.getChildAt(i);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    lp.width = dateItemWidth;
                    child.setX(i * dateItemWidth);
                }
            } else {
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
                if (!checkInState) {
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
                initCheckoutView();
                datesContainer.setClickable(true);
                checkInState = false;
                checkInCheckOutLabel.setText(getContext().getString(R.string.checkOut));
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
        ObjectAnimator translateX = ObjectAnimator.ofFloat(view, "x", selectedPositionX);
        translateX.setDuration(ANIMATION_DURATION_LONG);
        datesContainer.setClickable(false);
        translateX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                reInitCheckInView();
                datesContainer.setClickable(true);
                checkInState = true;
                checkInCheckOutLabel.setText(getContext().getString(R.string.checkIn));
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

    private OnClickListener checkinItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setSelected(true);
            if (checkInState) {
                animateSelectedViewToCheckout(v);
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    if (datesContainer.getChildAt(i) != v) {
                        animateFadeOutCheckInViews(datesContainer.getChildAt(i));
                    } else {
                        selectedPosition = i;
                        selectedPositionX = v.getX();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, selectedPosition);
                        selectedCheckInDate = calendar.getTime();
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
                    calendar.setTime(selectedCheckInDate);
                    calendar.add(Calendar.DATE, selectedCheckoutPosition);
                    selectedCheckOutDate = calendar.getTime();
                    break;
                }
            }
            if (dateSelectedListener != null) {
                dateSelectedListener.onDateSelected(selectedCheckInDate, selectedCheckOutDate);
            }
        }
    };
}
