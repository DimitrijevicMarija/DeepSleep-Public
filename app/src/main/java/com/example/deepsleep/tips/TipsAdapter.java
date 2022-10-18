package com.example.deepsleep.tips;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepsleep.databinding.ViewHolderTipBinding;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipViewHolder>{

    private final TipsViewModel viewModel;

    public TipsAdapter(TipsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderTipBinding viewHolderTipBinding = ViewHolderTipBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new TipViewHolder(viewHolderTipBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        Tip tip = viewModel.getTips().get(position);

        holder.binding.tipHeading.setText(tip.getHeading());
        holder.binding.tipDescription.setText(tip.getDescription());
    }

    @Override
    public int getItemCount() {
        return viewModel.getTips().size();
    }

    public class TipViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderTipBinding binding;

        public TipViewHolder(@NonNull ViewHolderTipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
