package com.example.myschedule.user;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private final String PREFS_NAME = "user_data";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_USER_CURRENT_SEMESTER = "user_current_semester";


    public UserDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public void setUserCurrentSemester(int currentSemester) {
        editor.putInt(KEY_USER_CURRENT_SEMESTER, currentSemester);
        editor.apply();
    }
    public int getUserCurrentSemester() {
        return sharedPreferences.getInt(KEY_USER_CURRENT_SEMESTER, 0);
    }


    public boolean isUserFirstTime() {
        return getUserName().isEmpty() || getUserCurrentSemester() == 0;
    }
}
