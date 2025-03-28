package com.example.myschedule.schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myschedule.R;
import com.example.myschedule.editor.items.TimetableSpinnerItem;

import java.util.List;

public class TimetableSpinnerAdapter extends ArrayAdapter<TimetableSpinnerItem> {

    private Context context;
    private List<TimetableSpinnerItem> timetable;

    public TimetableSpinnerAdapter(Context context, List<TimetableSpinnerItem> timetable) {
        super(context, R.layout.timetable_spinner_item, timetable);
        this.context = context;
        this.timetable = timetable;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.timetable_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.timetable_spinner_text);
        TimetableSpinnerItem currentItem = getItem(position);

        if (currentItem != null) {
            textView.setText(currentItem.toString());
        }

        return convertView;
    }


}
