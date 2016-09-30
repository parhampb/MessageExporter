package com.hazeltechnology.messageexporter;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.NoPermissionFragmentBinding;

/**
 * Created by Parham on 1/09/2016.
 */

public class NoPermissionFragment extends BackHandledFragment {

    private NoPermissionFragmentBinding binding;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private void setupViews() {

        final boolean read_sms = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
        final boolean read_contact = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

        String explanationText = "This application allows you to export your text messages on this device in many different formats.\n\n";
        final String[] projection;

        if (!read_sms && !read_contact) {
            explanationText += "The application requires the sms permission to be able to export the messages for you.\n\nThe contacts permission is so that a list of available contacts can be selected so that their messages can be exported.";
            projection = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS};
        } else if (!read_sms) {
            explanationText += "The application requires the sms permission to be able to export the messages for you.";
            projection = new String[]{Manifest.permission.READ_SMS};
        } else {
            explanationText += "The application requires the contacts permission is so that a list of available contacts can be selected so that their messages can be exported.";
            projection = new String[]{Manifest.permission.READ_CONTACTS};
        }

        binding.explanationTextview.setText(explanationText);

        binding.launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(projection, MainActivity.PERMISSION_CONSTANT);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MainActivity.PERMISSION_CONSTANT) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        setupViews();
                        return;
                    }
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).replaceFragment(new ConversationListFragment(), MainActivity.FRAGMENT_SLIDE_RIGHT_ANIMATION_IN, MainActivity.FRAGMENT_SLIDE_LEFT_ANIMATION_OUT);
                    }
                });
            } else {
                setupViews();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final View view = inflater.inflate(R.layout.no_permission_fragment, container, false);
        //binding = DataBindingUtil.getBinding(view);
        binding = DataBindingUtil.inflate(inflater, R.layout.no_permission_fragment, container, false);
        return binding.getRoot();
    }
}
