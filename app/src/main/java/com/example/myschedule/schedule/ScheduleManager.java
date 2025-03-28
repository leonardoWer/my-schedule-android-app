package com.example.myschedule.schedule;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.myschedule.db.AppDatabase;
import com.example.myschedule.schedule.items.CalendarDay;
import com.example.myschedule.schedule.items.Lesson;
import com.example.myschedule.utils.DateUtils;
import com.example.myschedule.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleManager {

    private final AppDatabase db;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ScheduleManager(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "schedule-db").build();
    }

    public interface ScheduleCallback {
        void onScheduleLoaded(List<CalendarDay> schedule);
        void onError(String error);
    }

    public void getSchedule(int semesterId, long startDate, long endDate, ScheduleCallback callback) {
        executorService.execute(() -> {
            try {
                List<CalendarDay> calendarDays = generateCalendarDays(startDate, endDate);
                List<Lesson> allLessons = db.lessonDao().getLessonsBySemesterIdAndDateRange(semesterId, startDate, endDate);

                for (CalendarDay calendarDay : calendarDays) {
                    long calendarDayDate = calendarDay.getDate();
                    for (Lesson lesson : allLessons) {
                        if (calendarDayDate == lesson.getDate()) {
                            Log.d("ScheduleManager", "Added lesson: " + lesson.getName() + " " + DateUtils.formatLongToString(lesson.getDate()) + "\nOn calendarDay: " + DateUtils.formatLongToString(calendarDay.getDate()));
                            calendarDay.getLessons().add(lesson);
                        }
                    }

                    calendarDay.setLessonCount(calendarDay.getLessons().size());
                }

                if (callback != null) {
                    callback.onScheduleLoaded(calendarDays);
                }

            } catch (Exception e) {
                Log.e("ScheduleManager", "Error getting schedule: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    private List<CalendarDay> generateCalendarDays(long startDate, long endDate) {
        List<CalendarDay> calendarDays = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        while (calendar.getTimeInMillis() <= endDate) {
            long currentDateInMillis = calendar.getTimeInMillis();
            String dayOfWeekName = StringUtils.capitalizeFirstLetter(DateUtils.getDayOfWeekNameFromLong(currentDateInMillis));

            calendarDays.add(new CalendarDay(dayOfWeekName, currentDateInMillis, 0, new ArrayList<>()));

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Переходим к следующему дню
        }

        return calendarDays;
    }

    public void addLesson(Lesson lesson, LessonCallback callback) {
        executorService.execute(() -> {
            try {
                db.lessonDao().insert(lesson);
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                Log.e("ScheduleManager", "Error adding lesson: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    //  Метод для удаления предмета
    public void deleteLesson(Lesson lesson, LessonCallback callback) {
        executorService.execute(() -> {
            try {
                db.lessonDao().delete(lesson);
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                Log.e("ScheduleManager", "Error deleting lesson: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    //  Метод для редактирования предмета
    public void updateLesson(Lesson lesson, LessonCallback callback) {
        executorService.execute(() -> {
            try {
                db.lessonDao().update(lesson);
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                Log.e("ScheduleManager", "Error updating lesson: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public interface LessonCallback {
        void onSuccess();
        void onError(String message);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
