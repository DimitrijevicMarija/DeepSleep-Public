package com.example.deepsleep.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.deepsleep.MainActivity;
import com.example.deepsleep.R;
import com.example.deepsleep.data.Alarm;
import com.example.deepsleep.data.AlarmRepository;
import com.example.deepsleep.data.MyDatabase;
import com.example.deepsleep.databinding.FragmentAlarmBinding;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.AlarmManager.AlarmClockInfo;


public class AlarmFragment extends Fragment {

    private MainActivity mainActivity;
    private NavController navController;
    private FragmentAlarmBinding binding;
    private AlarmViewModel viewModel;

    private AlarmManager alarmManager;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");


    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();


        alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);


        MyDatabase myDatabase = MyDatabase.getInstance(mainActivity);
        AlarmRepository alarmRepository = new AlarmRepository(myDatabase.alarmDao(), MyDatabase.getInstanceExecutor());
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
        binding = FragmentAlarmBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        binding.toolbar.setOnMenuItemClickListener(menuItem -> {

            if (menuItem.getItemId() == R.id.alarm_list){
                NavDirections navDirections = AlarmFragmentDirections.actionAlarmFragmentToAlarmListFragment();
                navController.navigate(navDirections);
            }
            return true;
        });



        binding.timePicker.setIs24HourView(true);
        if (viewModel.getSavedMinute() != -1 && viewModel.getSavedHour()!= -1){
            binding.timePicker.setHour(viewModel.getSavedHour());
            binding.timePicker.setMinute(viewModel.getSavedMinute());
        }
        binding.vibration.setChecked(viewModel.isVibrationChecked());
        binding.morningTest.setChecked(viewModel.isMorningTestChecked());
        binding.volumeSlider.setValue(viewModel.getVolumeSliderValue());


        binding.volumeSlider.setLabelFormatter(value -> (int)value + "%");
        binding.timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            viewModel.setHourAndMinute(hourOfDay, minute);
        });
        binding.vibration.setOnClickListener(view -> {
            viewModel.setVibrationChecked(binding.vibration.isChecked());
        });
        binding.morningTest.setOnClickListener(view -> {
            viewModel.setMorningTestChecked(binding.morningTest.isChecked());
        });
        binding.volumeSlider.addOnChangeListener((slider, value, fromUser) -> {
            Log.d("MIKA" ,"CHANGING VOLUME " + value);
            viewModel.setVolumeSliderValue(value);
        });


        binding.set.setOnClickListener(view -> {

            boolean vibrationChecked = binding.vibration.isChecked();
            boolean morningTestChecked = binding.morningTest.isChecked();
            float volumeSliderValue = binding.volumeSlider.getValue();
            int hour = binding.timePicker.getHour();
            int minute = binding.timePicker.getMinute();


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
            System.out.println("FORMATTED DATE" + sdf.format(calendar.getTime()));

            String timeFormatted = timeFormatter.format(calendar.getTime());
            Alarm alarm = new Alarm(0, calendar.getTimeInMillis(), timeFormatted, hour, minute, true,
                    volumeSliderValue, morningTestChecked, vibrationChecked);
            long id = viewModel.insert(alarm);


            Intent intent = new Intent(mainActivity, AlarmReceiver.class);
            intent.putExtra(getString(R.string.vibration_name_extra), vibrationChecked);
            intent.putExtra(getString(R.string.morning_test_extra), morningTestChecked);
            intent.putExtra(getString(R.string.volume_name_extra), volumeSliderValue);
            intent.putExtra(getString(R.string.id_alarm_extra), id);

            int requestCode = (int) id;
            Log.d("MIKA", "--INSERTING ALARM REQ QODE -- " + requestCode);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(mainActivity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            AlarmClockInfo alarmClockInfo = new AlarmClockInfo(calendar.getTimeInMillis(), null);
            alarmManager.setAlarmClock(alarmClockInfo, alarmIntent);


            String msg = getString(R.string.alarm_set_message) + " " + timeFormatted;
            showToast(msg);


            viewModel.setHourAndMinute(-1, -1);
            viewModel.setVolumeSliderValue(50);
            viewModel.setVibrationChecked(false);
            viewModel.setMorningTestChecked(false);
            navController.navigateUp();


        });




        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    public void showToast(String msg){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.simple_toast,
                (ViewGroup) mainActivity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(mainActivity);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


}