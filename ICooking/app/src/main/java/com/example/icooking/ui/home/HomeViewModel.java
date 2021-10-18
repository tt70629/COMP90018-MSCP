package com.example.icooking.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private String name;
    private String date;

    private MutableLiveData<String> mText;


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("homeViewModel");
    }
    public void setname(String name) {
        this.name = name;

    }
    public String getname(String name) {
        return name;

    }
    public void setDate(String date) {
        this.date = date;

    }
    public String getDate(String date) {
        return date;

    }
    @Override
    public String toString() {
        return "HomeViewModel{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", mText=" + mText +
                '}';
    }
}