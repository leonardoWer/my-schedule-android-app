package com.example.myschedule.schedule.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.schedule.ScheduleManager;
import com.example.myschedule.schedule.items.Lesson;
import com.example.myschedule.utils.DateUtils;

import java.util.Calendar;

public class AddLessonFragment extends Fragment {

    private ImageButton goBackButton;
    private AutoCompleteTextView lessonNameText, assessmentTypeText;
    private TextView dateTextView;
    private ImageButton datePickerButton;
    private Spinner timePickerSpinner;
    private AutoCompleteTextView teacherText, placeText;
    private Button saveButton;

    private String lessonName, lessonAssessmentType;
    private long lessonDate = -1;
    private Lesson.RepeatType lessonRepeatType = Lesson.RepeatType.NOT;
    private String lessonStartTime;
    private String teacher, place;

    private Context context;
    private MainActivity mainActivity;
    private ScheduleManager scheduleManager;
    private int currentSemester;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_lesson, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        goBackButton = view.findViewById(R.id.go_back_button);
        lessonNameText = view.findViewById(R.id.add_lesson_name_auto_text);
        assessmentTypeText = view.findViewById(R.id.add_lesson_assessment_type_auto_text);
        dateTextView = view.findViewById(R.id.add_lesson_date_text);
        datePickerButton = view.findViewById(R.id.add_lesson_date_dialog_image_button);
        timePickerSpinner = view.findViewById(R.id.add_lesson_lesson_start_time_spinner);
        teacherText = view.findViewById(R.id.add_lesson_teacher_auto_text);
        placeText = view.findViewById(R.id.add_lesson_place_auto_text);
        saveButton = view.findViewById(R.id.add_lesson_save_button);

        // Объявляем контекст
        context = requireContext();
        mainActivity = (MainActivity) requireActivity();
        scheduleManager = new ScheduleManager(context);
        currentSemester = mainActivity.getCurrentSemesterNumber();

        // Устанавливаем обработчики
        initButtons();
    }

    private void initButtons() {
        goBackButton.setOnClickListener(v -> closeFragment());
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> addLesson());
    }

    private void addLesson() {
        // Получаем данные
        lessonName = lessonNameText.getText().toString();
        lessonAssessmentType = assessmentTypeText.getText().toString();

        // Временно
        lessonStartTime = "8:10";

        teacher = teacherText.getText().toString();
        place = placeText.getText().toString();

        // Добавляем новый предмет если нет ошибок
        if (checkNotErrorsInData()) {
            Lesson newLesson = new Lesson(lessonName, lessonAssessmentType, 0, lessonDate, lessonRepeatType, lessonStartTime, "9:40", teacher, place, currentSemester);

            // Добавляем предмет
            scheduleManager.addLesson(newLesson, new ScheduleManager.LessonCallback() {
                @Override
                public void onSuccess() {
                    Log.d("AddNewLessonFragment", "Added new lesson " + newLesson);
                    mainActivity.refreshSchedule();
                }

                @Override
                public void onError(String message) {
                    Log.d("AddLessonFragment", "Error in adding lesson " + message);
                }
            });

            closeFragment();
        }
    }

    private void showDatePickerDialog() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_lesson_date_picker, null);

        // Находим элементы
        DatePicker datePicker = dialogView.findViewById(R.id.dialog_lesson_date_picker);
        RadioGroup repeatRadioGroup = dialogView.findViewById(R.id.dialog_lesson_date_picker_repeat_radio_group);
        ImageButton closeButton = dialogView.findViewById(R.id.dialog_lesson_close_image_button);
        Button saveButton = dialogView.findViewById(R.id.dialog_lesson_save_button);

        // Настраиваем DatePicker (устанавливаем текущую дату)
        Calendar calendar = Calendar.getInstance();
        if (lessonDate > 0) {
            calendar.setTimeInMillis(lessonDate);
        }
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Устанавливаем view для AlertDialog
        builder.setView(dialogView);

        // Создаем AlertDialog
        AlertDialog alertDialog = builder.create();

        // Обработчики кликов
        saveButton.setOnClickListener(v -> {
            // Получаем выбранную дату
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int dayOfMonth = datePicker.getDayOfMonth();
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            DateUtils.setOnlyImportantDataForCalendar(selectedCalendar);

            lessonDate = selectedCalendar.getTimeInMillis();
            dateTextView.setText(DateUtils.getDateAndMonthNameFromLong(lessonDate));

            // Получаем выбранный тип повторения
            int selectedRadioButtonId = repeatRadioGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId == R.id.dialog_lesson_date_picker_every_repeat) {
                lessonRepeatType = Lesson.RepeatType.EVERY_WEEK;
            } else if (selectedRadioButtonId == R.id.dialog_lesson_date_picker_two_weeks_repeat) {
                lessonRepeatType = Lesson.RepeatType.ONE_TIME_A_TWO_WEEKS;
            } else {
                lessonRepeatType = Lesson.RepeatType.NOT;
            }

            // Закрываем диалог
            alertDialog.dismiss();
        });

        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        // Покажем диалог
        alertDialog.show();
    }

    private boolean checkNotErrorsInData() {
        boolean haveErrors = false;
        if (lessonName.isEmpty()) {
            lessonNameText.setError("Введите название пары");
            haveErrors = true;
        }
        if (lessonAssessmentType.isEmpty()) {
            assessmentTypeText.setError("Выберите тип занятия");
            haveErrors = true;
        }
        if (lessonDate == -1) {
            dateTextView.setError("Выберите дату занятия");
            haveErrors = true;
        }

        return !haveErrors;
    }

    private void closeFragment() {
        mainActivity.closeAddLessonFragment();
    }

}