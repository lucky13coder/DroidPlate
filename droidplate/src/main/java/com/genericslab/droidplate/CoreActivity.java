package com.genericslab.droidplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.genericslab.droidplate.app.CoreApplication;
import com.genericslab.droidplate.ui.dialog.LockProgressDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by shahab on 12/17/15.
 */
public abstract class CoreActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    protected LockProgressDialog dialog;

    protected String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTag(this.getClass().getSimpleName());
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        homeAsUpByBackStack();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void displayLockProgress() {
        FragmentManager fm = getSupportFragmentManager();
        if (dialog != null) dismissLockProgress();
        dialog = new LockProgressDialog();
        dialog.show(fm, "LockProgressDialog");
    }

    public void dismissLockProgress() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * Load fragment by replacing all previous fragments
     * @param fragment
     */
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // clear back stack
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.mainView, fragment, "main");
        fragmentManager.popBackStack();
        t.commit();
    }

    /**
     * Load Fragment on top of other fragments
     * @param fragment
     */
    public void loadChildFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainView, fragment, "main")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (((CoreApplication) getApplication()).getRequestQueue() != null) {
            ((CoreApplication) getApplication()).getRequestQueue() .cancelAll(tag);
        }
    }

    @Override
    public void onBackStackChanged() {
        homeAsUpByBackStack();
    }

    private void homeAsUpByBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar == null) return;
        if (backStackEntryCount > 0) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelPendingRequests(getTag());
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}