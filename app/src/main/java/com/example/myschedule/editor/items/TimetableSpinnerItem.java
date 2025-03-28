package com.example.myschedule.editor.items;

import androidx.annotation.NonNull;

public class TimetableSpinnerItem {

    private String lessonNumber;
    private String startTime;

    public TimetableSpinnerItem(String lessonNumber, String startTime) {
        this.lessonNumber = lessonNumber;
        this.startTime = startTime;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    @NonNull
    @Override
    public String toString() {
        return startTime + " (" + lessonNumber + " пара)"; // Отображение в Spinner
    }
}
