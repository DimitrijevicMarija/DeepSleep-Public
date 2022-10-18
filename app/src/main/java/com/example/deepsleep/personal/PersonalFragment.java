package com.example.deepsleep.personal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deepsleep.MainActivity;
import com.example.deepsleep.R;
import com.example.deepsleep.databinding.FragmentPersonalBinding;
import com.example.deepsleep.tips.TipsViewModel;

public class PersonalFragment extends Fragment {

    private MainActivity mainActivity;
    private NavController navController;
    private FragmentPersonalBinding binding;
    private PersonalViewModel viewModel;
    private SharedPreferences preferences;

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        viewModel = new ViewModelProvider(mainActivity).get(PersonalViewModel.class);

        System.out.println("ON CREATE");
        preferences = mainActivity.getPreferences(Context.MODE_PRIVATE);

        int age = preferences.getInt(getString(R.string.shared_preferences_age_key), 20);
        String name = preferences.getString(getString(R.string.shared_preferences_name_key), "");

        if (viewModel.getAge() == -1) viewModel.setAge(age);
        if (viewModel.getName() == null) viewModel.setName(name);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("ON CREATE VIEW");

        binding = FragmentPersonalBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });



        binding.name.setText(viewModel.getName());

        binding.agePicker.setMinValue(1);
        binding.agePicker.setMaxValue(130);
        binding.agePicker.setValue(viewModel.getAge());
        binding.agePicker.setWrapSelectorWheel(false);

        binding.change.setOnClickListener(view -> {
            SharedPreferences.Editor editor = preferences.edit();
            System.out.println(binding.agePicker.getValue() + " " +  binding.name.getText().toString());
            editor.putInt(getString(R.string.shared_preferences_age_key), binding.agePicker.getValue());
            editor.putString(getString(R.string.shared_preferences_name_key), binding.name.getText().toString());
            editor.apply();

            showToast(getString(R.string.msg_personal_info_changed));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.setName(binding.name.getText().toString());
        viewModel.setAge(binding.agePicker.getValue());
    }
}