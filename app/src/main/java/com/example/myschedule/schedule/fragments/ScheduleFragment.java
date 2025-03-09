package com.example.myschedule.schedule.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myschedule.R;
import com.example.myschedule.editor.SemesterManager;
import com.example.myschedule.editor.items.Semester;
import com.example.myschedule.schedule.ScheduleManager;
import com.example.myschedule.schedule.adapters.ScheduleRecyclerViewAdapter;
import com.example.myschedule.schedule.items.CalendarDay;
import com.example.myschedule.user.UserDataManager;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private RecyclerView scheduleRecyclerView;

    private ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter;

    private Context context;
    private UserDataManager userDataManager;
    private ScheduleManager scheduleManager;
    private SemesterManager semesterManager;

    private Semester currentSemester;
    private List<CalendarDay> calendarDays = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы
        scheduleRecyclerView = view.findViewById(R.id.schedule_fragment_recycler_view);

        // Создаём менеджеры
        context = getContext();
        if (context != null) {
            userDataManager = new UserDataManager(context);
            scheduleManager = new ScheduleManager(context);
            semesterManager = new SemesterManager(context);
        }

        // Загружаем страницу
        initRecyclerView();
        initSchedule();
    }

    private void initRecyclerView() {
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(calendarDays);
        scheduleRecyclerView.setAdapter(scheduleRecyclerViewAdapter);
    }

    private void initSchedule() {
        // Получаем текущий семестр
        int currentSemesterId = userDataManager.getUserCurrentSemester();

        semesterManager.getSemesterById(currentSemesterId, new SemesterManager.SemesterCallback() {
            @Override
            public void onSemesterLoaded(Semester semester) {
                currentSemester = semester;
                Log.d("ScheduleFragment", "Loaded semester is: " + currentSemester.getId() + ", end date: " + currentSemester.getEndDate());

                // Получаем расписание
                calendarDays = scheduleManager.getSchedule(currentSemester.getId(), currentSemester.getStartDate(), currentSemester.getEndDate());

                // Обновляем адаптер в фоновом потоке
                requireActivity().runOnUiThread(() -> {
                    scheduleRecyclerViewAdapter.setCalendarDays(calendarDays);
                    scheduleRecyclerViewAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        semesterManager.shutdown();
    }
}