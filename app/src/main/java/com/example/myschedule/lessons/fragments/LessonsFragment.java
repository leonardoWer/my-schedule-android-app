package com.example.myschedule.lessons.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myschedule.R;
import com.example.myschedule.lessons.LessonsManager;
import com.example.myschedule.lessons.adapters.LessonsRecyclerViewAdapter;
import com.example.myschedule.lessons.items.Discipline;
import com.example.myschedule.user.UserDataManager;
import com.example.myschedule.widgets.CircleProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LessonsFragment extends Fragment {

    private RecyclerView lessonsRecyclerView;
    private Button addDisciplineButton;

    private Context context;
    private UserDataManager userDataManager;
    private LessonsManager lessonsManager;

    private int currentSemester;
    private List<Discipline> disciplines;
    private LessonsRecyclerViewAdapter lessonsRecyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим объекты
        lessonsRecyclerView = view.findViewById(R.id.lesson_fragment_recycler_view);
        addDisciplineButton = view.findViewById(R.id.lesson_fragment_add_lesson_button);

        // Получаем менеджеры
        context = getContext();
        if (context != null) {
            userDataManager = new UserDataManager(context);
            lessonsManager = new LessonsManager(context);
        }

        // Загружаем страницу
        initLessonsFragment();

    }

    private void initLessonsFragment() {
        // Получаем данные
        currentSemester = userDataManager.getUserCurrentSemester();
        disciplines = lessonsManager.getDisciplinesOnSemester(currentSemester);
        if (disciplines == null) {
            Log.d("LessonsFragment", "Disciplines = null exception!");
            disciplines = new ArrayList<>();
        }

        // Загружаем страницу
        initRecyclerView();

        // Устанавливаем обработчики
        addDisciplineButton.setOnClickListener(v -> showAddDisciplineDialog());
    }

    private void initRecyclerView() {
        // Создаём адаптер
        lessonsRecyclerViewAdapter = new LessonsRecyclerViewAdapter(disciplines, context);

        // Устанавливаем адаптер
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        lessonsRecyclerView.setAdapter(lessonsRecyclerViewAdapter);
    }

    private void showAddDisciplineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Надуваем макет диалога
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_discipline, null);
        builder.setView(view);

        // Находим элементы
        EditText nameEditText = view.findViewById(R.id.dialog_add_discipline_name_edit_text);
        AutoCompleteTextView assessmentTypeAutoText = view.findViewById(R.id.dialog_add_discipline_assessment_type_edit_text);
        CircleProgressBar circleProgressBar = view.findViewById(R.id.dialog_add_discipline_progress_bar);
        EditText ballsCntEditText = view.findViewById(R.id.dialog_add_discipline_balls_cnt_text);
        Button addDisciplineButton = view.findViewById(R.id.dialog_add_discipline_save_button);
        ImageButton closeImageButton = view.findViewById(R.id.dialog_add_discipline_close_image_button);

        // Устанавливаем адаптер
        String[] assessmentTypes = {"Экзамен", "Зачёт", "Дифференцированный зачёт"};
        assessmentTypeAutoText.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, assessmentTypes));

        // Устанавливаем максимум для прогресс бара
        circleProgressBar.setMaxProgress(100);

        // Создаём диалог
        AlertDialog dialog = builder.create();

        // Устанавливаем обработчики
        ballsCntEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float ballsCnt = Float.parseFloat(ballsCntEditText.getText().toString().trim());
                    circleProgressBar.setProgress((int) ballsCnt);
                } catch (NumberFormatException e) {
                    circleProgressBar.setProgress(0);
                }
            }
        });

        // Добавляем кнопки
        addDisciplineButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String assessmentType = assessmentTypeAutoText.getText().toString().trim();
            float ballsCnt;
            try {
                ballsCnt = Float.parseFloat(ballsCntEditText.getText().toString().trim());
            } catch (NumberFormatException e) {
                ballsCnt = 0;
            }

            // Проверки
            if (name.isEmpty()) {
                nameEditText.setError("Введите название предмета");
                return;
            }
            if (assessmentType.isEmpty()) {
                assessmentTypeAutoText.setError("Введите тип оценки");
                return;
            }
            if (ballsCnt < 0) {
                ballsCnt = 0;
            } else if (ballsCnt > 100) {
                ballsCnt = 100;
            }

            // Создаём новый предмет
            Discipline newDiscipline = new Discipline(name, assessmentType, ballsCnt);

            // Добавляем его в список, сохраняем и обновляем UI
            disciplines.add(newDiscipline);
            lessonsManager.setDisciplinesOnSemester(currentSemester, disciplines);
            lessonsRecyclerViewAdapter.setDisciplines(disciplines);

            dialog.dismiss();
        });

        closeImageButton.setOnClickListener(v -> dialog.dismiss());

        // Создаем и показываем диалог
        dialog.show();
    }
}