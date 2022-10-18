package com.example.deepsleep.alarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepsleep.R;
import com.example.deepsleep.data.Alarm;
import com.example.deepsleep.databinding.ViewHolderAlarmBinding;

import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{
    private List<Alarm> alarmList = new ArrayList<>();
    private final AlarmListFragment fragment;

    public AlarmAdapter(AlarmListFragment fragment) {
        this.fragment = fragment;
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderAlarmBinding viewHolderAlarmBinding = ViewHolderAlarmBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new AlarmAdapter.AlarmViewHolder(viewHolderAlarmBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {

        long now = System.currentTimeMillis();

        Alarm alarm = alarmList.get(position);
        boolean condition = alarm.isValid() && alarm.getMillis() > now;

        holder.binding.time.setText(alarm.getTime());
        holder.binding.alarmSwitch.setChecked(condition);
        holder.binding.delete.setVisibility(condition ?  View.INVISIBLE : View.VISIBLE);

        holder.binding.alarmSwitch.setOnClickListener(view -> {
            boolean isChecked = holder.binding.alarmSwitch.isChecked();
            Log.d("MIKA", "CHANGED STATE " + isChecked);
            if (isChecked) fragment.setAlarm(alarm);
            else fragment.cancelAlarm(alarm);
        });
        /*holder.binding.alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("MIKA", "CHANGED STATE " + isChecked);
            if (isChecked) fragment.setAlarm(alarm);
            else fragment.cancelAlarm(alarm);
        });*/
        if (! condition){
            holder.binding.getRoot().setBackground(fragment.getResources().getDrawable(R.drawable.layout_bg_transparent));
            holder.binding.time.setTextColor(fragment.getResources().getColor(R.color.borders_transparent));
            holder.binding.delete.setClickable(true);
            holder.binding.delete.setOnClickListener(view -> {
                fragment.deleteAlarm(alarm);
            });
        }
        else {
            holder.binding.getRoot().setBackground(fragment.getResources().getDrawable(R.drawable.layout_bg));
            holder.binding.time.setTextColor(fragment.getResources().getColor(R.color.primaryColor));
        }


    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderAlarmBinding binding;

        public AlarmViewHolder(@NonNull ViewHolderAlarmBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
