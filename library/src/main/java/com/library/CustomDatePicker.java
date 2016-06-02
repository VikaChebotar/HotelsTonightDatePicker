package com.library;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//TODO add atributes
//TODO open in fragment
//TODO add scroll
//TODO add dif days count
//TODO add back btn
//TODO refactor
public class CustomDatePicker extends LinearLayout {
    private static final int DATES_NUMBER_FIRST_DATE =5;
    private static final int DATES_NUMBER_SECOND_DATE = 6;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("E", Locale.getDefault());

    private TextView title;
    private LinearLayout datesContainer;

    private boolean isFirstState = true;
    private int firstDatePosition;
    private float firstDatePositionX;
    private Date firstDate;
    private Date secondDate;
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
        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.date_picker_background));
        setSaveEnabled(true);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.date_picker, this);
        title = (TextView) view.findViewById(R.id.title);
        datesContainer = (LinearLayout) view.findViewById(R.id.datesContainter);
        setLayoutTransition(null);
        datesContainer.setLayoutTransition(null);
        //  playAppearingAnimation();
        datesContainer.setVisibility(INVISIBLE);
        initFirstState();
    }

    public void playAppearingAnimation() {
        datesContainer.post(new Runnable() {
            @Override
            public void run() {
                float top = datesContainer.getTop();
                datesContainer.setY(top - datesContainer.getHeight());
                datesContainer.setAlpha(0);
                datesContainer.setVisibility(VISIBLE);
                AnimationUtil.animateTranslateAndAlphaAnimation(datesContainer, top);
            }
        });
    }

    public void playDisappearingAnimation() {
        AnimationUtil.animateFadeOut(datesContainer);
        AnimationUtil.animateFadeOut(title);
    }

    private void initFirstState() {
        datesContainer.setWeightSum(DATES_NUMBER_FIRST_DATE);
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < DATES_NUMBER_FIRST_DATE; i++) {
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
        AnimationUtil.animateWeightSum(datesContainer, DATES_NUMBER_SECOND_DATE + 2);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        for (int i = 0; i < DATES_NUMBER_SECOND_DATE + 1; i++) {
            View dateItem;
            if (i == 0) {
                dateItem = LayoutInflater.from(getContext()).inflate(R.layout.date_picker_divider, datesContainer, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
                param.gravity = Gravity.CENTER_VERTICAL;
                param.weight = 1;
                dateItem.setLayoutParams(param);
            } else {
                calendar.add(Calendar.DATE, 1);
                dateItem = initDateItem(calendar);
                dateItem.setOnClickListener(secondDateItemClickListener);
            }
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
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        param.gravity = Gravity.CENTER_VERTICAL;
        param.weight = 1;
        dateItem.setLayoutParams(param);
        return dateItem;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (oldw != w) {
            LinearLayout.LayoutParams datesContainerLp = (LayoutParams) datesContainer.getLayoutParams();
            int dateItemWidth = (w - datesContainerLp.leftMargin - datesContainerLp.rightMargin) / DATES_NUMBER_FIRST_DATE;
            if (!isFirstState) {
                firstDatePositionX = firstDatePosition * dateItemWidth;
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void animateSelectedViewToSecondState(View view) {
        datesContainer.setClickable(false);
        AnimationUtil.animateTranslateAnimation(view, 0, new Runnable() {
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
        AnimationUtil.animateWeightSum(datesContainer, DATES_NUMBER_FIRST_DATE);
        AnimationUtil.animateTranslateAnimation(view, firstDatePositionX, new Runnable() {
            @Override
            public void run() {
                view.setLeft((int) firstDatePositionX);
                view.setTranslationX(0);
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
                v.setSelected(false);
                animateSelectedViewToFirstState(v);
            }

            for (int i = 0; i < datesContainer.getChildCount(); i++) {
                View dateItemView = datesContainer.getChildAt(i);
                if (i != firstDatePosition && dateItemView.getVisibility() == View.VISIBLE) {
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
            for (int i = DATES_NUMBER_FIRST_DATE; i < datesContainer.getChildCount(); i++) {
                if (datesContainer.getChildAt(i).equals(v)) {
                    int selectedSecondDatePosition = i - DATES_NUMBER_FIRST_DATE;
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isFirstState = isFirstState;
        ss.firstDate = firstDate;
        ss.firstDatePosition = firstDatePosition;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        isFirstState = ss.isFirstState;
        firstDate = ss.firstDate;
        firstDatePosition = ss.firstDatePosition;
        if (!isFirstState) {
            View view = datesContainer.getChildAt(firstDatePosition);
            view.setSelected(true);
            view.setX(0);
            title.setText(getContext().getString(R.string.secondDay));
            for (int i = 0; i < datesContainer.getChildCount(); i++) {
                View dateItemView = datesContainer.getChildAt(i);
                if (!dateItemView.equals(view) && dateItemView.getVisibility() == View.VISIBLE) {
                    dateItemView.setSelected(false);
                    dateItemView.setAlpha(0);
                    dateItemView.setVisibility(View.GONE);
                }
            }
            initSecondState();
        }
        playAppearingAnimation();
    }

    public void setListener(DatePickerListener listener) {
        this.listener = listener;
    }


    static class SavedState extends BaseSavedState {
        boolean isFirstState;
        int firstDatePosition;
        Date firstDate;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isFirstState = in.readInt() == 1;
            if (!isFirstState) {
                firstDatePosition = in.readInt();
                firstDate = new Date(in.readLong());
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isFirstState ? 1 : 0);
            if (!isFirstState) {
                out.writeInt(firstDatePosition);
                out.writeLong(firstDate.getTime());
            }
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
