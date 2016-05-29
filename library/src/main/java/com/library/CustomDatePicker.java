package com.library;

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
            if (i != 0) {
                calendar.add(Calendar.DATE, 1);
            }
            View dateItem = initDateItem(calendar);
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
                AnimationUtil.animateFadeIn(view);
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
                calendar.add(Calendar.DATE, 1);
                dateItem = initDateItem(calendar);
                dateItem.setOnClickListener(secondDateItemClickListener);
            }
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(dateItemWidth, i == 0 ? datesContainer.getMeasuredHeight() : LayoutParams.WRAP_CONTENT);
            dateItem.setLayoutParams(param);
            dateItem.setX((i + 1) * dateItemWidth);
            dateItem.setAlpha(0);
            datesContainer.addView(dateItem);
            AnimationUtil.animateFadeIn(dateItem);
        }
    }

    private View initDateItem(Calendar calendar) {
        View dateItem = LayoutInflater.from(getContext()).inflate(R.layout.date_picker_item, datesContainer, false);
        TextView dayOfTheMonth = (TextView) dateItem.findViewById(R.id.dayOfTheMonth);
        TextView dayOfTheWeek = (TextView) dateItem.findViewById(R.id.dayOfTheWeek);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfTheMonth.setText(String.format("%02d", dayOfMonth));
        dayOfTheWeek.setText(monthFormat.format(calendar.getTime()));
        return dateItem;
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
                int firstStateViewsItemsIterator = 1;
                int secondStateViewsItemsIterator = 0;
                for (int i = 0; i < datesContainer.getChildCount(); i++) {
                    View child = datesContainer.getChildAt(i);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) child.getLayoutParams();
                    lp.width = dateItemWidth;
                    if (child.getVisibility() == View.VISIBLE) {
                        child.setX(secondStateViewsItemsIterator * dateItemWidth);
                        secondStateViewsItemsIterator++;
                    } else {
                        child.setX(firstStateViewsItemsIterator * dateItemWidth);
                        firstStateViewsItemsIterator++;
                    }
                }
            }
        }
    }

    private void animateSelectedViewToSecondState(View view) {
        datesContainer.setClickable(false);
        AnimationUtil.animateTranslateAnimation(view, datesContainer.getPaddingLeft(), new Runnable() {
            @Override
            public void run() {
                initSecondState();
                datesContainer.setClickable(true);
                isFirstState = false;
                title.setText(getContext().getString(R.string.secondDay));
            }
        });
    }

    private void animateSelectedViewToFirstState(final View view) {
        datesContainer.setClickable(false);
        AnimationUtil.animateTranslateAnimation(view, firstDatePositionX, new Runnable() {
            @Override
            public void run() {
                returnToFirstState();
            }
        });
    }

    private void animateFadeOut(final View v) {
        AnimationUtil.animateFadeOut(v, new Runnable() {
            @Override
            public void run() {
                if (isFirstState) {
                    v.setVisibility(View.GONE);
                } else {
                    datesContainer.removeView(v);
                }
            }
        });
    }

    private OnClickListener firstDateItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isFirstState) {
                firstDatePosition = datesContainer.indexOfChild(v);
                firstDatePositionX = v.getX();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, firstDatePosition);
                firstDate = calendar.getTime();
                v.setSelected(true);
                animateSelectedViewToSecondState(v);
            } else {
                animateSelectedViewToFirstState(v);
            }

            for (int i = 0; i < datesContainer.getChildCount(); i++) {
                View dateItemView = datesContainer.getChildAt(i);
                if (!dateItemView.equals(v) && dateItemView.getVisibility() == View.VISIBLE) {
                    dateItemView.setSelected(false);
                    animateFadeOut(dateItemView);
                }
            }
        }
    };

    private OnClickListener secondDateItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setSelected(true);
            for (int i = DATES_NUMBER; i < datesContainer.getChildCount(); i++) {
                if (datesContainer.getChildAt(i).equals(v)) {
                    int selectedSecondDatePosition = i - DATES_NUMBER;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(firstDate);
                    calendar.add(Calendar.DATE, selectedSecondDatePosition);
                    secondDate = calendar.getTime();
                    break;
                }
            }
            if (listener != null) {
                listener.onDateRangeSelected(firstDate, secondDate);
            }
        }
    };

    private void resetView() {
        isFirstState = true;
        firstDatePosition = 0;
        firstDatePositionX = 0;
        firstDate = null;
        secondDate = null;
        title.setText(getContext().getString(R.string.firstDay));
    }

    public void setListener(DatePickerListener listener) {
        this.listener = listener;
    }

}
