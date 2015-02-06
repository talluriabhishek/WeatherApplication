package com.zappos.weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Abhishek Talluri on 1/31/15.
 * Description: This file consists of the connection requests
 * to the web, both for the weather and the weather forecast.
 */
public class WeatherRequest {

    private static String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String WEATHER_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";

    public String getWeatherData(String location) {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {

            //Making the connection request for the weather data
            con = (HttpURLConnection) ( new URL(WEATHER_URL + location)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Reading the response from the connection request
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }

    public String getForecastWeatherData(String location, String lang, String numOfDays) {
        HttpURLConnection con = null ;
        InputStream is = null;
        int forecastDayNum = Integer.parseInt(numOfDays);

        try {

            //Making the connection request for the weather forecast data
            String url = WEATHER_FORECAST_URL + location;
            if (lang != null)
                url = url + "&lang=" + lang;

            url = url + "&cnt=" + forecastDayNum;
            con = (HttpURLConnection) ( new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Reading the response from the connection request
            StringBuffer buffer1 = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(is));
            String line1 = null;
            while (  (line1 = br1.readLine()) != null )
                buffer1.append(line1 + "\r\n");

            is.close();
            con.disconnect();
            return buffer1.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }

}
