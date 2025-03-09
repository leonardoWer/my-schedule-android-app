package com.example.myschedule.schedule.items;

import java.util.List;

public class CalendarDay {

    private String dayOfWeekName;
    private long date;
    private int lessonCount;
    private List<Lesson> lessons;

    public CalendarDay(String dayOfWeekName, long date, int lessonCount, List<Lesson> lessons) {
        this.dayOfWeekName = dayOfWeekName;
        this.date = date;
        this.lessonCount = lessonCount;
        this.lessons = lessons;
    }

    public String getDayOfWeek() {
        return dayOfWeekName;
    }

    public long getDate() {
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
