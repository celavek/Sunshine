/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.mce.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.mce.sunshine.db.WeatherContract.LocationEntry;
import com.example.mce.sunshine.db.WeatherContract.WeatherEntry;
import com.example.mce.sunshine.db.WeatherDbHelper;

public class TestProvider extends AndroidTestCase {
 
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
 
    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        values.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        values.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        values.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        long locationRowId = -1;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New location row id: " + locationRowId);

        // Specify which columns you want.
        String[] columns = {};
        values.keySet().toArray(columns);
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LAT));
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LONG));
            double longitude = cursor.getDouble(longIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(values.get(LocationEntry.COLUMN_CITY_NAME), name);
            assertEquals(values.get(LocationEntry.COLUMN_LOCATION_SETTING), location);
            assertEquals(values.get(LocationEntry.COLUMN_COORD_LAT), latitude);
            assertEquals(values.get(LocationEntry.COLUMN_COORD_LONG), longitude);

        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
        cursor.close();

        // Fantastic. Now that we have a location, add some weather!
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75.0);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65.0);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        long weatherRowId = -1;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        // Verify we got a row back.
        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG, "New weather row id: " + weatherRowId);

        // A cursor is your primary interface to the query results.
        String[] weatherColumns = {};
        weatherValues.keySet().toArray(weatherColumns);
        cursor = db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                weatherColumns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int index = cursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
            String date = cursor.getString(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
            String descr = cursor.getString(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
            int wId = cursor.getInt(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
            double minTemp = cursor.getDouble(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
            double maxTemp = cursor.getDouble(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
            double pressure = cursor.getDouble(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
            double humidity = cursor.getDouble(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);
            double wind = cursor.getDouble(index);

            index = cursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES);
            double deg = cursor.getDouble(index);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_DATETEXT), date);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_SHORT_DESC), descr);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_MIN_TEMP), minTemp);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_MAX_TEMP), maxTemp);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_WEATHER_ID), wId);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_PRESSURE), pressure);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_HUMIDITY), humidity);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_WIND_SPEED), wind);
            assertEquals(weatherValues.get(WeatherEntry.COLUMN_DEGREES), deg);

            // Fantastic.  Now that we have a location, add some weather!
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
        cursor.close();

        // close the DB handle
        dbHelper.close();
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140923";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }
}