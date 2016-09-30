package com.hazeltechnology.messageexporter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.ExportTypeDialogBinding;

import java.util.ArrayList;

/**
 * Created by Parham on 8/09/2016.
 */

public class ExportTypeDialog extends AppCompatDialogFragment {

    private ExportTypeDialogBinding binding;

    private ArrayList<ExportTypeObject> exportTypeObjects;
    private int positionSelected;

    public void setupDialogParams(ArrayList<ExportTypeObject> exportTypeObjects) {
        this.exportTypeObjects = exportTypeObjects;
        for (int i = 0, exportTypeObjectsSize = exportTypeObjects.size(); i < exportTypeObjectsSize; i++) {
            ExportTypeObject exportTypeObject = exportTypeObjects.get(i);
            if (exportTypeObject.isSelected()) {
                exportTypeObject.setArraySelected(true);
                positionSelected = i;
            } else {
                exportTypeObject.setArraySelected(false);
            }
        }
    }

    public void onClickSet() {
        for (int i = 0, exportTypeObjectsSize = exportTypeObjects.size(); i < exportTypeObjectsSize; i++) {
            ExportTypeObject exportTypeObject = exportTypeObjects.get(i);
            if (i == positionSelected) {
                exportTypeObject.setSelected(true);
            } else {
                exportTypeObject.setSelected(false);
            }
        }
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        setCancelable(false);
        getDialog().setTitle("Select Layout Format");

        final ExportTypeDialogRecycleViewAdapter adapter = new ExportTypeDialogRecycleViewAdapter(getActivity(), exportTypeObjects, new ExportTypeDialogRecycleViewAdapter.ExportTypeSelectionCallback() {
            @Override
            public void exportFormatSelected(ExportTypeObject exportTypeObject, int position) {
                positionSelected = position;
            }
        });

        binding = DataBindingUtil.inflate(inflater, R.layout.export_type_dialog, container, false);
        binding.exportTypeRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.exportTypeRecyclerview.setHasFixedSize(true);
        binding.exportTypeRecyclerview.setAdapter(adapter);

        binding.setHandler(ExportTypeDialog.this);

        return binding.getRoot();
    }
}
