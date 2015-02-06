package com.zappos.weather;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abhishek Talluri on 1/31/15.
 * Description: This class parses the JSON data that is retrieved as part of the
 * request to the weather api in WeatherRequest.java
 */
public class JSONParser {

    public static WeatherData getWeather(String data) throws JSONException {
        WeatherData weather = new WeatherData();
        ArrayList<String> days = new ArrayList<String>(){{add("SUNDAY");add("MONDAY");add("TUESDAY");add("WEDNESDAY");add("THURSDAY");add("FRIDAY");add("SATURDAY");}};
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        // We start extracting the info
        Location location;

        JSONObject coordObj = getObject("coord", jObj);

        JSONObject sysObj = getObject("sys", jObj);

        weather.setCity(getString("name", jObj));

        // The weather info is stored as an array.
        JSONArray jArr = jObj.getJSONArray("weather");

        // We use only the first value
        JSONObject JSONWeather = jArr.getJSONObject(0);
        weather.setDescription(getString("description", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);

        weather.setTemperature(getFloat("temp", mainObj));

        weather.setDay(days.get(day-1));

        return weather;
    }

    public static WeatherForecastData getForecastWeather(String data) throws JSONException  {

        WeatherForecastData forecast = new WeatherForecastData();

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        JSONArray jArr = jObj.getJSONArray("list"); // Here we have the forecast for every day
        ArrayList<String> days = new ArrayList<String>(){{add("SUNDAY");add("MONDAY");add("TUESDAY");add("WEDNESDAY");add("THURSDAY");add("FRIDAY");add("SATURDAY");}};
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i=0; i < jArr.length(); i++) {

            WeatherData weather = new WeatherData();
            JSONObject jDayForecast = jArr.getJSONObject(i);

            // Now we have the json object so we can extract the data

            JSONObject jTempObj = jDayForecast.getJSONObject("temp");
            weather.setTemperature(Math.round((jTempObj.getDouble("day") - 273.15)));
            weather.setDay(days.get((day+i)%7));

            // we extract the description from the weather
            JSONArray jWeatherArr = jDayForecast.getJSONArray("weather");
            JSONObject jWeatherObj = jWeatherArr.getJSONObject(0);
            weather.setDescription(getString("description", jWeatherObj));

            forecast.addForecast(weather);
        }
        return forecast;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private  static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}
