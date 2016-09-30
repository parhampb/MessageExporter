package com.hazeltechnology.messageexporter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Parham on 8/09/2016.
 */

public class ExportTypeObject extends BaseObservable {

    public static final int EXPORT_TYPE_TEXT = 0;
    public static final int EXPORT_TYPE_JSON = 1;

    private String exportFormatDisplay;
    private String exportFormat;
    private boolean arraySelected;
    private boolean selected;
    private int type = EXPORT_TYPE_TEXT;
    private String extension = ".txt";
    private String filename = "";

    public ExportTypeObject(String exportFormatDisplay, String exportFormat, boolean selected) {
        this.exportFormatDisplay = exportFormatDisplay;
        this.exportFormat = exportFormat;
        this.selected = selected;
        this.arraySelected = selected;
    }

    @Bindable
    public boolean isArraySelected() {
        return arraySelected;
    }

    public void setArraySelected(boolean selected) {
        this.arraySelected = selected;
        notifyPropertyChanged(BR.arraySelected);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getExportFormatDisplay() {
        return exportFormatDisplay;
    }

    public String getExportFormat() {
        return exportFormat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if (type == EXPORT_TYPE_TEXT) {
            this.extension = ".txt";
        } else {
            this.extension = ".json";
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }
}
