package com.example.myschedule.editor.managers;

import android.content.Context;
import androidx.room.Room;
import com.example.myschedule.db.AppDatabase;

import android.util.Log;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.myschedule.editor.items.Semester;
import com.example.myschedule.utils.DateUtils;

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
                    Semester newSemester = getCurrentSemester(semester, currentYear);
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

    // Методы для создания Semester
    private Semester getCurrentSemester(int semester, int year) {
        if (semester % 2 != 0) {
            return new Semester(semester, getFirstOfSeptember(year), getFirstOfJanuary(year + 1));
        }
        return new Semester(semester, getFirstOfFebruary(year), getFirstOfJune(year));
    }

    private long getFirstOfSeptember(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        DateUtils.setOnlyImportantDataForCalendar(calendar);
        return calendar.getTimeInMillis(); // Возвращаем timestamp
    }

    private long getFirstOfJanuary(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateUtils.setOnlyImportantDataForCalendar(calendar);

        return calendar.getTimeInMillis(); // Возвращаем timestamp
    }

    private long getFirstOfFebruary(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateUtils.setOnlyImportantDataForCalendar(calendar);

        return calendar.getTimeInMillis(); // Возвращаем timestamp
    }

    private long getFirstOfJune(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateUtils.setOnlyImportantDataForCalendar(calendar);

        return calendar.getTimeInMillis(); // Возвращаем timestamp
    }

}
