package com.example.myschedule.editor.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.myschedule.R;
import com.example.myschedule.user.UserDataManager;


public class EditorFragment extends Fragment {

    private EditText currentSemesterEditText;

    private Context context;
    private UserDataManager userDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        currentSemesterEditText = view.findViewById(R.id.editor_current_semester_edit_text);

        // Создаём менеджеры
        context = getContext();
        if (context != null) {
            userDataManager = new UserDataManager(context);
        }

        // Загружаем фрагмент
        initEditorFragment();
    }

    private void initEditorFragment() {
        // Получаем значения
        String currentSemester = userDataManager.getUserCurrentSemester();

        // Устанавливаем значения
        currentSemesterEditText.setText(currentSemester);

        // Устанавливаем обработчики

    }

    private void updateCurrentSemester() {
        userDataManager.setUserCurrentSemester(currentSemesterEditText.getText().toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        updateCurrentSemester();
    }
}