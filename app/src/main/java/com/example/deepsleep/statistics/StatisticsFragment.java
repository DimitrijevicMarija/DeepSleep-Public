package com.example.deepsleep.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deepsleep.MainActivity;
import com.example.deepsleep.R;
import com.example.deepsleep.data.DailySleep;
import com.example.deepsleep.data.DailySleepRepository;
import com.example.deepsleep.data.MyDatabase;
import com.example.deepsleep.data.Sleep;
import com.example.deepsleep.data.SleepRepository;
import com.example.deepsleep.databinding.FragmentStatisticsBinding;
import com.example.deepsleep.tips.TipsViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StatisticsFragment extends Fragment {

    private MainActivity mainActivity;
    private NavController navController;
    private FragmentStatisticsBinding binding;
    private SleepViewModel viewModel;
    private SharedPreferences preferences;

    private int userAge;
    private String userName;


    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntriesArrayList;

    private List<DailySleep> dailySleeps = new ArrayList<>();
    private final ArrayList<String> xAxisLabels = new ArrayList<>();
    private final ArrayList<Long> timestampsList = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.");

    private long currentMillis;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        preferences = mainActivity.getPreferences(Context.MODE_PRIVATE);

        userAge = preferences.getInt(getString(R.string.shared_preferences_age_key), -1);
        userName = preferences.getString(getString(R.string.shared_preferences_name_key), "");

        MyDatabase myDatabase = MyDatabase.getInstance(mainActivity);
        SleepRepository sleepRepository = new SleepRepository(myDatabase.sleepDao(), MyDatabase.getInstanceExecutor());
        DailySleepRepository dailySleepRepository = new DailySleepRepository(myDatabase.dailySleepDao(), MyDatabase.getInstanceExecutor());
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new SleepViewModel(sleepRepository, dailySleepRepository);
            }
        };
        viewModel = new ViewModelProvider(mainActivity, factory).get(SleepViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        currentMillis = System.currentTimeMillis();


        showOneSleep(viewModel.getColumn());


        Calendar cal = getTodayCalendar();
        Date end = cal.getTime();

        xAxisLabels.add("");
        timestampsList.add(new Long(0));
        xAxisLabels.add(simpleDateFormat.format(cal.getTime()));
        timestampsList.add(cal.getTime().getTime());
        for (int i = 1; i <=6; i++){
            cal.add(Calendar.DAY_OF_MONTH, -1);
            xAxisLabels.add(1, simpleDateFormat.format(cal.getTime()));
            timestampsList.add(1, cal.getTime().getTime());
        }


        Date start = cal.getTime();
        System.out.println("<<< START END >>> " + start + " -" + end);
        System.out.println("Miliseconds" + end.getTime());

        viewModel.getDailySleepInclusively(start, end).observe(getViewLifecycleOwner(), list -> {
            if (list != null && list.size() > 0){
                long avg = 0;
                for (DailySleep dailySleep: list){
                    avg += dailySleep.getDuration();
                }
                avg /= list.size();
                String avgString = getDurationString(avg);
                binding.average.setText(" " + avgString);

                int percentage = getPercentageOfRecommendedDuration(avg);
                if (percentage > 100) percentage = 100;
                binding.average.setTextColor(getSuccessColor(percentage));


                dailySleeps = list;
                setDataChart();

            }
            else {
                binding.average.setText(getResources().getString(R.string.unknown));
                binding.barChart.setNoDataText(getResources().getString(R.string.no_data_chart_available));
                binding.barChart.setNoDataTextColor(getResources().getColor(R.color.white));
                binding.barChart.invalidate();

            }
        });


        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


    }

    private String getDurationString(long seconds){
        return (seconds/3600) + getString(R.string.hours_label) + " " + ((seconds / 60) % 60) + getString(R.string.minutes_label);
    }

    private int getPercentageOfRecommendedDuration(long seconds){

        long recommended = sleepHoursNeeded();
        // hours -> seconds
        recommended *= 60 * 60;
        return (int) (100 * seconds / recommended);
    }

    private int sleepHoursNeeded(){
        int recommended;
        if (userAge == -1) recommended = 8; // If user age is not defined, 8 hours of sleep is considered recommended.
        else if (userAge <= 2) recommended = 14;
        else if (userAge <= 5) recommended = 12;
        else if (userAge <= 13) recommended = 10;
        else if (userAge <= 17) recommended = 9;
        else if (userAge <= 25) recommended = 8;
        else if (userAge <= 64) recommended = 8;
        else recommended = 7;
        return recommended;
    }

    private int getSuccessColor(int percentage){
        if (percentage <= 15) return getResources().getColor(R.color.dark_red);
        else if (percentage <= 40) return getResources().getColor(R.color.red);
        else if (percentage <= 55) return getResources().getColor(R.color.orange);
        else if (percentage <= 75) return getResources().getColor(R.color.yellow);
        else if (percentage <= 95) return getResources().getColor(R.color.light_green);
        else return getResources().getColor(R.color.green);
    }


    private Calendar getBoundary(int column) {
        //column  1 - 7
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMillis);
        calendar.add(Calendar.DAY_OF_MONTH, column - 7);

        if (calendar.get(Calendar.HOUR_OF_DAY) < 21){
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        System.out.println("<<< BOUNDARY IS >>> " + calendar.getTime());
        binding.sleepingLine.setCalendar(calendar);


        return calendar;
    }

    private Calendar getTodayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMillis);
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 21){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        System.out.println("<<< TODAY IS >>> " + calendar.getTime());

        return calendar;
    }



    private String getMessage(int percentage){
        StringBuilder sb = new StringBuilder();
        if (percentage <= 50) {
            sb.append(getResources().getString(R.string.msg_dangerous));
        }
        else if (percentage <= 75) sb.append(getResources().getString(R.string.msg_medium));
        else sb.append(getResources().getString(R.string.msg_normal));
        if (!userName.equals("")) {
            sb.append(", ").append(userName);
        }
        sb.append("! ");

        if (userAge != -1){
            sb.append(getResources().getString(R.string.msg_average_person_needs_1));
            sb.append(" ").append(userAge);
            sb.append(getResources().getString(R.string.msg_average_person_needs_2));
            sb.append(" ").append(sleepHoursNeeded());
            sb.append(" ").append(getResources().getString(R.string.msg_average_person_needs_3));
        }
        else {
            sb.append(getResources().getString(R.string.add_your_age));
        }

        return sb.toString();
    }


    private void setDataChart(){
        barChart = binding.barChart;

        getBarEntries();

        barDataSet = new BarDataSet(barEntriesArrayList, "");
        barData = new BarData(barDataSet);
        barChart.setData(barData);

        designValues();
        designAxes();

        barChart.getDescription().setEnabled(false);
        //barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateXY(1500, 1500);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                int x = (int) e.getX(); // 1 -7
                System.out.println("SELECTED " + x);
                viewModel.setColumn(x);
                showOneSleep(x);

            }

            @Override
            public void onNothingSelected() {

            }

        });



    }

    private void getBarEntries() {

        barEntriesArrayList = new ArrayList<>();

        int pos = 0;
        for (int i = 1; i <= 7; i++){
            if (pos < dailySleeps.size() && dailySleeps.get(pos).getDate().getTime() == timestampsList.get(i)){
                double v = 1.0 * dailySleeps.get(pos).getDuration() / 60 / 60;
                barEntriesArrayList.add(new BarEntry(i, (float) v));
                pos++;
            }
            else {
                barEntriesArrayList.add(new BarEntry(i, 0));
            }

        }
    }

    private String getDurationStringForBarChart(long seconds){
        if (seconds == 0) return "";
        return (seconds/3600) + getString(R.string.hours_label) + ((seconds / 60) % 60) + getString(R.string.minutes_label);
    }

    private void designAxes(){
        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setTextColor(getResources().getColor(R.color.white));
        axisLeft.setAxisLineColor(getResources().getColor(R.color.white));
        axisLeft.setGranularityEnabled(true);
        axisLeft.setGranularity(1);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = barChart.getAxisRight();
        axisRight.setTextColor(getResources().getColor(R.color.white));
        axisRight.setAxisLineColor(getResources().getColor(R.color.white));
        axisRight.setGranularityEnabled(true);
        axisRight.setGranularity(1);
        axisRight.setAxisMinimum(0);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.white));
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        xAxis.setDrawGridLines(false);


        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabels.get((int) value);
            }
        });

    }

    private void designValues(){
        barDataSet.setValueTextColor(getResources().getColor(R.color.white));
        barDataSet.setValueTextSize(14f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long seconds = (long) (60 * 60 * value);
                return getDurationStringForBarChart(seconds);
            }

        });
        barDataSet.setColor(getResources().getColor(R.color.light_blue));
    }

    private void showOneSleep(int column){
        Calendar calendarBoundary = getBoundary(column);
        long boundary1 = calendarBoundary.getTimeInMillis() / 1000;
        System.out.println("<<< BOUNDARY  1 IS >>> " + calendarBoundary.getTime());

        Calendar calendarBoundary2 = (Calendar) calendarBoundary.clone();
        calendarBoundary2.add(Calendar.DAY_OF_MONTH, 1);
        long boundary2 = calendarBoundary2.getTimeInMillis() / 1000;
        System.out.println("<<< BOUNDARY  2 IS >>> " + calendarBoundary2.getTime());

        viewModel.getAllSleepForDay(boundary1, boundary2).observe(getViewLifecycleOwner(), sleepList -> {
            if (sleepList != null && sleepList.size() > 0){

                long duration = 0;
                for (Sleep sleep: sleepList){
                    duration += sleep.getDuration();
                }


                binding.duration.setText(getDurationString(duration));
                int percentage = getPercentageOfRecommendedDuration(duration);
                if (percentage > 100) percentage = 100;
                int color = getSuccessColor(percentage);

                binding.progress.setProgress(percentage, true);
                binding.progress.setIndicatorColor(color);


                binding.message.setText(getMessage(percentage));

                binding.message.setBackgroundColor(color);
                binding.sleepingLine.setSleepList(sleepList);
                binding.date.setText(xAxisLabels.get(column));

            }
            else {
                binding.message.setText(getResources().getString(R.string.msg_no_sleep));
                binding.message.setTextColor(getResources().getColor(R.color.black));
                binding.message.setBackgroundColor(getResources().getColor(R.color.grey));

                binding.sleepingLine.setSleepList(sleepList);
                binding.duration.setText(getString(R.string.unknown));
                binding.progress.setProgress(0);
                binding.date.setText(xAxisLabels.get(column));
            }


        });
    }
}