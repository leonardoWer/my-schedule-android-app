package com.example.myschedule.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myschedule.schedule.items.Lesson;

import java.util.Date;
import java.util.List;

@Dao
public interface LessonDao {

    @Insert
    void insert(Lesson lesson);

    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId")
    List<Lesson> getLessonsBySemesterId(int semesterId);

    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND startDate <= :date")
    List<Lesson> getLessonsBySemesterIdAndDate(int semesterId, Date date);

    // Запрос с учетом типа повторения
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND dayOfWeek = :dayOfWeek AND startDate <= :date AND (repeatType = :repeatType OR repeatType IS NULL)")
    List<Lesson> getLessonsBySemesterIdAndDayOfWeekAndDateAndRepeatType(String semesterId, int dayOfWeek, Date date, Lesson.RepeatType repeatType);

    //  Запрос для получения всех уроков в заданном диапазоне дат (для оптимизации)
    @Query("SELECT * FROM lessons WHERE semesterId = :semesterId AND startDate BETWEEN :startDate AND :endDate")
    List<Lesson> getLessonsBySemesterIdAndDateRange(int semesterId, Date startDate, Date endDate);
}
