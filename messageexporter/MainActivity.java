package com.hazeltechnology.messageexporter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hazeltechnology.messageexporter.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    public static final String TAG = "tag";

    public static final int PERMISSION_CONSTANT = 0;

    public static final String PREFERENCES = "sharedPrefs";
    public static final String PREFERENCE_MY_NAME = "prefsMyName";

    public static final int FRAGMENT_SLIDE_RIGHT_ANIMATION_IN = R.anim.fragment_slide_in_right;
    public static final int FRAGMENT_SLIDE_RIGHT_ANIMATION_OUT = R.anim.fragment_slide_out_right;
    public static final int FRAGMENT_SLIDE_LEFT_ANIMATION_IN = R.anim.fragment_slide_in_left;
    public static final int FRAGMENT_SLIDE_LEFT_ANIMATION_OUT = R.anim.fragment_slide_out_left;

    protected BackHandledFragment selectedFragment;

    private ActivityMainBinding binding;

    private ContactObject selectedContactObject = null;
    private ExportTypeObject exportTypeObject = new ExportTypeObject("Name\t(Date)\r\nMessage Content", "``%$N\t``%$D\r\n``%$M", true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.rootFrameLayout.getId(), new NoPermissionFragment())
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.rootFrameLayout.getId(), new ConversationListFragment())
                        .commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.rootFrameLayout.getId(), new ConversationListFragment())
                    .commit();
        }
    }

    public void replaceFragment(final Fragment destFragment, final int enterAnimation, final int exitAnimation) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation)
                .replace(binding.rootFrameLayout.getId(), destFragment)
                .commit();
    }

    public void pushFragment(final Fragment destFragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(FRAGMENT_SLIDE_RIGHT_ANIMATION_IN, FRAGMENT_SLIDE_LEFT_ANIMATION_OUT, FRAGMENT_SLIDE_LEFT_ANIMATION_IN, FRAGMENT_SLIDE_RIGHT_ANIMATION_OUT)
                .replace(binding.rootFrameLayout.getId(), destFragment)
                .addToBackStack(null)
                .commit();
    }

    public boolean popFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    public void showSnackbar(String message, int length) {
        final Snackbar snackbar = Snackbar.make(binding.rootCoordinatorLayout, message, length);
        snackbar.getView().setBackgroundResource(R.color.snackBarColour);
        snackbar.show();
    }

    public void showSnackbar(String message, int length, String action, View.OnClickListener actionListener) {
        final Snackbar snackbar = Snackbar.make(binding.rootCoordinatorLayout, message, length);
        snackbar.getView().setBackgroundResource(R.color.snackBarColour);
        snackbar.setAction(action, actionListener);
        snackbar.show();
    }

    public ContactObject getSelectedContactObject() {
        return selectedContactObject;
    }

    public void setSelectedContactObject(ContactObject selectedContactObject) {
        this.selectedContactObject = selectedContactObject;
    }

    public ExportTypeObject getExportTypeObject() {
        return exportTypeObject;
    }

    public void setExportTypeObject(ExportTypeObject exportTypeObject) {
        this.exportTypeObject = exportTypeObject;
    }

    public void initExport() {
        new AsyncExporter(this, selectedContactObject, exportTypeObject).execute();
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }
}
