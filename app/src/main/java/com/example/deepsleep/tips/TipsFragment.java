package com.example.deepsleep.tips;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.deepsleep.MainActivity;
import com.example.deepsleep.R;
import com.example.deepsleep.databinding.FragmentMainBinding;
import com.example.deepsleep.databinding.FragmentTipsBinding;

import java.util.List;


public class TipsFragment extends Fragment {

    private MainActivity mainActivity;
    private NavController navController;
    private FragmentTipsBinding binding;
    private TipsViewModel viewModel;
    private LinearLayoutManager linearLayoutManager;


    public TipsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();

        viewModel = new ViewModelProvider(mainActivity).get(TipsViewModel.class);

        List<Tip> tips = Tip.createFromResources(getResources());
        viewModel.setTips(tips);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTipsBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        TipsAdapter tipsAdapter = new TipsAdapter(viewModel);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(tipsAdapter);

        linearLayoutManager = new LinearLayoutManager(mainActivity);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);


        binding.recyclerView.setLayoutManager(linearLayoutManager);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerView);

        binding.arrowRight.setClickable(true);
        binding.arrowLeft.setClickable(true);
        binding.arrowRight.setVisibility(View.INVISIBLE);
        binding.arrowLeft.setVisibility(View.INVISIBLE);


        binding.arrowRight.setOnClickListener(view ->  {
                int newPosition = linearLayoutManager.findLastVisibleItemPosition() + 1;

                if (newPosition < viewModel.getTips().size())
                    binding.recyclerView.smoothScrollToPosition(newPosition);
        });


        binding.arrowLeft.setOnClickListener(view ->  {
            int newPosition = linearLayoutManager.findLastVisibleItemPosition() - 1;

            if (newPosition >= 0)
                binding.recyclerView.smoothScrollToPosition(newPosition);

        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                changeVisibilityOfArrows();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        binding.recyclerView.postDelayed(this::changeVisibilityOfArrows, 100);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

    }

    public void changeVisibilityOfArrows(){
        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
            binding.arrowLeft.setVisibility(View.INVISIBLE);
        else
            binding.arrowLeft.setVisibility(View.VISIBLE);


        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == viewModel.getTips().size() - 1)
            binding.arrowRight.setVisibility(View.INVISIBLE);
        else
            binding.arrowRight.setVisibility(View.VISIBLE);
    }


}