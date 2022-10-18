package com.example.deepsleep.tips;

import androidx.lifecycle.ViewModel;

import java.util.List;

public class TipsViewModel extends ViewModel {

    private List<Tip> tips;

    public void setTips(List<Tip> tips) {
        this.tips = tips;
    }

    public List<Tip> getTips() {
        return tips;
    }
}
