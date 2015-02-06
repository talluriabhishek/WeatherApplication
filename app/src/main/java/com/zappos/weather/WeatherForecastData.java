package com.zappos.weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Talluri on 1/31/15.
 * Description: Class for the weather forecast
 * which contains the list of WeatherData
 *
 */
public class WeatherForecastData {

    private List<WeatherData> listForecast = new ArrayList<WeatherData>();

    public void addForecast(WeatherData forecast) {
        listForecast.add(forecast);
    }

    public WeatherData getForecast(int dayNum) {
        return listForecast.get(dayNum);
    }

    public List<WeatherData> getListForecast() {
        return listForecast;
    }
}
