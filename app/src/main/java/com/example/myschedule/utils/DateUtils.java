package com.example.myschedule.utils;

import android.util.Log;

import com.example.myschedule.editor.items.Semester;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String DAY_OF_WEEK_NAME_FORMAT = "EEEE";
    private static final String DATE_AND_MONTH_NAME_FORMAT = "d MMMM";
    private static final String TIME_FORMAT = "HH:mm";

    // Делает из даты строку формата 09.03.25
    public static String formatDate(Date date) {
        if (date == null) {
            Log.i("DateUtils", "Formatting date error: date = null");
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    // Делает из строки формата 09.03.25 формат March ... типа Date
    public static Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            Log.i("DateUtils", "Parsing date error: " + e);
            return null;
        }
    }

    // Возвращает текущую дату в формате Date
    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Возвращает текущую дату в формате long
    public static long getCurrentDateInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // Возвращает текущую дату в формате (String) 09.03.2025
    public static String getCurrentStringDate() {
        return formatDate(getCurrentDate());
    }

    // Переводит long (timestamp) в строку формата "dd.MM.yyyy"
    public static String formatLongToString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Переводит long (timestamp) в строку названия дня недели (например, "Понедельник")
    public static String getDayOfWeekNameFromLong(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_OF_WEEK_NAME_FORMAT, new Locale("ru")); // Полное название дня недели на русском
        return sdf.format(new Date(timestamp));
    }

    // Переводит long (timestamp) в строку типа "9 февраля"
    public static String getDateAndMonthNameFromLong(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_AND_MONTH_NAME_FORMAT, new Locale("ru")); // "d" - число, "MMMM" - название месяца на русском
        return sdf.format(new Date(timestamp));
    }

    // Устанавливает нулевые значения для времени (часы, минуты, секунды, миллисекунды)
    public static void setOnlyImportantDataForCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static Calendar getCalendarFromTimeString(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            Date date = sdf.parse(time);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            }
        } catch (ParseException e) {
            Log.d("DateUtils", "Error in calculating end lesson time");
            return null;
        }
        return null;
    }

    public static String getEndLessonTime(String startTime) {
        Calendar calendar = getCalendarFromTimeString(startTime);
        if (calendar != null) {
            calendar.add(Calendar.MINUTE, 90);
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            return sdf.format(calendar.getTime());
        } else {
            Log.e("DateUtils", "Не удалось получить Calendar из startTime: " + startTime);
            return "";
        }
    }

}
