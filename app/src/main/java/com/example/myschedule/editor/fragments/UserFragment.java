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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.user.UserDataManager;

public class UserFragment extends Fragment {

    private ImageButton goBackImageButton;
    private ImageView userPhotoImageView;
    private EditText nameEditText;

    private Context context;
    private MainActivity mainActivity;
    UserDataManager userDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        goBackImageButton = view.findViewById(R.id.go_back_button);
        userPhotoImageView = view.findViewById(R.id.user_fragment_user_logo);
        nameEditText = view.findViewById(R.id.user_fragment_name_edit_text);

        // Находим контекст
        context = getContext();
        mainActivity = (MainActivity) requireActivity();
        userDataManager = new UserDataManager(context);

        // Устанавливаем обработчики
        goBackImageButton.setOnClickListener(v -> mainActivity.closeUserFragment());

        // Заполняем страницу
        nameEditText.setText(userDataManager.getUserName());
    }

    @Override
    public void onPause() {
        super.onPause();
        updateUserName();
    }

    private void updateUserName() {
        String newUserName = nameEditText.getText().toString();
        if (newUserName.isEmpty()) {
            nameEditText.setError("Имя не может быть пустым");
        } else {
            userDataManager.setUserName(newUserName);
        }

    }
}