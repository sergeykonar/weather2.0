package com.example.weather2;


import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

public abstract class WeatherDataBase extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();
}
