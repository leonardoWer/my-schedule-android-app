package com.example.myschedule.actual.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.schedule.items.CalendarDay;
import com.example.myschedule.schedule.items.Lesson;
import com.example.myschedule.user.UserDataManager;
import com.example.myschedule.utils.DateUtils;
import com.example.myschedule.utils.StringUtils;

import java.util.Calendar;
import java.util.List;


public class ActualFragment extends Fragment {

    private TextView actualText;
    private LinearLayout actualLinearLayout;

    private CalendarDay todayCalendarDay;

    private Context context;
    private MainActivity mainActivity;
    private UserDataManager userDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        actualText = view.findViewById(R.id.actual_fragment_actual_text);
        actualLinearLayout = view.findViewById(R.id.actual_fragment_linear_layout);

        // Получаем менеджер
        context = getContext();
        mainActivity = (MainActivity) requireActivity();
        if (context != null) {
            userDataManager = new UserDataManager(context);
        }

        // Загружаем страницу
        initGreeting();
        loadTodayScheduleWhenReady();
    }

    private void loadTodayScheduleWhenReady() {
        if (mainActivity.isScheduleLoaded()) {
            todayCalendarDay = mainActivity.getTodaySchedule();
            initTodayCalendarDayView();
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(this::loadTodayScheduleWhenReady, 500); // Пытемся загрузить расписание каждые 500 мс
        }
    }

    private void initTodayCalendarDayView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View todayCalendarDayView = inflater.inflate(R.layout.calendar_day_item, null);

        // Находим элементы
        TextView dayOfWeekNameTextView = todayCalendarDayView.findViewById(R.id.calendar_day_day_text);
        TextView dateTextView = todayCalendarDayView.findViewById(R.id.calendar_day_date_text);
        TextView lessonCountTextView = todayCalendarDayView.findViewById(R.id.calendar_day_lessons_cnt_text);
        LinearLayout lessonsLinearLayout = todayCalendarDayView.findViewById(R.id.calendar_day_lessons_linear_layout);

        // Устанавливаем значения
        dayOfWeekNameTextView.setText(todayCalendarDay.getDayOfWeek());
        dateTextView.setText(DateUtils.getDateAndMonthNameFromLong(todayCalendarDay.getDate()));
        lessonCountTextView.setText(StringUtils.formatLessonCount(todayCalendarDay.getLessonCount()));
        lessonsLinearLayout.removeAllViews();
        List<Lesson> lessons = todayCalendarDay.getLessons();
        if (lessons.isEmpty()) {
            lessonCountTextView.setText("пар нет");
        } else {
            for (Lesson lesson : lessons) {
                addLessonView(lesson, lessonsLinearLayout);
            }
        }

        // Устанавливаем отступы
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                context.getResources().getDisplayMetrics()
        );
        params.setMargins(margin, margin, margin, 0);
        todayCalendarDayView.setLayoutParams(params);

        // Добавляем на страницу
        actualLinearLayout.addView(todayCalendarDayView);
    }
    private void addLessonView(Lesson lesson, LinearLayout lessonsLinearLayout) {
        //  Инфлейтим разметку lesson_item.xml
        View lessonView = LayoutInflater.from(lessonsLinearLayout.getContext()).inflate(R.layout.lesson_item, null);

        //  Получаем ссылки на элементы View из lesson_item.xml
        TextView lessonNameTextView = lessonView.findViewById(R.id.lesson_item_lesson_name_text);
        TextView assessmentTypeTextView = lessonView.findViewById(R.id.lesson_item_assessment_type);
        TextView startTimeTextView = lessonView.findViewById(R.id.lesson_item_lesson_start_time_text);
        TextView endTimeTextView = lessonView.findViewById(R.id.lesson_item_lesson_end_time_text);
        TextView teacherTextView = lessonView.findViewById(R.id.lesson_item_teacher_text);
        TextView placeTextView = lessonView.findViewById(R.id.lesson_item_place_text);

        // Заполняем данными из Lesson
        lessonNameTextView.setText(lesson.getName());
        assessmentTypeTextView.setText(lesson.getAssessmentType());
        startTimeTextView.setText(lesson.getStartTime());
        endTimeTextView.setText(lesson.getEndTime());
        if (lesson.getTeacher().isEmpty()) {
            teacherTextView.setVisibility(View.GONE);
        } else {
            teacherTextView.setText(lesson.getTeacher());
        }
        if (lesson.getPlace().isEmpty()) {
            placeTextView.setVisibility(View.GONE);
        } else {
            placeTextView.setText(lesson.getPlace());
        }

        // Отступы
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                context.getResources().getDisplayMetrics()
        );
        params.setMargins(0, 0, 0, margin);
        lessonView.setLayoutParams(params);

        // Добавляем
        lessonsLinearLayout.addView(lessonView);
    }

    private void initGreeting() {
        // Получаем текущее время
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting = getGreeting(hour);

        // Устанавливаем приветствие
        actualText.setText(String.format("%s%s", greeting, userDataManager.getUserName()));
    }

    private String getGreeting(int hour) {
        if (hour >= 6 && hour < 12) {
            return "Доброе утро, ";
        } else if (hour >= 12 && hour < 18) {
            return "Добрый день, ";
        } else if (hour >= 18 && hour < 22) {
            return "Добрый вечер, ";
        } else {
            return "Доброй ночи, ";
        }
    }
}