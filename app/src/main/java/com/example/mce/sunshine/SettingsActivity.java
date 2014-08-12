package com.example.mce.sunshine;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends Activity {

    private final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
            ActionBar actionBar = getActionBar();
            if (null != actionBar) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                Log.d(LOG_TAG, "No action bar present for SettingsActivity!");
            }
        }
    }

    @Override
    public Intent getParentActivityIntent() {

        Intent parentIntent = getIntent();
        if (null != parentIntent && parentIntent.hasExtra(Intent.EXTRA_INTENT)) {
            String className = parentIntent.getStringExtra(Intent.EXTRA_INTENT);
            try {

                return new Intent(this, Class.forName(className));

            } catch (ClassNotFoundException cnf) {
                cnf.printStackTrace();
                Log.d(LOG_TAG, "No parent class found: " + className);
                return super.getParentActivityIntent();
            }
        }

        return super.getParentActivityIntent();
    }
}