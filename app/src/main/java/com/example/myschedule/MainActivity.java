package com.example.myschedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myschedule.actual.fragments.ActualFragment;
import com.example.myschedule.editor.managers.SemesterManager;
import com.example.myschedule.editor.fragments.EditorFragment;
import com.example.myschedule.editor.items.Semester;
import com.example.myschedule.lessons.fragments.LessonsFragment;
import com.example.myschedule.schedule.ScheduleManager;
import com.example.myschedule.schedule.fragments.AddLessonFragment;
import com.example.myschedule.schedule.fragments.ScheduleFragment;
import com.example.myschedule.schedule.items.CalendarDay;
import com.example.myschedule.user.UserDataManager;
import com.example.myschedule.utils.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;

    private Semester currentSemester;
    private List<CalendarDay> schedule;
    private boolean scheduleLoaded = false;

    private SemesterManager semesterManager;
    private ScheduleManager scheduleManager;
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

        // Проверяем, не зашёл ли пользователь первый раз
        userDataManager = new UserDataManager(this);
        if (userDataManager.isUserFirstTime()) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }

        // Находим элементы
        bottomMenu = findViewById(R.id.bottom_menu);

        // Создаём менеджеры
        semesterManager = new SemesterManager(this);
        scheduleManager = new ScheduleManager(this);

        // Загружаем страницу
        loadFragment(new ActualFragment());
        initBottomMenu();
        loadSemesterAndSchedule();
    }

    private void loadSemesterAndSchedule() {
        semesterManager.getSemesterById(userDataManager.getUserCurrentSemester(), new SemesterManager.SemesterCallback() {
            @Override
            public void onSemesterLoaded(Semester semester) {
                currentSemester = semester;
                Log.d("ScheduleFragment", "Loaded semester is: " + currentSemester.getId() + ", end date: " + currentSemester.getEndDate());

                // Загружаем расписание
                loadSchedule();
            }
        });
    }

    private void loadSchedule() {
        long startScheduleDate = getStartScheduleDate();
        scheduleManager.getSchedule(currentSemester.getId(), startScheduleDate, currentSemester.getEndDate(), new ScheduleManager.ScheduleCallback() {
            @Override
            public void onScheduleLoaded(List<CalendarDay> calendarDays) {
                runOnUiThread(() -> {
                    schedule = calendarDays;
                    Log.d("MainActivityLoadingSchedule", "Loaded schedule size: " + schedule.size());
                    scheduleLoaded = true;
                });
            }

            @Override
            public void onError(String error) {
                Log.d("MainActivityLoadingSchedule", "Error on load schedule");
            }
        });
    }

    public void refreshSchedule() {
        if (currentSemester != null) {
            loadSchedule();
        }
    }

    private long getStartScheduleDate() {
        long todayDate = DateUtils.getCurrentDateInMillis();
        if (todayDate > currentSemester.getStartDate() && todayDate <= currentSemester.getEndDate()) {
            return todayDate;
        }
        return currentSemester.getStartDate();
    }

    public Semester getCurrentSemester() {
        return currentSemester;
    }

    public int getCurrentSemesterNumber() {
        return currentSemester.getId();
    }

    public List<CalendarDay> getSchedule() {
        return schedule;
    }

    public CalendarDay getTodaySchedule() {
        return schedule.get(0);
    }

    public boolean isScheduleLoaded() {
        return scheduleLoaded;
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

    public void loadAddLessonFragment() {
        bottomMenu.setVisibility(View.INVISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        fragmentTransaction.replace(R.id.add_lesson_frame_layout, new AddLessonFragment());
        fragmentTransaction.commit();
    }

    public void closeAddLessonFragment() {
        bottomMenu.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment addLessonFragment = fragmentManager.findFragmentById(R.id.add_lesson_frame_layout);
        if (addLessonFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            fragmentTransaction.remove(addLessonFragment);
            fragmentTransaction.commit();

            //Очищаем BackStack
            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduleManager != null) {
            scheduleManager.shutdown();
        }
        if (semesterManager != null) {
            semesterManager.shutdown();
        }
    }
}