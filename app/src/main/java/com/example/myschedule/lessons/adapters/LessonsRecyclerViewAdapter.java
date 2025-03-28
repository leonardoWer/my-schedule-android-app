package com.example.myschedule.lessons.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myschedule.R;
import com.example.myschedule.lessons.items.Discipline;
import com.example.myschedule.widgets.CircleProgressBar;

import java.util.List;

public class LessonsRecyclerViewAdapter extends RecyclerView.Adapter<LessonsRecyclerViewAdapter.ViewHolder> {

    private List<Discipline> disciplines;
    private Context context;

    public LessonsRecyclerViewAdapter(List<Discipline> disciplines, Context context) {
        this.disciplines = disciplines;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discipline_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discipline discipline = disciplines.get(position);
        holder.bind(discipline);
    }

    @Override
    public int getItemCount() {
        return disciplines.size();
    }

    public void setDisciplines(List<Discipline> newDisciplines) {
        this.disciplines = newDisciplines;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView assessmentTypeTextView;
        private TextView ballsCntTextView;
        private CircleProgressBar ballsCntProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.discipline_item_name_text);
            assessmentTypeTextView = itemView.findViewById(R.id.discipline_item_assessment_type_text);
            ballsCntTextView = itemView.findViewById(R.id.discipline_item_balls_cnt_text);
            ballsCntProgressBar = itemView.findViewById(R.id.discipline_item_balls_cnt_progress_bar);
        }

        public void bind(Discipline discipline) {
            // Отображаем название
            nameTextView.setText(discipline.getName());
            assessmentTypeTextView.setText(discipline.getAssessmentType());
            Log.d("LessonsRecyclerView", "assessmentType is " + discipline.getAssessmentType());

            // Отображение баллов
            float ballsCnt = discipline.getBallsCnt();
            ballsCntTextView.setText(String.valueOf((int) ballsCnt));
            ballsCntProgressBar.setMaxProgress(100);
            ballsCntProgressBar.setProgress((int) ballsCnt);
        }
    }
}
