package com.hazeltechnology.messageexporter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.ConversationListFragmentBinding;

/**
 * Created by Parham on 1/09/2016.
 */

public class ConversationListFragment extends BackHandledFragment {

    private ConversationListFragmentBinding binding;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ConversationListRecyclerAdapter recyclerAdapter = new ConversationListRecyclerAdapter(getActivity(), null, new ConversationListRecyclerAdapter.ConversationSelectListener() {
            @Override
            public void contactSelected(ContactObject contact) {
                ((MainActivity) getActivity()).setSelectedContactObject(contact);
                ((MainActivity) getActivity()).pushFragment(new OptionsFragment());
            }
        });
        binding.contactsRecyclerview.setHasFixedSize(true);
        binding.contactsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.contactsRecyclerview.setAdapter(recyclerAdapter);

        new ConversationListMessageAsyncLoader(getActivity(), recyclerAdapter, new ConversationListMessageAsyncLoader.ConversationAsyncListener() {
            @Override
            public void status(boolean available) {
                binding.setConversationsAvailable(available);
            }
        }).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final View view = inflater.inflate(R.layout.conversation_list_fragment, container, false);
        //binding = DataBindingUtil.getBinding(view);
        binding = DataBindingUtil.inflate(inflater, R.layout.conversation_list_fragment, container, false);
        binding.setConversationsAvailable(false);
        return binding.getRoot();
    }
}
