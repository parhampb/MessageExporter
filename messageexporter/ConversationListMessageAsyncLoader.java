package com.hazeltechnology.messageexporter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Parham on 1/09/2016.
 */

public class ConversationListMessageAsyncLoader extends AsyncTask<Void, ContactObject, Integer> {

    private final Context context;
    private final ConversationListRecyclerAdapter adapter;

    private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_CONTACT_READ_ERROR = 1;

    private ConversationAsyncListener listener;

    public ConversationListMessageAsyncLoader(final Context context, final ConversationListRecyclerAdapter adapter, final ConversationAsyncListener listener) {
        this.context = context;
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        int status = STATUS_SUCCESS;

        final Cursor conversationCursor = context.getContentResolver().query(Telephony.Sms.Conversations.CONTENT_URI, null, null, null, Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);

        if (conversationCursor != null && conversationCursor.moveToFirst()) {
            do {
                ContactObject contactObject = null;
                try {
                    contactObject = ContactObject.readContact(context, conversationCursor);
                } catch (Exception e) {
                    status = STATUS_CONTACT_READ_ERROR;
                }

                if (contactObject != null) {
                    publishProgress(contactObject);
                }
            } while (conversationCursor.moveToNext());

            conversationCursor.close();
        }

        return status;
    }

    @Override
    protected void onProgressUpdate(ContactObject... values) {
        if (listener != null) {
            listener.status(true);
        }
        adapter.insertItem(values[0]);
    }

    @Override
    protected void onPostExecute(Integer status) {
        if (status == STATUS_CONTACT_READ_ERROR && context instanceof MainActivity) {
            ((MainActivity) context).showSnackbar("Error Reading Some Contacts...", Snackbar.LENGTH_LONG);
        }
    }

    interface ConversationAsyncListener {
        void status(boolean available);
    }
}
