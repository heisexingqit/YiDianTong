package com.example.yidiantong.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<String> ip = new MutableLiveData<>();

    public LiveData<String> getIp() {
        return ip;
    }

    public void setIp(String ipValue) {
        ip.setValue(ipValue);
    }

}
