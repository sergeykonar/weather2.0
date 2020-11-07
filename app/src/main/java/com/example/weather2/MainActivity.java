package com.example.weather2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Calendar;


import java.lang.Math;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    private Button search;
    private String result;
    private String city="Prague";
    private TextView temp;
    private TextInputEditText cityPrimary;
    private TextView cityLabel;
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String WEATHER_API_KEY = "&units=metric&appid=e89813f0aac2ffe098b97f711aae632a";
    private SharedPreferences sharedPrefs;
    public static final String myPrefs = "myprefs";
    public static final String nameKey = "nameKey";
    private ImageView imgWeather;
    private TextView description;
    private LinearLayout view;
    private int weatherID;
    NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (LinearLayout) findViewById(R.id.layout2);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);
        search = (Button) findViewById(R.id.button2);
        cityPrimary = (TextInputEditText) findViewById(R.id.city);
        cityLabel = (TextView) findViewById(R.id.textView3);
        temp = (TextView) findViewById(R.id.textView);
        description = (TextView) findViewById(R.id.description);

        city = cityPrimary.getText().toString();
        init();
        weather();
        Toolbar toolbar = initToolbar();

        nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                item.setChecked(true);
                return true;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                city = cityPrimary.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(nameKey, city);
                editor.apply();

                weather();
                Calendar rightNow = Calendar.getInstance();
                int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                Log.d("TAG", String.valueOf(currentHourIn24Format));
            }
        });




    }

    private void init() {
        sharedPrefs = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        if (sharedPrefs.contains(nameKey)) {
            city = sharedPrefs.getString(nameKey, "");
            cityLabel.setText(city);
        }
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings){

        }else if(id == R.id.aboutDev){

        }
        return super.onOptionsItemSelected(item);
    }

    private void weather() {
        try {
            final URL uri = new URL(WEATHER_URL+city+WEATHER_API_KEY);
            final Handler handler = new Handler();
            new Thread(new Runnable() {

                public void run() {
                    HttpsURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpsURLConnection) uri.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setReadTimeout(10000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        result = getLines(in);

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
                    } catch (Exception e) {
                        Log.e("TAG", "Fail connection", e);

                        Snackbar snackbar = Snackbar.make(view, "Wrong city name!", Snackbar.LENGTH_LONG);
                        snackbar.setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        snackbar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        snackbar.setAction("UNDO", new MyUndoListener());
                        snackbar.show();

                        e.printStackTrace();
                    } finally {
                        if (null != urlConnection) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Log.e("TAG", "Fail URI", e);

            e.printStackTrace();
        }
    }



    @TargetApi(Build.VERSION_CODES.N)
    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }
    


    @SuppressLint("DefaultLocale")
    private void displayWeather(WeatherRequest weatherRequest) throws JSONException {
        float tTemp = weatherRequest.getMain().getTemp();



        cityLabel.setText(city);

        JSONObject jsonObject = new JSONObject(result);
        String weatherInfo = jsonObject.getString("weather");
        JSONArray arr = new JSONArray(weatherInfo);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonPart = arr.getJSONObject(i);
            String main = "";
            String descriptionT = "";
            main = jsonPart.getString("main");
            weatherID = jsonPart.getInt("id");
            descriptionT = jsonPart.getString("description");
            setImg(weatherID);
            description.setText(String.valueOf(descriptionT).toUpperCase());
        }
        temp.setText(String.format("%d°C", Math.round(tTemp)));

    }

    public void setImg(int descriptionT){
        if(descriptionT >= 803 && descriptionT <= 804){
            imgWeather.setImageResource(R.drawable.broken_clouds);
        }
        else if(descriptionT == 800){
            imgWeather.setImageResource(R.drawable.sun);
        }else if(descriptionT == 801){
            imgWeather.setImageResource(R.drawable.few_clouds);
        }
        else if(descriptionT ==802){
            imgWeather.setImageResource(R.drawable.scattered_clouds);
        }

//      Clouds
//        Rains
        else if(descriptionT >= 500 && descriptionT <=504){
            imgWeather.setImageResource(R.drawable.rain);
        }

        else if(descriptionT >= 520 && descriptionT <=531){
            imgWeather.setImageResource(R.drawable.shower_rain);
        }






    }

//    public void setImg(int descriptionT){
//        switch (descriptionT){
//            case "overcast clouds":
//                imgWeather.setImageResource(R.drawable.broken_clouds);
//                break;
//
//            case "light rain":
//                imgWeather.setImageResource(R.drawable.icons8_rain_cloud_50__2_);
//                break;
//
//            case "scattered clouds":
//                imgWeather.setImageResource(R.drawable.scattered_clouds);
//                break;
//
//            case "clear sky":
//                imgWeather.setImageResource(R.drawable.sun);
//                break;
//
//            case "light intensity shower rain":
//                imgWeather.setImageResource(R.drawable.icons8_light_rain_50);
//                break;
//            case "broken clouds":
//                imgWeather.setImageResource(R.drawable.broken_clouds);
//                break;
//            case "few clouds":
//                imgWeather.setImageResource(R.drawable.few_clouds);
//                break;
//        }
//
//    }


}
class MyUndoListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {


    }
}
