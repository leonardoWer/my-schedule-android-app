package com.example.myschedule.schedule.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.schedule.ScheduleManager;

public class AddLessonFragment extends Fragment {

    private ImageButton goBackButton;
    private AutoCompleteTextView lessonNameText, assessmentTypeText;
    private TextView dateTextView;
    private ImageButton datePickerButton;
    private Spinner timePickerSpinner;
    private AutoCompleteTextView teacherText, placeText;

    private Context context;
    private MainActivity mainActivity;
    private ScheduleManager scheduleManager;

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
        timePickerSpinner = view.findViewById(R.id.add_lesson_lesson_start_time_type_spinner);
        teacherText = view.findViewById(R.id.add_lesson_teacher_auto_text);
        placeText = view.findViewById(R.id.add_lesson_place_auto_text);

        // Объявляем контекст
        context = requireContext();
        mainActivity = (MainActivity) requireActivity();

        // Устанавливаем обработчики
        goBackButton.setOnClickListener(v -> mainActivity.closeAddLessonFragment());
    }

    //Метод для закрытия
    public void closeFragment(){

    }

}