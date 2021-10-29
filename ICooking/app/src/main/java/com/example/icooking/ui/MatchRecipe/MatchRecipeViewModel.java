package com.example.icooking.ui.MatchRecipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MatchRecipeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MatchRecipeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is match recipe fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}