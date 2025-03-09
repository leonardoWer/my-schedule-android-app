package com.example.myschedule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myschedule.actual.fragments.ActualFragment;
import com.example.myschedule.editor.fragments.EditorFragment;
import com.example.myschedule.lessons.fragments.LessonsFragment;
import com.example.myschedule.schedule.fragments.ScheduleFragment;
import com.example.myschedule.user.UserDataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;

    private UserDataManager userDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        getWindow().setStatusBarColor(Color.WHITE);

        // Проверяем, не зашёл ли пользователь первый раз
        userDataManager = new UserDataManager(this);
        if (userDataManager.isUserFirstTime()) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }

        // Находим элементы
        bottomMenu = findViewById(R.id.bottom_menu);

        // Загружаем страницу
        loadFragment(new ActualFragment());
        initBottomMenu();
    }

    private void initBottomMenu() {
        // Устанавливаем страницу по умолчанию
        bottomMenu.setSelectedItemId(R.id.nav_actual);

        // Устанавливаем обработчики
        bottomMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_editor) {
                loadFragment(new EditorFragment());
                return true;
            } else if (itemId == R.id.nav_lessons) {
                loadFragment(new LessonsFragment());
                return true;
            } else if (itemId == R.id.nav_schedule) {
                loadFragment(new ScheduleFragment());
                return true;
            } else if (itemId == R.id.nav_actual) {
                loadFragment(new ActualFragment());
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}