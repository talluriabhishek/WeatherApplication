package com.zappos.weather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Abhishek Talluri on 1/31/15.
 * Description: This file is the main application class
 * which sends initiates all the parsing and setting of
 * the views.
 */

public class WeatherAPP extends ActionBarActivity{

    private static final long MIN_TIME_BETWEEN_UPDATES = 1000*60;
    private static final float MIN_DISTANCE_CHANGE_BETWEEN_UPDATES = 100;
    private TextView temperature;
    private TextView description;
    private TextView day;
    private TextView city;
    private ListView list;
    private TextView loading;

    private Location location;
    private ProgressBar spinner;

    // Default location used for fetching the data of weather and forecast
    private static String place = "Jersey City,NJ";

    //default number of days for fetching the forecast
    private static final String FORECAST_DAYS = "7";

    //default language for fetching the data
    private static final String LANGUAGE = "english";

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            //Checks for the network information
            if(networkInfo == null || !networkInfo.isConnected()) {
                getConnectionStatus();
                return;
            }
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(null!=listAddresses && listAddresses.size()>0){
                    place = listAddresses.get(0).getAddressLine(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getConnectionStatus();
//            weatherTask();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            return;
        }

        @Override
        public void onProviderEnabled(String provider) {
            return;
        }

        @Override
        public void onProviderDisabled(String provider) {
            return;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_application);

        temperature = (TextView)findViewById(R.id.temperature);
        description = (TextView)findViewById(R.id.description);
        day = (TextView)findViewById(R.id.day);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        loading = (TextView)findViewById(R.id.loading);
        city = (TextView)findViewById(R.id.city);

        // This function call checks for the location
        getLocation();
    }

    private void getConnectionStatus(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Checks for the network information and raises an alert dialog to set the connection ON
        if(networkInfo == null || !networkInfo.isConnected()){
            AlertDialog.Builder alertInternet = new AlertDialog.Builder(this);
            alertInternet.setTitle("This app requires internet connection");
            alertInternet.setMessage("Go to Settings for switching on DATA");
            alertInternet.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            });
            alertInternet.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                    spinner.setVisibility(View.VISIBLE);
                    loading.setText("Please enable the DATA and REFRESH the application from the menu to fetch the data");
                    loading.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    city.setVisibility(View.GONE);
                    temperature.setVisibility(View.GONE);
                    day.setVisibility(View.GONE);

                }
            });
            AlertDialog dialog = alertInternet.create();
            dialog.show();
        }else{
            weatherTask();
        }


    }

    private void getLocation(){

        //This tries to fetch the location of the user
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_BETWEEN_UPDATES,
                        mLocationListener);
            }else{
                mLocationListener.onLocationChanged(location);
            }
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("This app requires GPS for fetching location");
            alert.setMessage("Go to Settings for switching on GPS");
            alert.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                    spinner.setVisibility(View.VISIBLE);
                    loading.setText("Please switch on the GPS and REFRESH the application from the menu to get the present location");
                    loading.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    city.setVisibility(View.GONE);
                    temperature.setVisibility(View.GONE);
                    day.setVisibility(View.GONE);
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }
    }

    private void weatherTask(){
        //These calls are made to call the
        // JSON parser functions and thereby
        // set the respective views

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // If the network is not connected, or if the location is not yet set,
        // then we again go and check through the function getLocation
        if(networkInfo == null || !networkInfo.isConnected() || location == null ){
            getLocation();
        }else{

            // If the DATA is enabled, then we try to fetch the weather content from the "place" that is set

            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(place);
            JSONForecastWeatherTask taskTwo = new JSONForecastWeatherTask();
            taskTwo.execute(place, LANGUAGE, FORECAST_DAYS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getConnectionStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        // We have the refresh option in this menu that refreshes the
        // weather content
        getMenuInflater().inflate(R.menu.menu_get_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here.
        int id = item.getItemId();

        //Handling the REFRESH option in the menu
        if(id == R.id.refresh){
            getLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // The request for weather data and thereby parsing it are
    // done using the AsyncTask
    private class JSONWeatherTask extends AsyncTask<String,Void, WeatherData> {

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected WeatherData doInBackground(String... params) {
            WeatherData weather = new WeatherData();
            String data = ( (new WeatherRequest()).getWeatherData(params[0]));

            try {
                weather = JSONParser.getWeather(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(WeatherData weather) {
            super.onPostExecute(weather);
            spinner.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            description.setText(weather.getDescription());
            temperature.setText("" + Math.round((weather.getTemperature() - 273.15))+"°C" );
            day.setText(weather.getDay());
            city.setText(weather.getCity());
            description.setVisibility(View.VISIBLE);
            city.setVisibility(View.VISIBLE);
            temperature.setVisibility(View.VISIBLE);
            day.setVisibility(View.VISIBLE);
        }
    }

    // The request for weather forecast data and thereby parsing it are
    // done using the AsyncTask
    private class JSONForecastWeatherTask extends AsyncTask<String, Void, WeatherForecastData> {

        @Override
        protected WeatherForecastData doInBackground(String... params) {

            String data = ( (new WeatherRequest()).getForecastWeatherData(params[0], params[1], params[2]));
            WeatherForecastData forecast = new WeatherForecastData();
            try {
                forecast = JSONParser.getForecastWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return forecast;
        }

        @Override
        protected void onPostExecute(WeatherForecastData forecastWeather) {
            super.onPostExecute(forecastWeather);
            list = (ListView)findViewById(R.id.listBelow);
            CustomList customAdapter = new CustomList(getApplicationContext(),R.layout.list_row,forecastWeather.getListForecast());
            list.setAdapter(customAdapter);
            list.setTextFilterEnabled(true);
            list.setVisibility(View.VISIBLE);
        }
    }
}
