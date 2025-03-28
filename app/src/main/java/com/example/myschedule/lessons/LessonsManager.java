package com.example.myschedule.lessons;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.example.myschedule.lessons.items.Discipline;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LessonsManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private final String PREFS_NAME = "disciplines";
    private final String KEY_DISCIPLINES_ON_SEMESTER = "disciplines_on_semester_";


    public LessonsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setDisciplinesOnSemester(int semester, List<Discipline> disciplines) {
        Gson gson = new Gson();
        String json = gson.toJson(disciplines);
        editor.putString(KEY_DISCIPLINES_ON_SEMESTER + semester, json);
        editor.apply();
    }

    public List<Discipline> getDisciplinesOnSemester(int semester) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_DISCIPLINES_ON_SEMESTER + semester, null);
        Type type = new TypeToken<List<Discipline>>() {}.getType();
        List<Discipline> disciplines = gson.fromJson(json, type);

        if (disciplines == null) {
            disciplines = new ArrayList<>();
        }

        return disciplines;
    }

    public void addDiscipline() {

    }
}
