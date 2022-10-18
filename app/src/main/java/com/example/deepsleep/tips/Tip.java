package com.example.deepsleep.tips;

import android.content.res.Resources;

import com.example.deepsleep.R;

import java.util.ArrayList;
import java.util.List;

public class Tip {
    private final String heading;
    private final String description;

    public Tip(String heading, String description) {
        this.heading = heading;
        this.description = description;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public static List<Tip> createFromResources(Resources resources){

        List<Tip> tips = new ArrayList<>();

        String[] headings = resources.getStringArray(R.array.tip_heading);
        String[] descriptions = resources.getStringArray(R.array.tip_description);

        for (int i = 0; i < headings.length; i++){
            Tip tip = new Tip(headings[i], descriptions[i]);
            tips.add(tip);
        }

        return tips;

    }
}
