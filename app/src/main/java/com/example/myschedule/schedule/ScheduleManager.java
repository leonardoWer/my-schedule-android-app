package com.example.myschedule.schedule;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.myschedule.db.AppDatabase;
import com.example.myschedule.schedule.items.CalendarDay;
import com.example.myschedule.schedule.items.Lesson;
import com.example.myschedule.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScheduleManager {

    private final AppDatabase db;

    public ScheduleManager(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "schedule-db").build();
    }

    public List<CalendarDay> getSchedule(int semesterId, Date startDate, Date endDate) {
        List<CalendarDay> calendarDays = generateCalendarDays(startDate, endDate);
        List<Lesson> allLessons = db.lessonDao().getLessonsBySemesterIdAndDateRange(semesterId, startDate, endDate);

        for (CalendarDay calendarDay : calendarDays) {
            String calendarDayDate = calendarDay.getDate();
            for (Lesson lesson : allLessons) {
                if (Objects.equals(DateUtils.formatDate(lesson.getStartDate()), calendarDayDate)) {
                    calendarDay.getLessons().add(lesson);
                }
            }

            calendarDay.setLessonCount(calendarDay.getLessons().size());

            Log.d("Schedule manager", "Added new calendar day on " + calendarDay.getDate() + ", lesson cnt: " + calendarDay.getLessonCount());
        }

        return calendarDays;
    }

    private List<CalendarDay> generateCalendarDays(Date startDate, Date endDate) {
        List<CalendarDay> calendarDays = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("ru")); // Полное название дня недели на русском

        while (!calendar.getTime().after(endDate)) {
            Date currentDate = calendar.getTime();
            String dayOfWeekName = dayOfWeekFormat.format(currentDate);
            String date = DateUtils.formatDate(currentDate);

            calendarDays.add(new CalendarDay(dayOfWeekName, date, 0, new ArrayList<>()));

            Log.d("ScheduleManager", "Generating calendar day: " + dayOfWeekName + ", date: " + date);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Переходим к следующему дню
        }

        return calendarDays;
    }
}
