package com.zappos.weather;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.LangUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class WeatherAPP extends ActionBarActivity{

    private TextView temperature;
    private TextView description;
    private TextView day;
    private TextView city;
    private ListView list;
    private TextView loading;

    private Location location;

    private ProgressBar spinner;

    private static final String PLACE = "Jersey City,NJ";
    private static final String FORECAST_DAYS = "7";
    private static final String LANGUAGE = "english";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        temperature = (TextView)findViewById(R.id.temperature);
        description = (TextView)findViewById(R.id.description);
        day = (TextView)findViewById(R.id.day);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        loading = (TextView)findViewById(R.id.loading);
        city = (TextView)findViewById(R.id.city);
        if(getConnectionStatus()){
            System.out.println("There is internet connection");
        }
    }

    private boolean getConnectionStatus(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
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
                }
            });
            AlertDialog dialog = alertInternet.create();
            dialog.show();
            return false;
        }

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(PLACE);
        JSONForecastWeatherTask taskTwo = new JSONForecastWeatherTask();
        taskTwo.execute(PLACE,LANGUAGE,FORECAST_DAYS);
        return true;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.refresh){
            getConnectionStatus();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
        }
    }

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
        }
    }
}
