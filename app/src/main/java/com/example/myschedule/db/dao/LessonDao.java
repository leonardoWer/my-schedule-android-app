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

    @Delete
    void delete(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId")
    List<Lesson> getLessonsBySemesterId(int semesterId);

    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND date <= :date")
    List<Lesson> getLessonsBySemesterIdAndDate(int semesterId, long date);

    // Запрос с учетом типа повторения
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND dayOfWeek = :dayOfWeek AND date <= :date AND (repeatType = :repeatType OR repeatType IS NULL)")
    List<Lesson> getLessonsBySemesterIdAndDayOfWeekAndDateAndRepeatType(String semesterId, int dayOfWeek, long date, Lesson.RepeatType repeatType);

    //  Запрос для получения всех уроков в заданном диапазоне дат (для оптимизации)
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND date BETWEEN :startDate AND :endDate")
    List<Lesson> getLessonsBySemesterIdAndDateRange(int semesterId, long startDate, long endDate);
}
