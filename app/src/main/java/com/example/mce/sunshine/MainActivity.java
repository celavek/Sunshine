package com.example.mce.sunshine;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    public static String KEY_PREF_CITY_ZIP = "";
    public static String KEY_PREF_UNIT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        KEY_PREF_CITY_ZIP = getString(R.string.pref_city_zip_key);
        KEY_PREF_UNIT = getString(R.string.pref_unit_key);

        Log.d(LOG_TAG, "ON_CREATE");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "ON_START");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "ON_RESTART");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "ON_PAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "ON_STOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "ON_DESTROY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class).putExtra(Intent.EXTRA_INTENT, MainActivity.class.getCanonicalName()));
            return true;
        } else if (R.id.action_view_on_map == id) {
            String location = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_PREF_CITY_ZIP, "");
            Uri geoLocation = Uri.parse("geo:0,0?q=" + Uri.encode(location));
            showOnMap(geoLocation);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showOnMap (Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException anf) {
                anf.printStackTrace();
                Log.d(LOG_TAG, "Could not start Map activity!");
            }
        }
    }

}
