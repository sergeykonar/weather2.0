package com.example.weather2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class GetUrlData {
    public String result;
    public HttpsURLConnection urlConnection = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    String getData() throws MalformedURLException {
        final URL uri = new URL(MainActivity.WEATHER_URL+MainActivity.city+MainActivity.WEATHER_API_KEY);
        HttpsURLConnection urlConnection = null;

        try {
            urlConnection = (HttpsURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            result = getLines(in);
        }catch (Exception e){

        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }
}
