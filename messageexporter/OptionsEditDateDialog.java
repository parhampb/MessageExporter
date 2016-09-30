package com.hazeltechnology.messageexporter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hazeltechnology.messageexporter.databinding.OptionsEditDateDialogBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Parham on 6/09/2016.
 */

public class OptionsEditDateDialog extends AppCompatDialogFragment {

    private OptionsEditDateDialogBinding binding;

    private OptionsObject options;

    public ObservableField<String> dateFormattedText = new ObservableField<>("");
    private SimpleDateFormat dateFormatter = new SimpleDateFormat();
    private boolean dateFormatOk = true;

    private boolean sortAscending = true;

    private Handler timeHandler = new Handler();

    public ObservableBoolean showCustomFormatLayout = new ObservableBoolean(false);

    public void setOptions(OptionsObject options) {
        this.options = options;
        this.showCustomFormatLayout.set(options.isExportDateFormatCustom());
    }

    public void dateFormatSelected(RadioGroup radioGroup, int selectedId) {
        if (selectedId != R.id.dateFormatCustom) {
            dateFormatter.applyPattern(((RadioButton) radioGroup.findViewById(selectedId)).getText().toString());
            dateFormatOk = true;
            showCustomFormatLayout.set(false);
            timeHandler.removeCallbacks(timeRunnable);
            timeHandler.post(timeRunnable);
        } else {
            showCustomFormatLayout.set(true);
            dateFormatOk = false;
            timeHandler.removeCallbacks(timeRunnable);
            binding.dateFormatCustomEditText.setText("");
        }
    }

    public void dateFormatTextWatcher(CharSequence text) {
        if (text.length() <= 0) {
            dateFormattedText.set("Input Empty");
            timeHandler.removeCallbacks(timeRunnable);
            dateFormatOk = false;
        } else {
            try {
                dateFormatter.applyPattern(text.toString());
                timeHandler.post(timeRunnable);
                dateFormatOk = true;
            } catch (IllegalArgumentException e) {
                dateFormattedText.set("Invalid Input");
                timeHandler.removeCallbacks(timeRunnable);
                dateFormatOk = false;
            }
        }
    }

    public void setSortAscending(int selectedId) {
        this.sortAscending = (selectedId == R.id.dateAscending);
    }

    public void onClickFormatHelp() {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://developer.android.com/reference/java/text/SimpleDateFormat.html")));
    }

    public void onClickSet() {
        if (dateFormatOk) {
            options.setDateFormat(dateFormatter.toPattern(), binding.dateFormatCustom.isChecked());
            options.setExportSortAscending(sortAscending);
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Date format invalid", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (dateFormatOk) {
            timeHandler.post(timeRunnable);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        setCancelable(false);
        getDialog().setTitle("Edit Date Format");

        binding = DataBindingUtil.inflate(inflater, R.layout.options_edit_date_dialog, container, false);
        binding.setOptions(options);
        binding.setHandler(OptionsEditDateDialog.this);

        dateFormatter.applyPattern(options.getDateFormat());

        return binding.getRoot();
    }

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            dateFormattedText.set(dateFormatter.format(new Date()));
            timeHandler.postDelayed(this, 500);
        }
    };
}
