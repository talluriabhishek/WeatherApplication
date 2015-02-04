package com.zappos.weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhis_000 on 1/27/15.
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
