package com.hazeltechnology.messageexporter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Parham on 7/09/2016.
 */

public class OptionsSpanDateDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {

    private OptionsObject options;
    private boolean settingSpanFrom;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        c.clear();
        if (settingSpanFrom) {
            c.setTimeInMillis(options.getExportTimeFrom());
        } else {
            c.setTimeInMillis(options.getExportTimeTo());
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(options.getExportTimeFrom());
        datePickerDialog.getDatePicker().setMaxDate(options.getExportTimeTo());

        setCancelable(false);

        return datePickerDialog;
    }

    public void setupTypeAndObject(boolean settingSpanFrom, OptionsObject options) {
        this.options = options;
        this.settingSpanFrom = settingSpanFrom;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, dayOfMonth);

        if (settingSpanFrom) {
            options.setExportTimeFrom(c.getTimeInMillis());
        } else {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            options.setExportTimeTo(c.getTimeInMillis());
        }

        dismiss();
    }
}
