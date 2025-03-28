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

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.editor.managers.PersonsAndPlacesManager;
import com.example.myschedule.editor.managers.TimetableManager;
import com.example.myschedule.user.UserDataManager;
import com.example.myschedule.utils.DateUtils;
import com.example.myschedule.utils.LayoutUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class EditorFragment extends Fragment {

    private EditText currentSemesterEditText;
    private LinearLayout timetableLinearLayout;
    private LinearLayout personsLinearLayout, placesLinearLayout;
    private ImageButton addPersonImageButton, addPlaceImageButton;

    private Context context;
    private MainActivity mainActivity;
    private UserDataManager userDataManager;
    private TimetableManager timetableManager;
    private PersonsAndPlacesManager personsAndPlacesManager;

    private HashMap<String, String> timetable = new HashMap<>();
    private List<String> persons, places;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        currentSemesterEditText = view.findViewById(R.id.editor_current_semester_edit_text);
        timetableLinearLayout = view.findViewById(R.id.editor_timetable_linear_layout);
        personsLinearLayout = view.findViewById(R.id.editor_persons_linear_layout);
        placesLinearLayout = view.findViewById(R.id.editor_places_linear_layout);
        addPersonImageButton = view.findViewById(R.id.editor_add_person_image_button);
        addPlaceImageButton = view.findViewById(R.id.editor_add_place_image_button);

        // Создаём менеджеры
        context = getContext();
        mainActivity = (MainActivity) requireActivity();
        if (context != null) {
            userDataManager = new UserDataManager(context);
            timetableManager = new TimetableManager(context);
            personsAndPlacesManager = new PersonsAndPlacesManager(context);
        }

        // Загружаем фрагмент
        initEditorFragment();
    }

    private void initEditorFragment() {
        initCurrentSemester();
        initTimetable();
        initPersonsAndPlaces();
    }

    private void initCurrentSemester() {
        // Получаем значения
        int currentSemester = userDataManager.getUserCurrentSemester();

        // Устанавливаем значения
        currentSemesterEditText.setText(String.valueOf(currentSemester));
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
            loadPersonsAndPlaces(persons, personsLinearLayout, "persons");
        }
        if (!places.isEmpty()) {
            loadPersonsAndPlaces(places, placesLinearLayout, "places");
        }

        // Устанавливаем обработчики
        addPersonImageButton.setOnClickListener(v -> showAddPersonsAndPlacesDialog("Добавить персону", "persons"));
        addPlaceImageButton.setOnClickListener(v -> showAddPersonsAndPlacesDialog("Добавить место", "places"));
    }

    private void loadPersonsAndPlaces(List<String> lst, LinearLayout parent, String personsOrPlaces) {
        parent.removeAllViews();
        for (String str : lst) {
            addPersonsAndPlacesView(str, parent, personsOrPlaces);
        }
    }
    private void addPersonsAndPlacesView(String text, LinearLayout parent, String personsOrPlaces) {
        // Надуваем макете
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.persons_and_places_item, null);

        // Находим элементы
        TextView textView = item.findViewById(R.id.persons_and_places_item_text);
        ImageView icon = item.findViewById(R.id.persons_and_places_item_icon);

        // Устанавливаем значения
        textView.setText(text);

        Drawable iconDrawable = ContextCompat.getDrawable(context, R.drawable.pin);
        if (personsOrPlaces.equals("persons")) {
            iconDrawable = ContextCompat.getDrawable(context, R.drawable.person);
        } else if (personsOrPlaces.equals("places")) {
            iconDrawable = ContextCompat.getDrawable(context, R.drawable.location);
        }
        icon.setImageDrawable(iconDrawable);

        // Устанавливаем обработчики
        item.setOnClickListener(v -> showEditPersonsAndPlacesDialog(text));

        // Устанавливаем отступы
        LayoutUtils.setMargins(context, item, 2, 4, 2, 4);

        // Добавляем в ll
        parent.addView(item);
    }

    private void showAddPersonsAndPlacesDialog(String titleText, String personsOrPlaces) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Надуваем макет
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.dialog_add_person_and_places, null);
        builder.setView(item);

        // Находим элементы
        TextView titleTextView = item.findViewById(R.id.dialog_add_person_and_places_title_text);
        EditText editText = item.findViewById(R.id.dialog_add_person_and_places_edit_text);
        Button saveButton = item.findViewById(R.id.dialog_add_person_and_places_save_button);
        ImageButton closeButton = item.findViewById(R.id.dialog_add_person_and_places_close_image_button);

        // Устанавливаем значения
        titleTextView.setText(titleText);

        // Создаём диалог
        AlertDialog dialog = builder.create();

        // Обрабатываем нажатия
        saveButton.setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (text.isEmpty()) {
                editText.setError("Введите что-нибудь");
                return;
            }

            // Обновляем нужный список
            if (personsOrPlaces.equals("persons")) {
                persons.add(text);
                personsAndPlacesManager.setPersons(persons);
                loadPersonsAndPlaces(persons, personsLinearLayout, personsOrPlaces);
            } else if (personsOrPlaces.equals("places")) {
                places.add(text);
                personsAndPlacesManager.setPlaces(places);
                loadPersonsAndPlaces(places, placesLinearLayout, personsOrPlaces);
            }

            // Закрываем диалог
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Показываем диалог
        dialog.show();
    }

    private void showEditPersonsAndPlacesDialog(String text) {

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