package com.hazeltechnology.messageexporter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by Parham on 1/09/2016.
 */

public class ContactObject extends BaseObservable {

    private static final int NULL_ID = -1;

    private final String name;
    private final String number;
    private final String messageSnippet;
    private final int messageCount;
    private final long threadId;
    private final int contactId;

    private boolean showProgress = false;
    private Drawable profilePicture = null;
    private boolean isVisible = false;

    private final OptionsObject options;

    public ContactObject(String name, String number, String messageSnippet, int messageCount, long threadId, int contactId, OptionsObject options) {
        this.name = name;
        this.number = number;
        this.messageSnippet = messageSnippet;
        this.messageCount = messageCount;
        this.threadId = threadId;
        this.contactId = contactId;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getNumberFormatted() {
        return PhoneNumberUtils.formatNumber(number, number, Locale.getDefault().getCountry());
    }

    public String getNumber() {
        return this.number;
    }

    public String getMessageSnippet() {
        return messageSnippet;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public String getMessageCountText() {
        return String.valueOf(messageCount);
    }

    public long getThreadId() {
        return threadId;
    }

    public int getContactId() {
        return contactId;
    }

    @Bindable
    public boolean isShowProgress() {
        return showProgress;
    }

    @Bindable
    public Drawable getProfilePicture() {
        return this.profilePicture;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        notifyPropertyChanged(BR.showProgress);
    }

    public void setProfilePicture(Drawable profilePicture) {
        this.profilePicture = profilePicture;
        notifyPropertyChanged(BR.profilePicture);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public OptionsObject getOptions() {
        return options;
    }

    /*
            ***DO NOT RUN*** THIS METHOD ON THE MAIN THREAD!!!
         */
    public static ContactObject readContact(final Context context, final Cursor conversationsCursor) throws Exception {

        final long thread_id = Long.parseLong(conversationsCursor.getString(conversationsCursor.getColumnIndexOrThrow(Telephony.Sms.Conversations.THREAD_ID)));
        final String snippet = conversationsCursor.getString(conversationsCursor.getColumnIndexOrThrow(Telephony.Sms.Conversations.SNIPPET));
        final int messageCount = Integer.parseInt(conversationsCursor.getString(conversationsCursor.getColumnIndexOrThrow(Telephony.Sms.Conversations.MESSAGE_COUNT)));

        String number = null;
        long exportFromDate = 0;
        long exportToDate = Long.MAX_VALUE;
        final Cursor miscCursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.DATE}, Telephony.Sms.THREAD_ID + " = " + String.valueOf(thread_id), null, Telephony.Sms.DEFAULT_SORT_ORDER);
        if (miscCursor != null && miscCursor.moveToFirst()) {
            number = miscCursor.getString(miscCursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
            exportToDate = Long.parseLong(miscCursor.getString(miscCursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));
            if (miscCursor.moveToLast()) {
                exportFromDate = Long.parseLong(miscCursor.getString(miscCursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));
            }
            miscCursor.close();
        }

        String name = number;
        int contactId = NULL_ID;
        if (number != null) {
            final Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            final Cursor nameAndIdCursor = context.getContentResolver().query(contactUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            if (nameAndIdCursor != null && nameAndIdCursor.moveToFirst()) {
                contactId = nameAndIdCursor.getInt(nameAndIdCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                name = nameAndIdCursor.getString(nameAndIdCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                nameAndIdCursor.close();
            }
        }

        final OptionsObject options = new OptionsObject(context.getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.PREFERENCE_MY_NAME, "Me"), name, exportFromDate, exportToDate);

        return new ContactObject(name, number, snippet, messageCount, thread_id, contactId, options);
    }
    
    public static class LoadImageToObjectAsync extends AsyncTask<Void, Void, Drawable> {

        private final Context context;
        private final ContactObject contactObject;
        private final LoadImageListener listener;

        public LoadImageToObjectAsync (final Context context, final ContactObject contactObject, final LoadImageListener listener) {
            this.context = context;
            this.contactObject = contactObject;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            contactObject.setShowProgress(true);
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            Drawable profilePicture = context.getResources().getDrawable(R.drawable.ic_contact_picture, null);

            if (contactObject.getContactId() != NULL_ID && contactObject.isVisible()) {
                InputStream contactStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactObject.contactId), true);
                if (contactStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(contactStream);
                    try {
                        contactStream.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    profilePicture = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                    ((RoundedBitmapDrawable) profilePicture).setCornerRadius(Math.min(bitmap.getHeight() >> 1, bitmap.getWidth() >> 1));
                }
            }

            return profilePicture;
        }

        @Override
        protected void onPostExecute(Drawable profilePicture) {
            if (listener != null) {
                listener.imageLoaded(profilePicture);
            }
            contactObject.setShowProgress(false);
        }

        interface LoadImageListener {
            void imageLoaded(Drawable profilePicture);
        }
    }
}
