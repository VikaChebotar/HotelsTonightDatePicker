package com.library;

import java.util.Date;

/**
 * Created by vika on 27.05.16.
 */
public interface DatePickerListener {
    void onDateRangeSelected(Date firstDate, Date secondDate);
}
