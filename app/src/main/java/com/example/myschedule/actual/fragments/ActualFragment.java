package com.example.myschedule.actual.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myschedule.R;
import com.example.myschedule.user.UserDataManager;


public class ActualFragment extends Fragment {

    private TextView actualText;

    private UserDataManager userDataManager;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем менеджер
        context = getContext();
        if (context != null) {
            userDataManager = new UserDataManager(context);
        }

        // Находим элементы
        actualText = view.findViewById(R.id.actual_fragment_actual_text);

        // Загружаем страницу
        initActualFragment();
    }

    private void initActualFragment() {
        actualText.setText(String.format("Здравствуйте, %s!", userDataManager.getUserName()));
    }
}