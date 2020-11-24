package com.example.weather2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weather2.Weather;

import java.util.List;


@Dao
public interface WeatherDao {

    // Метод для добавления студента в базу данных
    // @Insert - признак добавления
    // onConflict - что делать, если такая запись уже есть
    // В данном случае просто заменим её
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWeather(Weather weather);

    // Метод для замены данных студента
    @Update
    void updateWeather(Weather weather);

    // Удаляем данные студента
    @Delete
    void deleteWeather(Weather weather);

    // Удаляем данные студента, зная ключ
    @Query("DELETE FROM weather WHERE id = :id")
    void deteleWeatherById(long id);

    // Забираем данные по всем студентам
    @Query("SELECT * FROM weather")
    List<Weather> getAllWeather();

    // Получаем данные одного студента по id
    @Query("SELECT * FROM student WHERE id = :id")
    Weather getWeatherById(long id);

    //Получаем количество записей в таблице
    @Query("SELECT COUNT(WeatherStudents();
}

