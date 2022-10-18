package com.example.deepsleep.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deepsleep.MainActivity;
import com.example.deepsleep.R;
import com.example.deepsleep.data.Alarm;
import com.example.deepsleep.data.AlarmRepository;
import com.example.deepsleep.data.MyDatabase;
import com.example.deepsleep.databinding.FragmentAlarmListBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmListFragment extends Fragment {

    private MainActivity mainActivity;
    private NavController navController;
    private FragmentAlarmListBinding binding;
    private AlarmViewModel viewModel;

    private AlarmManager alarmManager;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public AlarmListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();

        alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);

        MyDatabase myDatabase = MyDatabase.getInstance(mainActivity);
        AlarmRepository alarmRepository = new AlarmRepository(myDatabase.alarmDao(),  MyDatabase.getInstanceExecutor());
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AlarmViewModel(alarmRepository);
            }
        };
        viewModel = new ViewModelProvider(mainActivity, factory).get(AlarmViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlarmListBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });


        AlarmAdapter alarmAdapter = new AlarmAdapter(this);
        viewModel.getAlarmList().observe(getViewLifecycleOwner(), alarmAdapter::setAlarmList);
        binding.recyclerView.setAdapter(alarmAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    public void cancelAlarm(Alarm alarm){


        Intent intent = new Intent(mainActivity, AlarmReceiver.class);
        intent.putExtra(getString(R.string.vibration_name_extra), alarm.isVibration());
        intent.putExtra(getString(R.string.morning_test_extra), alarm.isMorningTest());
        intent.putExtra(getString(R.string.volume_name_extra), alarm.getVolume());
        intent.putExtra(getString(R.string.id_alarm_extra), alarm.getId());

        int requestCode = (int) alarm.getId();
        Log.d("MIKA", "-- CANCELLING ALARM REQ QODE -- " + requestCode);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mainActivity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(alarmIntent);

        alarm.setValid(false);
        viewModel.update(alarm);

    }

    public void setAlarm(Alarm alarm){

        int hour = alarm.getHour();
        int minute = alarm.getMinute();

        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if( now > calendar.getTimeInMillis()){
            calendar.add( Calendar.DAY_OF_MONTH, 1 );
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
        System.out.println("FORMATTED DATE" + sdf.format(calendar.getTime()));

        Intent intent = new Intent(mainActivity, AlarmReceiver.class);
        intent.putExtra(getString(R.string.vibration_name_extra), alarm.isVibration());
        intent.putExtra(getString(R.string.morning_test_extra), alarm.isMorningTest());
        intent.putExtra(getString(R.string.volume_name_extra), alarm.getVolume());
        intent.putExtra(getString(R.string.id_alarm_extra), alarm.getId());

        int requestCode = (int) alarm.getId();
        Log.d("MIKA", "--SETTING ALARM REQ CODE -- " + requestCode);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mainActivity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null);
        alarmManager.setAlarmClock(alarmClockInfo, alarmIntent);

        alarm.setValid(true);
        alarm.setMillis(calendar.getTimeInMillis());

        viewModel.update(alarm);
    }

    public void deleteAlarm(Alarm alarm){
        Log.d("MIKA", "-- DELETING ALARM -- " + alarm.getId());
        viewModel.delete(alarm);

    }


}