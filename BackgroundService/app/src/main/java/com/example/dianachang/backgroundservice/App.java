package com.example.dianachang.backgroundservice;

import android.app.Application;
import android.content.Intent;

/**
 * Created by dianachang on 2016-09-17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }
}