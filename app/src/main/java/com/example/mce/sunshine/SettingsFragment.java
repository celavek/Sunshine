package com.example.mce.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static String KEY_PREF_CITY_ZIP = "";
    public static String KEY_PREF_UNIT = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KEY_PREF_CITY_ZIP = getString(R.string.pref_city_zip_key);
        KEY_PREF_UNIT = getString(R.string.pref_unit_key);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (null != savedInstanceState)
        {
            findPreference(KEY_PREF_CITY_ZIP).setSummary(savedInstanceState.getString(KEY_PREF_CITY_ZIP));
            findPreference(KEY_PREF_UNIT).setSummary(savedInstanceState.getString(KEY_PREF_UNIT));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_CITY_ZIP) || key.equals(KEY_PREF_UNIT)) {
            findPreference(key).setSummary(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putString(KEY_PREF_CITY_ZIP, getPreferenceManager().getSharedPreferences().getString(KEY_PREF_CITY_ZIP, ""));
        outState.putString(KEY_PREF_UNIT, getPreferenceManager().getSharedPreferences().getString(KEY_PREF_UNIT, ""));
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public void onRestoreInstanceState (Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_PREF_CITY))
        {
            String savedVal = savedInstanceState.getString(KEY_PREF_CITY);
            findPreference(KEY_PREF_CITY).setDefaultValue(savedVal);
            findPreference(KEY_PREF_CITY).setSummary(savedVal);
            savedVal = savedInstanceState.getString(KEY_PREF_UNIT);
            findPreference(KEY_PREF_UNIT).setDefaultValue(savedVal);
            findPreference(KEY_PREF_UNIT).setSummary(savedVal);
        }
    }*/
}