package com.example.bankapp;

import android.app.Application;

import com.example.bankapp.component.DaggerDataBaseComponent;
import com.example.bankapp.component.DataBaseComponent;
import com.example.bankapp.module.DatabaseModule;

public class MyApp extends Application {
    private static MyApp app;
    private DataBaseComponent dataBaseComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        dataBaseComponent = DaggerDataBaseComponent.builder()
                .databaseModule(new DatabaseModule(getApplicationContext()))
                .build();
    }//onCreate

    public static MyApp app() {
        return app;
    }

    public DataBaseComponent dataBaseComponent() {
        return dataBaseComponent;
    }
}//class MyApp
