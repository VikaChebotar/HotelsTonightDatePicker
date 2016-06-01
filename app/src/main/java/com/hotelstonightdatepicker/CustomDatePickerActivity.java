package com.hotelstonightdatepicker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.transition.Transition;

import com.library.CustomDatePicker;
import com.library.DatePickerListener;

import java.util.Date;

/**
 * Created by vika on 01.06.16.
 */
public class CustomDatePickerActivity extends Activity {
    CustomDatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker_fragment);
        datePicker = (CustomDatePicker) findViewById(R.id.datePicker);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    datePicker.playAppearingAnimation();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        } else {
            datePicker.playAppearingAnimation();
        }

        datePicker.setListener(new DatePickerListener() {
            @Override
            public void onDateRangeSelected(Date firstDate, Date secondDate) {
                datePicker.playDisappearingAnimation();
                ActivityCompat.finishAfterTransition(CustomDatePickerActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        datePicker.playDisappearingAnimation();
    }
}

