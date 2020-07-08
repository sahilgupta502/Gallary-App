package com.gallaryapp.utils;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.gallaryapp.api.WebService;

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
        new WebService();
    }
}
