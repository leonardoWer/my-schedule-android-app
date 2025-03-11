package com.example.myschedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myschedule.editor.managers.SemesterManager;
import com.example.myschedule.user.UserDataManager;

public class StartActivity extends AppCompatActivity {

    private EditText nameEditText, currentSemesterEditText;
    private Button startButton;

    private UserDataManager userDataManager;
    private SemesterManager semesterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Находим элементы
        nameEditText = findViewById(R.id.start_activity_name_edit_text);
        currentSemesterEditText = findViewById(R.id.start_activity_current_semester_edit_text);
        startButton = findViewById(R.id.start_activity_start_button);

        // Создаём менеджер
        userDataManager = new UserDataManager(this);
        semesterManager = new SemesterManager(this);

        // Устанавливаем обработчики
        startButton.setOnClickListener(v -> saveAndStart());
    }

    private void saveAndStart() {
        String userName = nameEditText.getText().toString();
        String currentSemester = currentSemesterEditText.getText().toString();
        Log.d("StartActivity", "User: " + userName + " " + currentSemester);

        if (checkNotErrorsInData(userName, currentSemester)) {
            // Сохраняем параметры
            userDataManager.setUserName(userName);
            userDataManager.setUserCurrentSemester(Integer.parseInt(currentSemester));

            Toast.makeText(this, "Приятно познакомиться, " + userName, Toast.LENGTH_SHORT).show();

            // Генерируем семестры в фоновом потоке
            semesterManager.initializeSemesters(Integer.parseInt(currentSemester));

            // Загружаем новую страницу
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkNotErrorsInData(String name, String semester) {
        boolean haveErrors = false;
        if (name.isEmpty()) {
            nameEditText.setError("Введите имя");
            haveErrors = true;
        }
        if (semester.isEmpty()) {
            currentSemesterEditText.setError("Введите текущий семестр");
            haveErrors = true;
        }
        if (Integer.parseInt(semester) <= 0 || Integer.parseInt(semester) > 8) {
            currentSemesterEditText.setError(semester + " - это число, а не семестр");
            haveErrors = true;
        }
        return !haveErrors;
    }
}