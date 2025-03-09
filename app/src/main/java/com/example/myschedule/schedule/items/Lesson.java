package com.example.myschedule.schedule.items;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import java.util.UUID;

@Entity(tableName = "lessons")
public class Lesson {

    public enum RepeatType {
        NOT,
        EVERY_WEEK,
        ONE_TIME_A_TWO_WEEKS
    }

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String assessmentType; // Лекция, практика, спорт и т.д.
    private String startTime; // "HH:mm"
    private String endTime;   // "HH:mm"
    private String teacher;
    private String place;
    private int dayOfWeek; // 1 (Понедельник), 2 (Вторник), ..., 7 (Воскресенье)

    @TypeConverters(DateConverter.class)
    private Date startDate; // Дата начала действия записи

    private RepeatType repeatType;

    private int semesterId; // Связь с таблицей семестров

    public Lesson() {
        this.id = UUID.randomUUID().toString();
    }

    public Lesson(String name, String assessmentType, int dayOfWeek, Date startDate, RepeatType repeatType, String startTime, String endTime, String teacher, String place, int semesterId) {
        this.name = name;
        this.assessmentType = assessmentType;
        this.dayOfWeek = dayOfWeek;
        this.startDate = startDate;
        this.repeatType = repeatType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacher = teacher;
        this.place = place;
        this.semesterId = semesterId;
        id = "";
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }
    public String getAssessmentType() {
        return assessmentType;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getEndTime() {
        return endTime;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    public String getTeacher() {
        return teacher;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    public String getPlace() {
        return place;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }
    public RepeatType getRepeatType() {
        return repeatType;
    }

    public int getSemesterId() {
        return semesterId;
    }
    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    // Класс-конвертер для Date
    public static class DateConverter {
        @TypeConverter
        public Long fromDate(Date date) {
            return date == null ? null : date.getTime();
        }

        @TypeConverter
        public Date toDate(Long timestamp) {
            return timestamp == null ? null : new Date(timestamp);
        }
    }

    // TypeConverter для RepeatType
    public static class RepeatTypeConverter {
        @TypeConverter
        public String fromRepeatType(RepeatType repeatType) {
            return repeatType == null ? null : repeatType.name();
        }

        @TypeConverter
        public RepeatType toRepeatType(String repeatType) {
            return repeatType == null ? null : RepeatType.valueOf(repeatType);
        }
    }

}
