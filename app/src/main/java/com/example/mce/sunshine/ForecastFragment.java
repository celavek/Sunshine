package com.example.mce.sunshine;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
     * A placeholder fragment containing a simple view.
     */
    public class ForecastFragment extends Fragment {

        private ArrayAdapter<String> mForecastAdapter;

        public ForecastFragment() {
        }

        @Override
        public void onCreate (Bundle savedInstances)
        {
            super.onCreate(savedInstances);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ArrayList<String> forecasts = new ArrayList<String>(12);
            forecasts.add("Today - Sunny - 88/63");
            forecasts.add("Tomorrow - Sunny - 89/64");
            forecasts.add("Thursday - Sunny - 90/65");
            forecasts.add("Friday - Sunny - 87/62");
            forecasts.add("Saturday - Sunny - 86/61");
            forecasts.add("Sunday - Sunny - 85/60");
            forecasts.add("Monday - Sunny - 88/63");
            forecasts.add("Tuesday - Sunny - 88/63");
            forecasts.add("Wednesday - Sunny - 88/63");
            forecasts.add("Thursday - Sunny - 88/63");
            forecasts.add("Friday - Sunny - 88/63");
            forecasts.add("Saturday - Sunny - 88/63");

            mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecasts);

            ListView forecastList = (ListView) rootView.findViewById(R.id.forecast_listview);
            forecastList.setAdapter(mForecastAdapter);

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
                new FetchWeatherTask(1050, "BE", "metric", 7).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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