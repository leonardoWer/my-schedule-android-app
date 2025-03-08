package com.example.myschedule.editor.items;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myschedule.schedule.items.Lesson;

import java.util.Date;

@Entity(tableName = "semesters")
public class Semester {

    @PrimaryKey
    private int id; // От 1 до 8

    @TypeConverters(Lesson.DateConverter.class)
    private Date startDate;

    @TypeConverters(Lesson.DateConverter.class)
    private Date endDate;

    public Semester() {}

    public Semester(int semester, Date startDate, Date endDate) {
        this.id = semester;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Date getEndDate() {
        return endDate;
    }

}
