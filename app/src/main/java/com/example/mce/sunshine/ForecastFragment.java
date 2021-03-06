package com.example.mce.sunshine;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
     * A placeholder fragment containing a simple view.
     */
    public class ForecastFragment extends Fragment {

        private final String prefFileName = "com.example.mce.sunshine_preferences";
        private ArrayAdapter<String> mForecastAdapter;

        private Map<String, String> unitsMapping = new HashMap<String, String>();

        public static String KEY_PREF_CITY_ZIP = "";
        public static String KEY_PREF_UNIT = "";

        public ForecastFragment() {
            unitsMapping.put("Celsius", "metric");
            unitsMapping.put("Fahrenheit", "imperial");
        }

        @Override
        public void onCreate (Bundle savedInstances)
        {
            super.onCreate(savedInstances);
            setHasOptionsMenu(true);
            KEY_PREF_CITY_ZIP = getActivity().getString(R.string.pref_city_zip_key);
            KEY_PREF_UNIT = getActivity().getString(R.string.pref_unit_key);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);

            ListView forecastList = (ListView) rootView.findViewById(R.id.forecast_listview);
            forecastList.setAdapter(mForecastAdapter);

            forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context ctx = getActivity().getApplicationContext();
                    Intent detailIntent = new Intent(ctx, DetailActivity.class).putExtra(Intent.EXTRA_INTENT, (String) parent.getItemAtPosition(position));
                    startActivity(detailIntent);
                }
            });
            return rootView;
        }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeatherData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart () {
        super.onStart();
        updateWeatherData();
    }

    public void updateWeatherData () {
        //SharedPreferences prefs = getActivity().getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        new FetchWeatherTask(Integer.parseInt(prefs.getString(KEY_PREF_CITY_ZIP, "")), "BE", unitsMapping.get(prefs.getString(KEY_PREF_UNIT, "")), 7).execute();
    }

    class FetchWeatherTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private final String WEATHER_DATA_FORMAT = "json";

        private Integer mPostcode = 94043;
        private String mCountryCode = "US";
        private String mWeatherDataUnits = "metric";
        private Integer mWeatherDataSpan = 7;

        FetchWeatherTask (Integer postcode, String countryCode, String dataUnits, Integer dataSpan) {
            mPostcode = postcode;
            mCountryCode = countryCode;
            mWeatherDataUnits = dataUnits;
            mWeatherDataSpan = dataSpan;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {

                Uri builtUri = Uri.parse("http://api.openweathermap.org/data/2.5").buildUpon()
                        .appendPath("/forecast/daily")
                        .appendQueryParameter("q", String.valueOf(mPostcode) + "," + mCountryCode)
                        .appendQueryParameter("mode", WEATHER_DATA_FORMAT)
                        .appendQueryParameter("units", mWeatherDataUnits)
                        .appendQueryParameter("cnt", String.valueOf(mWeatherDataSpan)).build();

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Log.v(LOG_TAG, builtUri.toString());
                URL url = new URL(builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                //Log.d(LOG_TAG, forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {

                String[] weatherData =  new WeatherDataParser().getWeatherDataFromJson(forecastJsonStr, mWeatherDataSpan);
                /*for (String data : weatherData)
                {
                    Log.d(LOG_TAG, data);
                }*/

                return weatherData;
            } catch (JSONException je) {
                Log.e(LOG_TAG, je.getMessage(), je);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (null != strings) {
                super.onPostExecute(strings);
                mForecastAdapter.clear();
                mForecastAdapter.addAll(Arrays.asList(strings));
            }
        }
    }
}