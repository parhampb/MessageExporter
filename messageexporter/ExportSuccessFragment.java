package com.hazeltechnology.messageexporter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.ExportSuccessFragmentBinding;

import java.io.File;

/**
 * Created by Parham on 28/9/16.
 */

public class ExportSuccessFragment extends BackHandledFragment {

    public void onClickShare() {
        ExportTypeObject exportTypeObject = ((MainActivity) getActivity()).getExportTypeObject();
        File exportFile = new File(Environment.getExternalStorageDirectory(), AsyncExporter.FILE_DIR_NAME + File.separator + exportTypeObject.getFilename() + exportTypeObject.getExtension());
        if (exportFile.exists()) {
         //   File file = new File(Environment.getExternalStoragePublicDirectory("Conversation Exporter"), exportTypeObject.getFilename() + exportTypeObject.getExtension());
            Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.hazeltechnology.messageexporter.fileprovider", exportFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            getActivity().startActivity(Intent.createChooser(shareIntent, "Share file to"));
        }
    }

    @Override
    public boolean onBackPressed() {
        ((MainActivity) getActivity()).replaceFragment(new ConversationListFragment(), MainActivity.FRAGMENT_SLIDE_LEFT_ANIMATION_IN, MainActivity.FRAGMENT_SLIDE_RIGHT_ANIMATION_OUT);
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ExportSuccessFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.export_success_fragment, container, false);
        final ContactObject selectedContact = ((MainActivity) getActivity()).getSelectedContactObject();
        final ExportTypeObject exportTypeObject = ((MainActivity) getActivity()).getExportTypeObject();
        final String name = selectedContact.getName();
        final String editedName = selectedContact.getOptions().getContactName();
        final String nameTxt = name.equals(editedName) ? name : name + " (" + editedName + ")";
        binding.setTextFieldText("Export for " + nameTxt + " completed\n\nFile located at:\nDevice Storage/Conversation Exporter/\n" + exportTypeObject.getFilename() + exportTypeObject.getExtension());
        binding.setHandler(ExportSuccessFragment.this);
        return binding.getRoot();
    }
}
