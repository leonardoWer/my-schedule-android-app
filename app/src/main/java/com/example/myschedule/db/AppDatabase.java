package com.example.myschedule.db;

import androidx.room.RoomDatabase;
import androidx.room.Database;
import androidx.room.TypeConverters;

import com.example.myschedule.db.dao.LessonDao;
import com.example.myschedule.db.dao.SemesterDao;
import com.example.myschedule.editor.items.Semester;
import com.example.myschedule.schedule.items.Lesson;


@Database(entities = {Lesson.class, Semester.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LessonDao lessonDao();
    public abstract SemesterDao semesterDao();
}
