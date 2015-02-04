package com.zappos.weather;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abhis_000 on 1/27/15.
 */
public class CustomList extends ArrayAdapter<WeatherData>{

    private final Context context;

    public CustomList(Context context,int resourceId,List<WeatherData> items){
        super(context,resourceId,items);
        this.context = context;
    }

    private class ViewHolder {
        TextView day;
        TextView description;
        TextView temperature;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        ViewHolder holder = null;
        WeatherData rowItem = getItem(position);
        LayoutInflater inflater =  (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_row, null);
                holder = new ViewHolder();
                holder.day = (TextView)convertView.findViewById(R.id.dayForecast);
                holder.description = (TextView) convertView.findViewById(R.id.descriptionForecast);
                holder.temperature = (TextView) convertView.findViewById(R.id.temperatureForecast);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.day.setText(rowItem.getDay());
            holder.temperature.setText(""+Math.round(rowItem.getTemperature())+"°C");
            holder.description.setText(rowItem.getDescription());

        return convertView;
    }
}
