package com.example.myschedule.editor.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class TimetableManager {

    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;

    private final String PREFS_NAME = "timetable";
    private final String KEY_LESSON_NUMBER_TIME = "timetable_lesson_number_";

    public TimetableManager(Context context) {
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setLessonTime(String lessonNumber, String lessonTime) {
        editor.putString(KEY_LESSON_NUMBER_TIME + lessonNumber, lessonTime);
        editor.apply();
    }

    public String getLessonTime(String lessonNumber) {
        return sp.getString(KEY_LESSON_NUMBER_TIME + lessonNumber,"");
    }

    public HashMap<String, String> getTimetable() {
        HashMap<String, String> timetable = new HashMap<>();

        for (int i=1; i<=8; i++) {
            String lessonNumber = String.valueOf(i);
            String lessonTime = getLessonTime(lessonNumber);

            if (lessonTime.isEmpty()) {
                initDefaultTimetable();
                getTimetable();
            }

            timetable.put(lessonNumber, lessonTime);
        }

        return timetable;
    }

    private void initDefaultTimetable() {
        setLessonTime("1", "08:30");
        setLessonTime("2", "10:10");
        setLessonTime("3", "12:10");
        setLessonTime("4", "13:50");
        setLessonTime("5", "15:30");
        setLessonTime("6", "17:10");
        setLessonTime("7", "18:50");
        setLessonTime("8", "20:30");
    }
}
