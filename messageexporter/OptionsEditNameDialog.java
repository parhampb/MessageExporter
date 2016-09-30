package com.hazeltechnology.messageexporter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hazeltechnology.messageexporter.databinding.OptionsEditNameDialogBinding;

/**
 * Created by Parham on 2/09/2016.
 */

public class OptionsEditNameDialog extends AppCompatDialogFragment {

    private OptionsEditNameDialogBinding binding;

    private OptionsObject options;

    public void handleClickSet() {
        if (binding.contactNameEdittext.getText().length() > 0) {
            if (binding.myNameEdittext.getText().length() > 0) {
                options.setContactName(binding.contactNameEdittext.getText().toString());
                options.setMyName(binding.myNameEdittext.getText().toString());
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Please Set Your Name", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please Set Contact Name", Toast.LENGTH_LONG).show();
        }
    }

    public void initNames(OptionsObject options) {
        this.options = options;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().setTitle("Edit Names");
        setCancelable(false);

        binding = DataBindingUtil.inflate(inflater, R.layout.options_edit_name_dialog, container, false);
        binding.setContact(options);
        binding.setHandler(OptionsEditNameDialog.this);

        return binding.getRoot();
    }
}
