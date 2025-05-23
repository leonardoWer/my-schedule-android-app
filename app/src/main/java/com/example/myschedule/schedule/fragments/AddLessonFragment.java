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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.editor.items.TimetableSpinnerItem;
import com.example.myschedule.editor.managers.PersonsAndPlacesManager;
import com.example.myschedule.editor.managers.TimetableManager;
import com.example.myschedule.lessons.LessonsManager;
import com.example.myschedule.lessons.items.Discipline;
import com.example.myschedule.schedule.ScheduleManager;
import com.example.myschedule.schedule.adapters.TimetableSpinnerAdapter;
import com.example.myschedule.schedule.items.Lesson;
import com.example.myschedule.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class AddLessonFragment extends Fragment {

    private ImageButton goBackButton;
    private AutoCompleteTextView lessonNameAutoText, assessmentTypeAutoText;
    private TextView dateTextView;
    private RelativeLayout datePickerDialogRelative;
    private Spinner timePickerSpinner;
    private AutoCompleteTextView personAutoText, placeAutoText;
    private Button saveButton;

    private TimetableSpinnerAdapter timetableSpinnerAdapter;

    private Context context;
    private MainActivity mainActivity;
    private ScheduleManager scheduleManager;
    private TimetableManager timetableManager;
    private LessonsManager lessonsManager;
    private PersonsAndPlacesManager personsAndPlacesManager;

    private int currentSemester;
    private String lessonName, lessonAssessmentType;
    private long lessonDate = -1;
    private Lesson.RepeatType lessonRepeatType = Lesson.RepeatType.NOT;
    private String lessonStartTime, lessonEndTime;
    private String teacher, place;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_lesson, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        goBackButton = view.findViewById(R.id.go_back_button);

        lessonNameAutoText = view.findViewById(R.id.add_lesson_name_auto_text);
        assessmentTypeAutoText = view.findViewById(R.id.add_lesson_assessment_type_auto_text);

        dateTextView = view.findViewById(R.id.add_lesson_date_text);
        datePickerDialogRelative = view.findViewById(R.id.add_lesson_date_dialog_relative_layout);
        timePickerSpinner = view.findViewById(R.id.add_lesson_lesson_start_time_spinner);

        personAutoText = view.findViewById(R.id.add_lesson_teacher_auto_text);
        placeAutoText = view.findViewById(R.id.add_lesson_place_auto_text);

        saveButton = view.findViewById(R.id.add_lesson_save_button);

        // Загружаем страницу
        initAddLessonFragment();
    }

    private void initAddLessonFragment() {
        // Объявляем контекст
        initManagers();

        // Устанавливаем обработчики
        initButtons();
        initAdapters();
    }

    private void initManagers() {
        // Получаем контекст
        context = requireContext();
        mainActivity = (MainActivity) requireActivity();
        currentSemester = mainActivity.getCurrentSemesterNumber();

        // Получаем менеджеры
        scheduleManager = new ScheduleManager(context);
        timetableManager = new TimetableManager(context);
        lessonsManager = new LessonsManager(context);
        personsAndPlacesManager = new PersonsAndPlacesManager(context);
    }

    private void initButtons() {
        goBackButton.setOnClickListener(v -> closeFragment());
        datePickerDialogRelative.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> addLesson());
    }

    private void initAdapters() {
        // Адаптер для выбора времени пары
        List<TimetableSpinnerItem> timetable = timetableManager.getTimetableSpinnerItems();
        timetableSpinnerAdapter = new TimetableSpinnerAdapter(context, timetable);
        timePickerSpinner.setAdapter(timetableSpinnerAdapter);

        // Адаптер для предметов
        List<Discipline> disciplines = lessonsManager.getDisciplinesOnSemester(currentSemester);
        if (!disciplines.isEmpty()) {
            List<String> lessonNames = disciplines.stream()
                    .map(Discipline::getName)  // Преобразуем Discipline в имя (String)
                    .distinct()            // Оставляем только уникальные значения
                    .collect(Collectors.toList()); // Собираем в List

            // Создаём адаптер
            ArrayAdapter<String> lessonNameAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, lessonNames);
            lessonNameAutoText.setAdapter(lessonNameAdapter);
        }

        // Адаптер для типа занятия
        String[] assessmentTypes = {"Лекция", "Практика", "Семинар", "Спорт"};
        ArrayAdapter<String> assessmentTypeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, assessmentTypes);
        assessmentTypeAutoText.setAdapter(assessmentTypeAdapter);

        // Адаптер для персон
        List<String> persons = personsAndPlacesManager.getPersons();
        personAutoText.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, persons));

        // Адаптер для мест
        List<String> places = personsAndPlacesManager.getPlaces();
        placeAutoText.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, places));
    }

    private void addLesson() {
        // Данные о предмете
        lessonName = lessonNameAutoText.getText().toString();
        lessonAssessmentType = assessmentTypeAutoText.getText().toString();

        // Время
        lessonStartTime = getLessonStartTime();
        lessonEndTime = DateUtils.getEndLessonTime(lessonStartTime);

        // Препод и место
        teacher = personAutoText.getText().toString();
        place = placeAutoText.getText().toString();

        // Добавляем новый предмет если нет ошибок
        if (checkNotErrorsInData()) {
            switch (lessonRepeatType) {
                case NOT:
                    addOneLessonToTheSchedule();
                    break;
                case EVERY_WEEK:
                    addMultipleLessonsToTheSchedule(7);
                    break;
                case ONE_TIME_A_TWO_WEEKS:
                    addMultipleLessonsToTheSchedule(14);
                    break;
            }
        }
    }

    private void addOneLessonToTheSchedule() {
        Lesson newLesson = new Lesson(lessonName, lessonAssessmentType, 0, lessonDate, lessonRepeatType, lessonStartTime, lessonEndTime, teacher, place, currentSemester);

        // Добавляем предмет
        scheduleManager.addLesson(newLesson, new ScheduleManager.LessonCallback() {
            @Override
            public void onSuccess() {
                Log.d("AddNewLessonFragment", "Added new lesson " + newLesson);
                // Обновляем расписание
                mainActivity.runOnUiThread(() -> {
                    mainActivity.refreshSchedule();
                    closeFragment();
                });
            }

            @Override
            public void onError(String message) {
                Log.d("AddLessonFragment", "Error in adding lesson " + message);
                mainActivity.runOnUiThread(() -> {
                    Toast.makeText(context, "Произошла ошибка: не удалось добавить предмет", Toast.LENGTH_LONG).show();
                    closeFragment();
                });
            }
        });
    }

    private void addMultipleLessonsToTheSchedule(int repeatInterval) {
        // Работаем с датами
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lessonDate); // Устанавливаем дату начала
        long endDate = mainActivity.getCurrentSemesterEndDate(); // Получаем дату окончания семестра

        // Создаём предметы
        List<Lesson> lessonsToAdd = new ArrayList<>();
        while (calendar.getTimeInMillis() <= endDate) {
            long dateToNewLessonAdding = calendar.getTimeInMillis();

            // Создаем новый Lesson
            Lesson newLesson = new Lesson(
                    lessonName,
                    lessonAssessmentType,
                    0, // TODO: либо убрать либо заменить на метод получения номера дня в неделе
                    dateToNewLessonAdding,
                    lessonRepeatType,
                    lessonStartTime,
                    lessonEndTime,
                    teacher,
                    place,
                    currentSemester
            );

            lessonsToAdd.add(newLesson); // Добавляем урок в список

            // Переходим к следующей дате
            calendar.add(Calendar.DAY_OF_MONTH, repeatInterval);
        }

        // Теперь у нас есть список уроков, который нужно добавить в БД
        scheduleManager.addLessons(lessonsToAdd, new ScheduleManager.LessonCallback() {
            @Override
            public void onSuccess() {
                Log.d("AddNewLessonFragment", "Added " + lessonsToAdd.size() + " lessons successfully!");
                // Обновляем расписание
                mainActivity.runOnUiThread(() -> {
                    mainActivity.refreshSchedule();
                    closeFragment();
                });
            }

            @Override
            public void onError(String message) {
                Log.e("AddLessonFragment", "Error in adding lessons: " + message);
                mainActivity.runOnUiThread(() -> {
                    Toast.makeText(context, "Произошла ошибка: не удалось добавить предметы", Toast.LENGTH_LONG).show();
                    closeFragment();
                });
            }
        });
    }


    private void showDatePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_lesson_date_picker, null);

        // Находим элементы
        DatePicker datePicker = dialogView.findViewById(R.id.dialog_lesson_date_picker);
        RadioGroup repeatRadioGroup = dialogView.findViewById(R.id.dialog_lesson_date_picker_repeat_radio_group);
        RadioButton selectedRB = dialogView.findViewById(getSelectedRepeatTypeRBId());
        selectedRB.setChecked(true);
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
            lessonNameAutoText.setError("Введите название пары");
            haveErrors = true;
        }
        if (lessonAssessmentType.isEmpty()) {
            assessmentTypeAutoText.setError("Выберите тип занятия");
            haveErrors = true;
        }
        if (lessonDate == -1) {
            dateTextView.setError("Выберите дату занятия");
            haveErrors = true;
        }
        if (lessonStartTime.isEmpty() || lessonEndTime.isEmpty()) {
            haveErrors = true;
        }

        return !haveErrors;
    }

    private String getLessonStartTime() {
        TimetableSpinnerItem selectedItem = (TimetableSpinnerItem) timePickerSpinner.getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.getStartTime();
        }
        return timetableManager.getLessonTime("1"); // По умолчанию возвращаем время первой пары
    }

    private int getSelectedRepeatTypeRBId() {
        switch (lessonRepeatType) {
            case NOT:
                return R.id.dialog_lesson_date_picker_not_repeat;
            case ONE_TIME_A_TWO_WEEKS:
                return R.id.dialog_lesson_date_picker_two_weeks_repeat;
            case EVERY_WEEK:
                return R.id.dialog_lesson_date_picker_every_repeat;
            default:
                return R.id.dialog_lesson_date_picker_not_repeat;
        }
    }

    private void closeFragment() {
        mainActivity.closeAddLessonFragment();
    }

}