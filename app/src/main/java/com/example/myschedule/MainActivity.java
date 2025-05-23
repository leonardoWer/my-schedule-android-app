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
    private FragmentManager fragmentManager;

    private SemesterManager semesterManager;
    private ScheduleManager scheduleManager;
    private UserDataManager userDataManager;

    private Semester currentSemester;
    private List<CalendarDay> schedule;
    private boolean scheduleLoaded = false;


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
        checkUserFirstTime();

        // Находим элементы
        bottomMenu = findViewById(R.id.bottom_menu);

        // Загружаем страницу
        initMainActivity();
    }

    private void checkUserFirstTime() {
        userDataManager = new UserDataManager(this);
        if (userDataManager.isUserFirstTime()) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }
    }

    private void initMainActivity() {
        initManagers();
        loadFragment(new ActualFragment());
        initBottomMenu();
        loadSemesterAndSchedule();
    }

    private void initManagers() {
        // Создаём менеджеры
        fragmentManager = getSupportFragmentManager();
        semesterManager = new SemesterManager(this);
        scheduleManager = new ScheduleManager(this);
    }

    private void loadSemesterAndSchedule() {
        semesterManager.getSemesterById(userDataManager.getUserCurrentSemester(), semester -> {
            currentSemester = semester;
            Log.i("MainActivity(ScheduleFragment)", "Loaded semester is: " + currentSemester.getId() + "; dates: " + DateUtils.formatLongToString(currentSemester.getStartDate()) + ":" + DateUtils.formatLongToString(currentSemester.getEndDate()));

            // Загружаем расписание
            loadSchedule();
        });
    }

    private void loadSchedule() {
        scheduleLoaded = false;

        long startScheduleDate = getStartScheduleDate();
        scheduleManager.getSchedule(currentSemester.getId(), startScheduleDate, currentSemester.getEndDate(), new ScheduleManager.ScheduleCallback() {
            @Override
            public void onScheduleLoaded(List<CalendarDay> calendarDays) {
                runOnUiThread(() -> {
                    schedule = calendarDays;
                    Log.d("MainActivity", "Schedule successfully loaded!\nDisplaying schedule days cnt: " + schedule.size());
                    scheduleLoaded = true;
                });
            }

            @Override
            public void onError(String error) {
                Log.d("MainActivityLoadingSchedule", "Error on load schedule");
            }
        });
    }

    private long getStartScheduleDate() {
        long todayDate = DateUtils.getCurrentDateInMillis();
        if (todayDate > currentSemester.getStartDate() && todayDate <= currentSemester.getEndDate()) {
            return todayDate;
        }
        return currentSemester.getStartDate();
    }

    // Методы для других страниц
    public List<CalendarDay> getSchedule() {
        return schedule;
    }

    public CalendarDay getTodaySchedule() {
        return schedule.get(0);
    }

    public void refreshSchedule() {
        Log.i("MainActivity", "Start refreshing schedule");
        if (currentSemester != null) {
            loadSchedule();
        } else {
            Log.w("MainActivity", "Error with refreshing schedule: current semester = 0");
        }
    }

    public boolean isScheduleLoaded() {
        return scheduleLoaded;
    }

    public Semester getCurrentSemester() {
        return currentSemester;
    }

    public int getCurrentSemesterNumber() {
        return currentSemester.getId();
    }

    public long getCurrentSemesterStartDate() {
        return currentSemester.getStartDate();
    }

    public long getCurrentSemesterEndDate() {
        return currentSemester.getEndDate();
    }

    // Меню

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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void loadAddLessonFragment() {
        bottomMenu.setVisibility(View.INVISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        fragmentTransaction.replace(R.id.add_lesson_frame_layout, new AddLessonFragment());
        fragmentTransaction.commit();
    }
    public void closeAddLessonFragment() {
        bottomMenu.setVisibility(View.VISIBLE);
        Fragment addLessonFragment = fragmentManager.findFragmentById(R.id.add_lesson_frame_layout);
        if (addLessonFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            fragmentTransaction.remove(addLessonFragment);
            fragmentTransaction.commit();

            // Очищаем BackStack
            fragmentManager.popBackStack();
        }

        // Обновляем расписание в фрагменте
        updateScheduleInScheduleFragment();
    }
    public void updateScheduleInScheduleFragment() {
        try {
            ScheduleFragment scheduleFragment = (ScheduleFragment) fragmentManager.findFragmentById(R.id.main_frame_layout);
            if (scheduleFragment != null) {
                scheduleFragment.updateSchedule();
            }
        } catch (Error e) {
            Log.d("MainPage", "Error updating Schedule Fragment: " + e);
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