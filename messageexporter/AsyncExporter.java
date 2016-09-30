package com.hazeltechnology.messageexporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.annotations.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Parham on 15/09/2016.
 */

public class AsyncExporter extends AsyncTask<Void, Integer, Boolean> {

    public static final String FILE_DIR_NAME = "Conversation Exporter";

    public static final String EXPORT_FORMAT_NAME = "``%$N";
    public static final String EXPORT_FORMAT_MSG = "``%$M";
    public static final String EXPORT_FORMAT_DATE = "``%$D";

    private Context context;
    private ContactObject contactObject;
    private OptionsObject optionsObject;
    private ExportTypeObject exportTypeObject;
    private String filename;

    private File exportDirectory;

    private ProgressDialog progressDialog;

    public AsyncExporter(Context context, ContactObject contactObject, ExportTypeObject exportTypeObject) {
        this.context = context;
        this.contactObject = contactObject;
        this.optionsObject = contactObject.getOptions();
        this.exportTypeObject = exportTypeObject;
        this.filename = exportTypeObject.getFilename();
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Exporting", "Exporting conversation: " + contactObject.getName() + " which contains " + contactObject.getMessageCountText() + " messages", false, false);
        //exportFile = new File(Environment.getExternalStoragePublicDirectory("Conversation Exporter"), filename + exportTypeObject.getExtension());
        exportDirectory = new File(Environment.getExternalStorageDirectory(), FILE_DIR_NAME);
        if (!exportDirectory.exists()) {
            exportDirectory.mkdir();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isCancelled()) {
            return false;
        }

        File exportFile = new File(exportDirectory, filename + exportTypeObject.getExtension());
        if (!exportFile.exists()) {
            try {
                exportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        final Cursor messageCursor;
        final String[] projection = new String[] {
                //Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.DATE_SENT
        };
        if (optionsObject.isExportSortAscending()) {
            messageCursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, projection, Telephony.Sms.THREAD_ID + " = " + contactObject.getThreadId(), null, "date ASC");
        } else {
            messageCursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, projection, Telephony.Sms.THREAD_ID + " = " + contactObject.getThreadId(), null, "date DESC");
        }

        if (messageCursor != null && messageCursor.moveToFirst()) {
            try {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat(optionsObject.getDateFormat());
                final Date epochDateObj = new Date();
                FileOutputStream fileWriter = new FileOutputStream(exportFile);
                JSONArray jsonArray = new JSONArray();

                if (exportTypeObject.getType() == ExportTypeObject.EXPORT_TYPE_TEXT) {
                    exportTextHeader(fileWriter, optionsObject.getContactName(), optionsObject.getMyName(), optionsObject.isExportNumber() ? contactObject.getNumberFormatted() : null, optionsObject.isExportMsgCount() ? contactObject.getMessageCountText() : null);
                } else if (exportTypeObject.getType() == ExportTypeObject.EXPORT_TYPE_JSON) {
                    exportJsonHeader(optionsObject.getContactName(), optionsObject.getMyName(), optionsObject.isExportNumber() ? contactObject.getNumberFormatted() : null, optionsObject.isExportMsgCount() ? contactObject.getMessageCountText() : null);
                }
                do {
                    //final String number = messageCursor.getString(messageCursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    final long dateReceived = messageCursor.getLong(messageCursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    final long dateSent = messageCursor.getLong(messageCursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT));
                    final long dateEpoch = dateReceived;

                    //final String name = number.equals(contactObject.getNumber()) ? optionsObject.getContactName() : optionsObject.getMyName();
                    final String name = dateSent != 0 ? optionsObject.getContactName() : optionsObject.getMyName();
                    final String message = messageCursor.getString(messageCursor.getColumnIndexOrThrow(Telephony.Sms.BODY));

                    if (dateEpoch < optionsObject.getExportTimeFrom()) {
                        continue;
                    } else if (dateEpoch > optionsObject.getExportTimeTo()) {
                        break;
                    }

                    String date = null;
                    if (optionsObject.isExportDate()) {
                        epochDateObj.setTime(dateEpoch);
                        date = dateFormatter.format(epochDateObj);
                    }

                    if (exportTypeObject.getType() == ExportTypeObject.EXPORT_TYPE_TEXT) {
                        exportTextToFile(fileWriter, name, message, date, exportTypeObject.getExportFormat());
                    } else if (exportTypeObject.getType() == ExportTypeObject.EXPORT_TYPE_JSON) {
                        exportJson(jsonArray, name, message, date);
                    }
                } while (messageCursor.moveToNext());

                if (exportTypeObject.getType() == ExportTypeObject.EXPORT_TYPE_TEXT) {
                    exportTextFooter(fileWriter);
                } else if (exportTypeObject.getType() == ExportTypeObject.EXPORT_TYPE_JSON) {
                    try {
                        JSONObject expObj = new JSONObject();
                        expObj.put("head", exportJsonHeader(optionsObject.getContactName(), optionsObject.getMyName(), optionsObject.isExportNumber() ? contactObject.getNumberFormatted() : null, optionsObject.isExportMsgCount() ? contactObject.getMessageCountText() : null));
                        expObj.put("content", jsonArray);
                        //final String formattedJson = jsonArray.toString(4);
                        final String formattedJson = expObj.toString(4).replace("\\", "");
                        fileWriter.write(formattedJson.getBytes("UTF-16"));
                        final File compactFile = new File(exportDirectory, filename + ".compact" + exportTypeObject.getExtension());
                        final FileOutputStream compactWriter = new FileOutputStream(compactFile);
                        compactWriter.write(expObj.toString().getBytes("UTF-16"));
                        compactWriter.flush();
                        compactWriter.close();


                    } catch (JSONException e) {
                        Log.e(MainActivity.TAG, e.getMessage());
                    }
                }

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (messageCursor != null) {
            messageCursor.close();
        }
        return true;
    }

    private void exportJson(JSONArray jsonArray, String name, String message, @Nullable String date) {
        try {
            final JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("message", message);
            if (date != null) {
                json.put("date", date);
            }
            jsonArray.put(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject exportJsonHeader(String contactName, String myName, String contactNumber, String messageCount) {
        try {
            final JSONObject json = new JSONObject();
            json.put("contactName", contactName);
            json.put("myName", myName);
            if (contactNumber != null) {
                json.put("contactNumber", contactNumber);
            }
            if (messageCount != null) {
                json.put("msgCount", messageCount);
            }
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean exportTextToFile(FileOutputStream writer, String name, String message, @Nullable String date, String format) {
        final String item = format.replace(EXPORT_FORMAT_NAME, name).replace(EXPORT_FORMAT_MSG, message).replace(EXPORT_FORMAT_DATE, date != null ? date : "") + "\r\n\r\n";
        try {
            writer.write(item.getBytes("UTF-16"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /*
        This method allows for null values of arguments. If the argument is null, that part of the header will not be included
     */
    private void exportTextHeader(FileOutputStream writer, String contactName, String myName, String contactNumber, String messageCount) throws IOException {
        if (contactName != null) {
            writer.write(("Contact Name: " + contactName + "\r\nYour Name: " + myName).getBytes("UTF-16"));
        }

        if (contactNumber != null) {
            writer.write(("\r\nContact Number: " + contactNumber).getBytes("UTF-16"));
        }

        if (messageCount != null) {
            writer.write(("\r\nMessage Count: " + messageCount).getBytes("UTF-16"));
        }

        writer.write(("\r\n\r\n\r\n--- Conversation Begin ---\r\n\r\n").getBytes("UTF-16"));
    }

    private void exportTextFooter(FileOutputStream writer) throws IOException {
        writer.write("--- Conversation Ended ---".getBytes("UTF-16"));
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(progressDialog.getProgress() + 1);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        progressDialog.dismiss();
        ((MainActivity) context).showSnackbar("Directory could not be created", Snackbar.LENGTH_LONG);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        progressDialog.dismiss();
        if (success) {
            ((MainActivity) context).replaceFragment(new ExportSuccessFragment(), MainActivity.FRAGMENT_SLIDE_RIGHT_ANIMATION_IN, MainActivity.FRAGMENT_SLIDE_LEFT_ANIMATION_OUT);
        } else {
            ((MainActivity) context).showSnackbar("Error exporting...", Snackbar.LENGTH_LONG);
        }
    }
}
