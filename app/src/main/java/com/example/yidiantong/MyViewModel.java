package com.example.yidiantong;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MyViewModel {
    private MutableLiveData<Integer> AllAnswerFlag;

    public MyViewModel() {
        AllAnswerFlag = new MutableLiveData<>();
    }

    public LiveData<Integer> getMyVariable() {
        return AllAnswerFlag;
    }

    public void setMyVariable(Integer value) {
        AllAnswerFlag.setValue(value);
    }
}
