package com.example.myschedule.editor.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myschedule.R;
import com.example.myschedule.editor.managers.TimetableManager;
import com.example.myschedule.user.UserDataManager;

import org.w3c.dom.Text;

import java.util.HashMap;


public class EditorFragment extends Fragment {

    private EditText currentSemesterEditText;
    private LinearLayout timetableLinearLayout;

    private HashMap<String, String> timetable = new HashMap<>();

    private Context context;
    private UserDataManager userDataManager;
    private TimetableManager timetableManager;

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

        // Создаём менеджеры
        context = getContext();
        if (context != null) {
            userDataManager = new UserDataManager(context);
            timetableManager = new TimetableManager(context);
        }

        // Загружаем фрагмент
        initCurrentSemester();
        initTimetable();
    }

    private void initCurrentSemester() {
        // Получаем значения
        int currentSemester = userDataManager.getUserCurrentSemester();

        // Устанавливаем значения
        currentSemesterEditText.setText(String.valueOf(currentSemester));
    }

    private void initTimetable() {
        timetable = timetableManager.getTimetable();
        for (int i = 1; i<=8;i++) {
            // Получаем все пары
            String lessonNumber = String.valueOf(i);
            String lessonTime = timetable.get(lessonNumber);

            // Добавляем на страницу
            addTimetableItemView(lessonNumber, lessonTime);
        }
    }

    private void addTimetableItemView(String lessonNumber, String lessonTime) {
        // Надуваем макетё
        LayoutInflater inflater = LayoutInflater.from(context);
        View timetableItem = inflater.inflate(R.layout.timetable_item, null);

        // Находим элементы
        TextView lessonNumberTextView = timetableItem.findViewById(R.id.timetable_item_number_text);
        EditText timeEditText = timetableItem.findViewById(R.id.timetable_item_time_edit_text);

        // Устанавливаем значения
        lessonNumberTextView.setText(String.format("%s пара", lessonNumber));
        timeEditText.setText(lessonTime);

        // Устанавливаем обработчики
        timeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    timeEditText.setError("Нельзя удалить время");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String newLessonTime = s.toString();
                if (!newLessonTime.isEmpty()) {
                    timetable.put(lessonNumber, newLessonTime);
                    timetableManager.setLessonTime(lessonNumber, newLessonTime);
                } else {
                    timeEditText.setError("Введите время пары");
                }
            }
        });

        // Устанавливаем отступы
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginHorizontal = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8, context.getResources().getDisplayMetrics()
        );

        int marginTop = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                context.getResources().getDisplayMetrics()
        );
        params.setMargins(marginHorizontal, marginTop, marginHorizontal, 0);
        timetableItem.setLayoutParams(params);

        // Добавляем в ll
        timetableLinearLayout.addView(timetableItem);
    }

    private void updateCurrentSemester() {
        int newSelectedSemester = Integer.parseInt(currentSemesterEditText.getText().toString());
        if (newSelectedSemester > 0) {
            try {
                userDataManager.setUserCurrentSemester(newSelectedSemester);
            } catch (Error e) {
                Log.i("Editor fragment", e + ": Uncorrect selected semester");
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        updateCurrentSemester();
    }
}