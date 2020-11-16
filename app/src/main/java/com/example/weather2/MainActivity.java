package com.example.weather2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    public static String result;
    public static String city="Prague";
    public static TextView temp;
    private TextInputEditText cityPrimary;
    public static TextView cityLabel;
    public static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    public static final String WEATHER_API_KEY = "&units=metric&appid=e89813f0aac2ffe098b97f711aae632a";
    private SharedPreferences sharedPrefs;
    public static final String myPrefs = "myprefs";
    public static final String nameKey = "nameKey";
    public ImageView imgWeather;
    public static TextView description;
    private LinearLayout view;
    public static int weatherID;
    NavigationView nav;
    private Handler mHandler;

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

        initNotificationChannel();

//        weather();
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


                GetWeatherData weatherData = new GetWeatherData();
                weatherData.getData(city, new Handler());

                Calendar rightNow = Calendar.getInstance();
                int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                Log.d("TAG", String.valueOf(currentHourIn24Format));
            }
        });




    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void weather() {
        try {
            GetWeatherData weatherData = new GetWeatherData();
            weatherData.getData(city, new Handler());



        } catch (Exception e) {
            Log.e("TAG", "Fail URI", e);
            runOnUiThread(new Runnable() {
                public void run() {
                    errorDialog();
                }
            });

            e.printStackTrace();
        }
    }



    @TargetApi(Build.VERSION_CODES.N)
    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    private void errorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error")
                .setMessage("The error occurred while connecting to the server.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
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


    public static void setImg(int descriptionT){
//        if(descriptionT >= 803 && descriptionT <= 804){
//            imgWeather.setImageResource(R.drawable.broken_clouds);
//        }
//        else if(descriptionT == 800){
//            imgWeather.setImageResource(R.drawable.sun);
//        }else if(descriptionT == 801){
//            imgWeather.setImageResource(R.drawable.few_clouds);
//        }
//        else if(descriptionT ==802){
//            imgWeather.setImageResource(R.drawable.scattered_clouds);
//        }
//
////      Clouds
////        Rains
//        else if(descriptionT >= 500 && descriptionT <=504){
//            imgWeather.setImageResource(R.drawable.rain);
//        }
//
//        else if(descriptionT >= 520 && descriptionT <=531){
//            imgWeather.setImageResource(R.drawable.shower_rain);
//        }






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