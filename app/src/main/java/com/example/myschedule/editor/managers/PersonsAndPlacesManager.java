package com.example.myschedule.editor.managers;

import android.content.SharedPreferences;
import android.content.Context;

import com.example.myschedule.lessons.items.Discipline;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PersonsAndPlacesManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private final String PREFS_NAME = "persons_and_places";
    private final String KEY_PERSONS = "persons";
    private final String KEY_PLACES = "places";


    public PersonsAndPlacesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setPersons(List<String> persons) {
        Gson gson = new Gson();
        String json = gson.toJson(persons);
        editor.putString(KEY_PERSONS, json);
        editor.apply();
    }
    public List<String> getPersons() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_PERSONS, null);
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> persons = gson.fromJson(json, type);

        return (persons == null) ? new ArrayList<>() : persons;
    }

    public void setPlaces(List<String> places) {
        Gson gson = new Gson();
        String json = gson.toJson(places);
        editor.putString(KEY_PLACES, json);
        editor.apply();
    }
    public List<String> getPlaces() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_PLACES, null);
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> places = gson.fromJson(json, type);

        return (places == null) ? new ArrayList<>() : places;
    }
}
