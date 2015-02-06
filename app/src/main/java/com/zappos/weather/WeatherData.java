package com.zappos.weather;

/**
 * Created by Abhishek Talluri on 1/31/15.
 * Description: Class structure for the weather related data
 */
public class WeatherData {

    private String day;
    private float temperature;
    private String city;
    private String description;
    private String heading;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
