package com.example.myschedule.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myschedule.schedule.items.Lesson;

import java.util.Date;
import java.util.List;

@Dao
public interface LessonDao {

    @Insert
    void insert(Lesson lesson);
    @Insert
    void insertAll(List<Lesson> lessons);


    @Delete
    void delete(Lesson lesson);
    @Query("DELETE FROM lessons WHERE semesterId = :semesterId")
    void deleteAllLessonsBySemesterId(int semesterId);


    @Update
    void update(Lesson lesson);


    // Все предметы в семестре
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId")
    List<Lesson> getLessonsBySemesterId(int semesterId);

    // На конкретную дату
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND date <= :date")
    List<Lesson> getLessonsBySemesterIdAndDate(int semesterId, long date);

    //  Запрос для получения всех уроков в заданном диапазоне дат
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND date BETWEEN :startDate AND :endDate")
    List<Lesson> getLessonsBySemesterIdAndDateRange(int semesterId, long startDate, long endDate);
}
