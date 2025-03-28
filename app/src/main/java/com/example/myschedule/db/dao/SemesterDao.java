package com.example.myschedule.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myschedule.editor.items.Semester;

import java.util.List;

@Dao
public interface SemesterDao {

    @Insert
    void insert(Semester semester);

    @Query("SELECT * FROM semesters")
    List<Semester> getAllSemesters();

    @Query("SELECT * FROM semesters WHERE id = :semesterId")
    Semester getSemesterById(int semesterId);
}
