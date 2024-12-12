package com.example.evaluacionnacional.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("HOLAA!!"); // Valor inicial que será observado por la UI.
    }

    public LiveData<String> getText() {
        return mText;
    }
}