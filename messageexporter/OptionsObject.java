package com.hazeltechnology.messageexporter;


import android.databinding.BaseObservable;

/**
 * Created by Parham on 6/09/2016.
 */

public class OptionsObject extends BaseObservable {

    private boolean exportName = true;
    private boolean exportNumber = true;
    private boolean exportDate = true;
    private boolean exportMsgCount = true;

    private String dateFormat = "HH:mm:ss\tdd/MM/yy";
    private boolean exportSortAscending = true;
    private long exportTimeFrom;
    private long exportTimeTo;

    private String myName;
    private String contactName;

    private boolean exportDateFormatCustom = false;

    public OptionsObject(String myName, String contactName, long firstMessageDate, long lastMessageDate) {
        this.myName = myName;
        this.contactName = contactName;
        this.exportTimeFrom = firstMessageDate;
        this.exportTimeTo = lastMessageDate;
    }

    public boolean isExportName() {
        return exportName;
    }

    public void setExportName(boolean exportName) {
        this.exportName = exportName;
    }

    public boolean isExportNumber() {
        return exportNumber;
    }

    public void setExportNumber(boolean exportNumber) {
        this.exportNumber = exportNumber;
    }

    public boolean isExportDate() {
        return exportDate;
    }

    public void setExportDate(boolean exportDate) {
        this.exportDate = exportDate;
    }

    public boolean isExportMsgCount() {
        return exportMsgCount;
    }

    public void setExportMsgCount(boolean exportMsgCount) {
        this.exportMsgCount = exportMsgCount;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat, boolean custom) {
        this.dateFormat = dateFormat;
        this.exportDateFormatCustom = custom;
    }

    public void dateReformatJsonStyle() {
        this.dateFormat = this.dateFormat.replace("\t", " ").replace("\\", "");
    }

    public boolean isExportSortAscending() {
        return exportSortAscending;
    }

    public void setExportSortAscending(boolean exportSortAscending) {
        this.exportSortAscending = exportSortAscending;
    }

    public long getExportTimeFrom() {
        return exportTimeFrom;
    }

    public void setExportTimeFrom(long exportTimeFrom) {
        this.exportTimeFrom = exportTimeFrom;
    }

    public long getExportTimeTo() {
        return exportTimeTo;
    }

    public void setExportTimeTo(long exportTimeTo) {
        this.exportTimeTo = exportTimeTo;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isExportDateFormatCustom() {
        return exportDateFormatCustom;
    }
}
