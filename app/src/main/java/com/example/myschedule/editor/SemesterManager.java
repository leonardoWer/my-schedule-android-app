package com.example.myschedule.editor;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.myschedule.db.AppDatabase;
import com.example.myschedule.editor.items.Semester;
import com.example.myschedule.utils.DateUtils;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SemesterManager {

    private static final int SEMESTER_CNT = 8;
    private final AppDatabase db;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public SemesterManager(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "schedule-db").build();
    }

    // Создаём семестры в бд 1 раз
    public void initializeSemesters(int currentSemester) {
        executorService.execute(() -> {
            if (db.semesterDao().getAllSemesters().isEmpty()) {
                // Получаем текущий год
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);

                // Генерируем следующие учебные промежутки
                for (int semester = currentSemester; semester <= SEMESTER_CNT; semester++) {
                    Semester newSemester = DateUtils.getCurrentSemester(semester, currentYear);
                    db.semesterDao().insert(newSemester);

                    Log.w("SemesterManager", "Initialize semesters: new semester added id: " + newSemester.getId() + ", end date: " + newSemester.getEndDate());

                    if (semester %2 != 0) {
                        currentYear++;
                    }
                }
            }
        });
    }

    public void getSemesterById(int semesterId, SemesterCallback callback) {
        executorService.execute(() -> {
            Semester semester = db.semesterDao().getSemesterById(semesterId);
            if (callback != null) {
                callback.onSemesterLoaded(semester);
            }
        });
    }

    // Интерфейс для обратного вызова при получении семестра
    public interface SemesterCallback {
        void onSemesterLoaded(Semester semester);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
