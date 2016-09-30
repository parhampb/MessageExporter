package com.hazeltechnology.messageexporter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.tool.util.L;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.ExportFragmentBinding;

import java.util.ArrayList;

/**
 * Created by Parham on 7/09/2016.
 */

public class ExportFragment extends BackHandledFragment {

    private ExportFragmentBinding binding;

    public ObservableBoolean exportTypeFileEditEnabled = new ObservableBoolean(true);

    private static ArrayList<ExportTypeObject> exportTypeObjects = null;

    public void exportTypeSelected(int selectedId) {
        if (selectedId == R.id.exportTypeFile) {
            exportTypeFileEditEnabled.set(true);
        } else {
            exportTypeFileEditEnabled.set(false);
        }
    }

    public void onClickExportTypeTextEdit() {
        ExportTypeDialog dialog = new ExportTypeDialog();
        dialog.setupDialogParams(exportTypeObjects);
        dialog.show(getFragmentManager(), null);
    }

    public void onClickExport() {

        for (ExportTypeObject object : exportTypeObjects) {
            if (object.isSelected()) {
                object.setType(exportTypeFileEditEnabled.get() ? ExportTypeObject.EXPORT_TYPE_TEXT : ExportTypeObject.EXPORT_TYPE_JSON);
                ((MainActivity) getActivity()).setExportTypeObject(object);

                if (object.getType() == ExportTypeObject.EXPORT_TYPE_JSON) {
                    ((MainActivity) getActivity()).getSelectedContactObject().getOptions().dateReformatJsonStyle();
                }
                break;
            }
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.PERMISSION_CONSTANT);
        } else {
            new ExportNameDialog().show(getFragmentManager(), null);
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (exportTypeObjects == null) {
            exportTypeObjects = new ArrayList<>();
            exportTypeObjects.add(new ExportTypeObject("Name\t(Date)\r\nMessage Content", "``%$N\t``%$D\r\n``%$M", true));
            exportTypeObjects.add(new ExportTypeObject("Name\r\n(Date)\r\nMessage Content", "``%$N\r\n``%$D\r\n``%$M", false));
            exportTypeObjects.add(new ExportTypeObject("Name\r\nMessage Content\t(Date)", "``%$N\r\n``%$M\t``%$D", false));
            exportTypeObjects.add(new ExportTypeObject("Name\r\nMessage Content\r\n(Date)", "``%$N\r\n``%$M\r\n``%$D", false));
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.export_fragment, container, false);
        binding.setHandler(ExportFragment.this);
        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.PERMISSION_CONSTANT && grantResults.length > 0) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new ExportNameDialog().show(getFragmentManager(), null);
                }
            });
        }
    }
}
