package com.hazeltechnology.messageexporter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.OptionsFragmentBinding;

/**
 * Created by Parham on 1/09/2016.
 */

public class OptionsFragment extends BackHandledFragment {

    public final ObservableBoolean showSpanGroup = new ObservableBoolean(false);
    private final ObservableBoolean nameChecked = new ObservableBoolean(true);
    public final ObservableBoolean dateChecked = new ObservableBoolean(true);
    private boolean numberChecked = true;
    private boolean msgCountChecked = true;

    private OptionsFragmentBinding binding;

    private ContactObject selectedContact;

    //public boolean isDateChecked() {
    //    return dateChecked.get();
    //}

    public boolean isNameChecked() {
        return nameChecked.get();
    }

    public void setNameChecked(boolean nameChecked) {
        this.nameChecked.set(nameChecked);
    }

    public void setNumberChecked(boolean numberChecked) {
        this.numberChecked = numberChecked;
    }

    public void setDateChecked(boolean dateChecked1) {
        dateChecked.set(dateChecked1);
    }

    public void setMsgCountChecked(boolean msgCountChecked) {
        this.msgCountChecked = msgCountChecked;
    }

    public void onClickNameEdit() {
        final OptionsEditNameDialog dialog = new OptionsEditNameDialog();
        dialog.initNames(selectedContact.getOptions());
        dialog.show(getFragmentManager(), null);
    }

    public void onClickDateEdit() {
        final OptionsEditDateDialog dialog = new OptionsEditDateDialog();
        dialog.setOptions(selectedContact.getOptions());
        dialog.show(getFragmentManager(), null);
    }

    public void onClickExpandSpanGroup() {
        showSpanGroup.set(true);
    }

    public void onClickSpanFrom() {
        OptionsSpanDateDialog dialog = new OptionsSpanDateDialog();
        dialog.setupTypeAndObject(true, selectedContact.getOptions());
        dialog.show(getFragmentManager(), null);
    }

    public void onClickSpanTo() {
        OptionsSpanDateDialog dialog = new OptionsSpanDateDialog();
        dialog.setupTypeAndObject(false, selectedContact.getOptions());
        dialog.show(getFragmentManager(), null);
    }

    public void onClickContinue() {
        selectedContact.getOptions().setExportName(nameChecked.get());
        selectedContact.getOptions().setExportDate(dateChecked.get());
        selectedContact.getOptions().setExportNumber(numberChecked);
        selectedContact.getOptions().setExportMsgCount(msgCountChecked);

        ((MainActivity) getActivity()).pushFragment(new ExportFragment());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectedContact = ((MainActivity) getActivity()).getSelectedContactObject();

        binding = DataBindingUtil.inflate(inflater, R.layout.options_fragment, container, false);
        binding.setContact(selectedContact);
        binding.setHandler(OptionsFragment.this);

        return binding.getRoot();
    }
}
