package com.example.myschedule.editor.items;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "semesters")
public class Semester {

    @PrimaryKey
    private int id; // От 1 до 8

    private long startDate;
    private long endDate;

    public Semester() {}

    public Semester(int semester, long startDate, long endDate) {
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

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    public long getStartDate() {
        return startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
    public long getEndDate() {
        return endDate;
    }

}
