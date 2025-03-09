package com.example.myschedule.schedule.items;

import java.util.List;

public class CalendarDay {

    private String dayOfWeek; // Название дня недели
    private String date;    // Число месяца (например, "15")
    private int lessonCount; // Количество пар
    private List<Lesson> lessons;

    public CalendarDay(String dayOfWeek, String date, int lessonCount, List<Lesson> lessons) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.lessonCount = lessonCount;
        this.lessons = lessons;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }
    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
    public List<Lesson> getLessons() {
        return lessons;
    }

}
