package com.example.deepsleep.personal;

import androidx.lifecycle.ViewModel;

public class PersonalViewModel extends ViewModel {

    private String name = null;
    private int age = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
