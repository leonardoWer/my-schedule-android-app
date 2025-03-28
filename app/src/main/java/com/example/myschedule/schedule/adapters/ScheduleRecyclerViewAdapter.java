package com.example.myschedule.schedule.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myschedule.R;
import com.example.myschedule.schedule.items.CalendarDay;
import com.example.myschedule.schedule.items.Lesson;
import com.example.myschedule.utils.DateUtils;
import com.example.myschedule.utils.LayoutUtils;
import com.example.myschedule.utils.StringUtils;

import java.util.List;

public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

    private List<CalendarDay> calendarDays;
    private Context context;

    public ScheduleRecyclerViewAdapter(Context context, List<CalendarDay> calendarDays) {
        this.context = context;
        this.calendarDays = calendarDays;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarDay calendarDay = calendarDays.get(position);
        holder.bind(calendarDay, context);
    }

    @Override
    public int getItemCount() {
        return calendarDays != null ? calendarDays.size() : 0;
    }

    // TODO: используйте DiffUtil для более эффективного обновления
    public void setCalendarDays(List<CalendarDay> calendarDays) {
        this.calendarDays = calendarDays;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dayOfWeekNameTextView;
        private TextView dateTextView;
        private TextView lessonCountTextView;
        private LinearLayout lessonsLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekNameTextView = itemView.findViewById(R.id.calendar_day_day_text);
            dateTextView = itemView.findViewById(R.id.calendar_day_date_text);
            lessonCountTextView = itemView.findViewById(R.id.calendar_day_lessons_cnt_text);
            lessonsLinearLayout = itemView.findViewById(R.id.calendar_day_lessons_linear_layout);
        }

        public void bind(CalendarDay calendarDay, Context context) {
            // Устанавливаем данные для CalendarDay
            dayOfWeekNameTextView.setText(calendarDay.getDayOfWeek());
            dateTextView.setText(DateUtils.getDateAndMonthNameFromLong(calendarDay.getDate()));
            lessonCountTextView.setText(StringUtils.formatLessonCount(calendarDay.getLessonCount()));

            // Загружаем lessons
            lessonsLinearLayout.removeAllViews();
            List<Lesson> lessons = calendarDay.getLessons();
            if (!lessons.isEmpty()) {
                for (Lesson lesson : lessons) {
                    addLessonView(lesson, context);
                }
            } else {
                lessonCountTextView.setText("пар нет");
            }
        }

        private void addLessonView(Lesson lesson, Context context) {
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

            // Если пустое, скрываем
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
            LayoutUtils.setMargins(context, lessonView, 0, 8, 0, 8);

            // Добавляем
            lessonsLinearLayout.addView(lessonView);
        }
    }
}
