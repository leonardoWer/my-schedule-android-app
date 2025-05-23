package com.example.myschedule.editor.fragments;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.editor.items.Semester;
import com.example.myschedule.editor.managers.PersonsAndPlacesManager;
import com.example.myschedule.editor.managers.TimetableManager;
import com.example.myschedule.lessons.LessonsManager;
import com.example.myschedule.schedule.ScheduleManager;
import com.example.myschedule.user.UserDataManager;
import com.example.myschedule.utils.DateUtils;
import com.example.myschedule.utils.LayoutUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class EditorFragment extends Fragment {

    private EditText currentSemesterEditText;
    private EditText currentSemesterStartDateEditText, currentSemesterEndDateEditText;
    private LinearLayout timetableLinearLayout;
    private LinearLayout personsLinearLayout, placesLinearLayout;
    private ImageButton addPersonImageButton, addPlaceImageButton;
    private Button deleteScheduleButton, deleteLessonsButton;

    private Context context;
    private MainActivity mainActivity;
    private UserDataManager userDataManager;
    private TimetableManager timetableManager;
    private PersonsAndPlacesManager personsAndPlacesManager;
    private LessonsManager lessonsManager;
    private ScheduleManager scheduleManager;

    private int currentSemester;
    private HashMap<String, String> timetable = new HashMap<>();
    private List<String> persons, places;
    private final String PERSONS_KEY = "persons";
    private final String PLACES_KEY = "places";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        currentSemesterEditText = view.findViewById(R.id.editor_current_semester_edit_text);
        currentSemesterStartDateEditText = view.findViewById(R.id.editor_current_semester_start_date_edit_text);
        currentSemesterEndDateEditText = view.findViewById(R.id.editor_current_semester_end_date_edit_text);
        timetableLinearLayout = view.findViewById(R.id.editor_timetable_linear_layout);
        personsLinearLayout = view.findViewById(R.id.editor_persons_linear_layout);
        placesLinearLayout = view.findViewById(R.id.editor_places_linear_layout);
        addPersonImageButton = view.findViewById(R.id.editor_add_person_image_button);
        addPlaceImageButton = view.findViewById(R.id.editor_add_place_image_button);
        deleteScheduleButton = view.findViewById(R.id.editor_delete_schedule);
        deleteLessonsButton = view.findViewById(R.id.editor_delete_disciplines);

        // Загружаем фрагмент
        initEditorFragment();
    }

    private void initEditorFragment() {
        initManagers();
        initCurrentSemester();
        initTimetable();
        initPersonsAndPlaces();
        initDeleteButtons();
    }

    private void initManagers() {
        // Создаём менеджеры
        context = getContext();
        mainActivity = (MainActivity) requireActivity();
        if (context != null) {
            userDataManager = new UserDataManager(context);
            timetableManager = new TimetableManager(context);
            personsAndPlacesManager = new PersonsAndPlacesManager(context);
            lessonsManager = new LessonsManager(context);
            scheduleManager = new ScheduleManager(context);
        }
    }

    private void initCurrentSemester() {
        // Получаем значения
        currentSemester = userDataManager.getUserCurrentSemester();
        String currentSemesterStartDate = DateUtils.formatLongToString(mainActivity.getCurrentSemesterStartDate());
        String currentSemesterEndDate = DateUtils.formatLongToString(mainActivity.getCurrentSemesterEndDate());

        // Устанавливаем значения
        try {
            currentSemesterEditText.setText(String.valueOf(currentSemester));
            currentSemesterStartDateEditText.setText(currentSemesterStartDate);
            currentSemesterEndDateEditText.setText(currentSemesterEndDate);
        } catch (Error e) {
            Log.w("EditorFragment", "Не удалось установить параметры текущего семестра");
        }
    }

    private void initTimetable() {
        timetable = timetableManager.getTimetable();
        for (int i = 1; i <= 8; i++) {
            // Получаем все пары
            String lessonNumber = String.valueOf(i);
            String lessonTime = timetable.get(lessonNumber);

            // Добавляем на страницу
            addTimetableItemView(lessonNumber, lessonTime);
        }
    }

    private void initPersonsAndPlaces() {
        // Загружаем списки
        persons = personsAndPlacesManager.getPersons();
        places = personsAndPlacesManager.getPlaces();

        // Заполняем страницу
        if (!persons.isEmpty()) {
            loadPersonsOrPlaces(persons, personsLinearLayout, PERSONS_KEY);
        }
        if (!places.isEmpty()) {
            loadPersonsOrPlaces(places, placesLinearLayout, PLACES_KEY);
        }

        // Устанавливаем обработчики
        addPersonImageButton.setOnClickListener(v -> showAddPersonsAndPlacesDialog("Добавить персону", PERSONS_KEY, ""));
        addPlaceImageButton.setOnClickListener(v -> showAddPersonsAndPlacesDialog("Добавить место", PLACES_KEY, ""));
    }

    private void initDeleteButtons() {
        deleteScheduleButton.setOnClickListener(v -> showConfirmationDialog("Вы уверены, что хотите удалить расписание за текущий семестр?", this::clearScheduleCurrentSemester));
        deleteLessonsButton.setOnClickListener(v -> showConfirmationDialog("Вы уверены, что хотите удалить все предметы за текущий семестр?", this::clearLessonsCurrentSemester));
    }

    private void showConfirmationDialog(String message, Runnable onConfirmAction) {
        new AlertDialog.Builder(context)
                .setTitle("Внимание!")
                .setMessage(message)
                .setPositiveButton("Да", (dialog, which) -> {
                    onConfirmAction.run();
                    dialog.dismiss();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void clearScheduleCurrentSemester() {
        scheduleManager.deleteALlLessonsInSemester(currentSemester, new ScheduleManager.LessonCallback() {
            @Override
            public void onSuccess() {
                mainActivity.refreshSchedule();
                mainActivity.runOnUiThread(() -> Toast.makeText(context, "Расписание за текущий семестр успешно удалено", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(String message) {
                mainActivity.runOnUiThread(() -> Toast.makeText(context, "Произошла ошибка: не удалось удалить расписание за текущий семестр", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void clearLessonsCurrentSemester() {
        lessonsManager.setDisciplinesOnSemester(currentSemester, new ArrayList<>());
        Toast.makeText(context, "Все предметы успешно удалены", Toast.LENGTH_SHORT).show();
    }

    private void loadPersonsOrPlaces(List<String> lst, LinearLayout parent, String personsOrPlaces) {
        parent.removeAllViews();
        for (String personOrPlace : lst) {
            addPersonsAndPlacesView(personOrPlace, parent, personsOrPlaces);
        }
    }
    private void addPersonsAndPlacesView(String personOrPlace, LinearLayout parent, String personsOrPlaces) {
        // Надуваем макете
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.persons_and_places_item, null);

        // Находим элементы
        TextView personOrPlaceTextView = item.findViewById(R.id.persons_and_places_item_text);
        ImageView icon = item.findViewById(R.id.persons_and_places_item_icon);

        // Устанавливаем значения
        personOrPlaceTextView.setText(personOrPlace);

        Drawable iconDrawable = ContextCompat.getDrawable(context, R.drawable.pin);
        if (personsOrPlaces.equals("persons")) {
            iconDrawable = ContextCompat.getDrawable(context, R.drawable.person);
        } else if (personsOrPlaces.equals("places")) {
            iconDrawable = ContextCompat.getDrawable(context, R.drawable.location);
        }
        icon.setImageDrawable(iconDrawable);

        // Устанавливаем обработчики
        item.setOnClickListener(v -> showAddPersonsAndPlacesDialog("Редактирование", personsOrPlaces, personOrPlace));

        // Устанавливаем отступы
        LayoutUtils.setMargins(context, item, 2, 4, 2, 4);

        // Добавляем в ll
        parent.addView(item);
    }

    private void showAddPersonsAndPlacesDialog(String titleText, String personsOrPlaces, String personOnPlace) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Надуваем макет
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.dialog_add_person_and_places, null);
        builder.setView(item);

        // Находим элементы
        TextView titleTextView = item.findViewById(R.id.dialog_add_person_and_places_title_text);
        EditText contentEditText = item.findViewById(R.id.dialog_add_person_and_places_edit_text);
        Button saveButton = item.findViewById(R.id.dialog_add_person_and_places_save_button);
        ImageButton closeButton = item.findViewById(R.id.dialog_add_person_and_places_close_image_button);

        // Устанавливаем значения
        titleTextView.setText(titleText);
        contentEditText.setText(personOnPlace);

        // Создаём диалог
        AlertDialog dialog = builder.create();

        // Обрабатываем нажатия
        if (!personOnPlace.isEmpty()) {
            ImageButton deleteButton = item.findViewById(R.id.dialog_add_person_and_places_delete_image_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> {
                deletePersonOrPlace(personsOrPlaces, personOnPlace);
                dialog.dismiss();
            });
        }

        saveButton.setOnClickListener(v -> {
            String content = contentEditText.getText().toString();
            if (content.isEmpty()) {
                contentEditText.setError("Введите что-нибудь");
                return;
            }

            // Обновляем нужный список
            if (personsOrPlaces.equals(PERSONS_KEY)) {
                int index = persons.indexOf(personOnPlace);
                if (index >= 0) {
                    persons.set(index, content);
                } else {
                    persons.add(content);
                }
                personsAndPlacesManager.setPersons(persons);
                loadPersonsOrPlaces(persons, personsLinearLayout, personsOrPlaces);
            } else if (personsOrPlaces.equals(PLACES_KEY)) {
                int index = places.indexOf(personOnPlace);
                if (index >= 0) {
                    places.set(index, content);
                } else {
                    places.add(content);
                }
                personsAndPlacesManager.setPlaces(places);
                loadPersonsOrPlaces(places, placesLinearLayout, personsOrPlaces);
            }

            // Закрываем диалог
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Показываем диалог
        dialog.show();
    }

    private void deletePersonOrPlace(String personsOrPlaces, String personOrPlace) {
        if (personsOrPlaces.equals(PERSONS_KEY)) {
            persons.remove(personOrPlace);
            personsAndPlacesManager.setPersons(persons);
            loadPersonsOrPlaces(persons, personsLinearLayout, personsOrPlaces);
            Toast.makeText(context, "Персона успешно удалена", Toast.LENGTH_SHORT).show();
        } else if (personsOrPlaces.equals(PLACES_KEY)) {
            places.remove(personOrPlace);
            personsAndPlacesManager.setPlaces(places);
            loadPersonsOrPlaces(places, placesLinearLayout, personsOrPlaces);
            Toast.makeText(context, "Место успешно удалено", Toast.LENGTH_SHORT).show();
        }
    }


    private void addTimetableItemView(String lessonNumber, String lessonTime) {
        // Надуваем макете
        LayoutInflater inflater = LayoutInflater.from(context);
        View timetableItem = inflater.inflate(R.layout.timetable_item, null);

        // Находим элементы
        TextView lessonNumberTextView = timetableItem.findViewById(R.id.timetable_item_number_text);
        TextView timeTextView = timetableItem.findViewById(R.id.timetable_item_time_text);

        // Устанавливаем значения
        lessonNumberTextView.setText(String.format("%s пара", lessonNumber));
        timeTextView.setText(lessonTime);

        // Устанавливаем обработчики
        timeTextView.setOnClickListener(v -> showTimePickerDialog(lessonNumber, lessonTime, timeTextView));

        // Устанавливаем отступы
        LayoutUtils.setMargins(context, timetableItem, 8, 16, 8, 0);

        // Добавляем в ll
        timetableLinearLayout.addView(timetableItem);
    }

    private void showTimePickerDialog(String lessonNumber, String lessonTime, TextView timeTextView) {
        Calendar calendar = DateUtils.getCalendarFromTimeString(lessonTime);
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Создаем диалог выбора времени
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute1) -> {
            // Обрабатываем выбранное время
            String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
            timeTextView.setText(formattedTime);
            timetableManager.setLessonTime(lessonNumber, formattedTime);

        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void updateCurrentSemester() {
        int newSelectedSemester = Integer.parseInt(currentSemesterEditText.getText().toString());
        if (newSelectedSemester > 0) {
            try {
                userDataManager.setUserCurrentSemester(newSelectedSemester);
                mainActivity.refreshCurrentSemester();
            } catch (Error e) {
                Log.i("Editor fragment", e + ": Uncorrect selected semester");
                initCurrentSemester();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        updateCurrentSemester();
    }
}