package com.example.weather2;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class GetWeatherData {

    public void getData(String city, final Handler handler){
        final GetUrlData getUrlData = new GetUrlData();

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    final String result = getUrlData.getData();

                    Gson gson = new Gson();
                    final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                displayWeather(weatherRequest);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void displayWeather(WeatherRequest weatherRequest) throws JSONException {
        float tTemp = weatherRequest.getMain().getTemp();





        JSONObject jsonObject = new JSONObject(MainActivity.result);
        String weatherInfo = jsonObject.getString("weather");
        JSONArray arr = new JSONArray(weatherInfo);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonPart = arr.getJSONObject(i);
            String main = "";
            String descriptionT = "";
            main = jsonPart.getString("main");
            MainActivity.weatherID = jsonPart.getInt("id");
            descriptionT = jsonPart.getString("description");
            MainActivity.setImg(MainActivity.weatherID);
            MainActivity.description.setText(String.valueOf(descriptionT).toUpperCase());
        }
        MainActivity.temp.setText(String.format("%d°C", Math.round(tTemp)));


    }
}
