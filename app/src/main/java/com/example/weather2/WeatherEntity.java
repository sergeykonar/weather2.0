package com.example.weather2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// @Entity - это признак табличного объекта, то есть объект будет сохраняться
// в базе данных в виде строки
// indices указывает на индексы в таблице
@Entity(indices = {@Index(value = {"city", "temp"})})
public class WeatherEntity {

    // @PrimaryKey - указывает на ключевую запись,
    // autoGenerate = true - автоматическая генерация ключа
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Имя студента
    // @ColumnInfo позволяет задавать параметры колонки в БД
    // name = "first_name" - имя колонки
    @ColumnInfo(name = "city")
    public String firstName;

    // Фамилия студента
    @ColumnInfo(name = "temp")
    public String temp;
}
