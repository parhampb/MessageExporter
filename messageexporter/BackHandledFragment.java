package com.hazeltechnology.messageexporter;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Parham on 31/08/2016.
 */

public abstract class BackHandledFragment extends Fragment {

    protected BackHandlerInterface backHandlerInterface;

    public boolean onBackPressed() {
        return ((MainActivity) getActivity()).popFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity()  instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        backHandlerInterface.setSelectedFragment(this);
    }

    public interface BackHandlerInterface {
        void setSelectedFragment(BackHandledFragment backHandledFragment);
    }
}
