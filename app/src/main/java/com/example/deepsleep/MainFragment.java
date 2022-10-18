package com.example.deepsleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deepsleep.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private MainActivity mainActivity;
    private NavController navController;
    private FragmentMainBinding binding;
    private SharedPreferences preferences;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        preferences = mainActivity.getPreferences(Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);

        binding.floatingActionButtonTips.setOnClickListener(view -> {
            NavDirections navDirections = MainFragmentDirections.actionMainFragmentToTipsFragment();
            navController.navigate(navDirections);
        });
        binding.floatingActionButtonAlarm.setOnClickListener(view -> {
            NavDirections navDirections = MainFragmentDirections.actionMainFragmentToAlarmFragment();
            navController.navigate(navDirections);
        });
        binding.floatingActionButtonStatistics.setOnClickListener(view -> {
            NavDirections navDirections = MainFragmentDirections.actionMainFragmentToStatisticsFragment();
            navController.navigate(navDirections);
        });
        binding.personalInfo.setOnClickListener(view -> {
            NavDirections navDirections = MainFragmentDirections.actionMainFragmentToPersonalFragment();
            navController.navigate(navDirections);
        });

        String name = preferences.getString(getString(R.string.shared_preferences_name_key), "");
        if (!name.equals("")){
            binding.hello.setText(getString(R.string.hello_string) + ", " + name + "!");
        }
        else {
            binding.hello.setText(getString(R.string.hello_string) + "!");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}