package com.hazeltechnology.messageexporter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.ExportNameDialogBinding;

import java.util.Calendar;

/**
 * Created by Parham on 15/09/2016.
 */

public class ExportNameDialog extends AppCompatDialogFragment {

    private ExportNameDialogBinding binding;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(CharSequence filename) {
        this.filename = filename.toString();
    }

    public void exportClicked() {
        ((MainActivity) getActivity()).getExportTypeObject().setFilename(filename);
        ((MainActivity) getActivity()).initExport();
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().setTitle("Set Filename");
        setCancelable(false);

        Calendar calendar = Calendar.getInstance();
        if (!getActivity().getResources().getConfiguration().locale.getCountry().equals("US")) {
            filename = ((MainActivity) getActivity()).getSelectedContactObject().getName() + " - Conversation Export - " + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
        } else {
            filename = ((MainActivity) getActivity()).getSelectedContactObject().getName() + " - Conversation Export - " + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR);
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.export_name_dialog, container, false);
        binding.setHandler(ExportNameDialog.this);

        return binding.getRoot();
    }
}
