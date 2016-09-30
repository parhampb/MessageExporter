package com.hazeltechnology.messageexporter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.hazeltechnology.messageexporter.databinding.ConversationContactListItemBinding;


import java.util.ArrayList;

/**
 * Created by Parham on 1/09/2016.
 */

public class ConversationListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<ContactObject> contactObjects;
    private final ConversationSelectListener listener;

    public ConversationListRecyclerAdapter(@NonNull final Context context, @Nullable final ArrayList<ContactObject> contactObjects, @Nullable final ConversationSelectListener listener) {
        this.context = context;
        if (contactObjects != null) {
            this.contactObjects = contactObjects;
        } else {
            this.contactObjects = new ArrayList<>();
        }
        this.listener = listener;
    }

    public void insertItem(@NonNull ContactObject contactObject) {
        contactObjects.add(contactObject);
        notifyItemInserted(contactObjects.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactSelectViewHolder(LayoutInflater.from(context).inflate(R.layout.conversation_contact_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactSelectViewHolder contactSelectViewHolder = (ContactSelectViewHolder) holder;
        final ContactObject contact = contactObjects.get(position);
        contact.setVisible(true);

        contactSelectViewHolder.setContact(contact);

        new ContactObject.LoadImageToObjectAsync(context, contact, new ContactObject.LoadImageToObjectAsync.LoadImageListener() {
            @Override
            public void imageLoaded(Drawable profilePicture) {
                if (contact.isVisible()) {
                    contact.setProfilePicture(profilePicture);
                }
            }
        }).execute();

        contactSelectViewHolder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ContactObject tempContact : contactObjects) {
                    if (tempContact != contact && tempContact.isVisible()) {
                        tempContact.setProfilePicture(null);
                        tempContact.setVisible(false);
                    }
                }
                if (listener != null) {
                    listener.contactSelected(contact);
                }
            }
        });
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        final ContactObject contact = contactObjects.get(holder.getAdapterPosition());
        contact.setVisible(false);
        contact.setProfilePicture(null);
    }

    @Override
    public int getItemCount() {
        return contactObjects.size();
    }

    private class ContactSelectViewHolder extends RecyclerView.ViewHolder {

        private ConversationContactListItemBinding binding;

        public ContactSelectViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setContact(ContactObject contact) {
            binding.setContact(contact);
        }

        public View getRootView() {
            return binding.getRoot();
        }
    }

    interface ConversationSelectListener {
        void contactSelected(final ContactObject contact);
    }
}
