package com.example.myschedule.schedule.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myschedule.MainActivity;
import com.example.myschedule.R;
import com.example.myschedule.schedule.adapters.ScheduleRecyclerViewAdapter;
import com.example.myschedule.schedule.items.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView scheduleRecyclerView;
    private ImageButton addLessonButton;

    private ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter;
    private boolean isActualScheduleDisplayed = false;

    private Context context;
    private MainActivity mainActivity;
    private List<CalendarDay> schedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        swipeRefreshLayout = view.findViewById(R.id.schedule_fragment_swipe_refresh_layout);
        scheduleRecyclerView = view.findViewById(R.id.schedule_fragment_recycler_view);
        addLessonButton = view.findViewById(R.id.schedule_fragment_add_lesson_image_button);

        // Получаем контекст
        context = getContext();
        mainActivity = (MainActivity) requireActivity();

        // Устанавливаем обработчики
        addLessonButton.setOnClickListener(v -> mainActivity.loadAddLessonFragment());

        // Загружаем страницу
        initRecyclerView();
        initScheduleWhenReady();
    }

    private void initRecyclerView() {
        // Адаптер длч RecyclerView
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(context, new ArrayList<>());
        scheduleRecyclerView.setAdapter(scheduleRecyclerViewAdapter);

        // Адаптер для обновления
        swipeRefreshLayout.setOnRefreshListener(this::updateSchedule);
    }

    private void initScheduleWhenReady() {
        if (mainActivity.isScheduleLoaded()) {
            schedule = mainActivity.getSchedule();
            displaySchedule();
        } else {
            isActualScheduleDisplayed = false;
            new Handler(Looper.getMainLooper()).postDelayed(this::initScheduleWhenReady, 500); // Пытемся загрузить расписание каждые 500 мс
        }
    }

    private void updateSchedule() {
        isActualScheduleDisplayed = false;
        initScheduleWhenReady();
        if (isActualScheduleDisplayed) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void displaySchedule() {
        requireActivity().runOnUiThread(() -> {
            scheduleRecyclerViewAdapter.setCalendarDays(schedule);
            isActualScheduleDisplayed = true;
        });
    }
}