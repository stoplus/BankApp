package com.example.bankapp.component;

import com.example.bankapp.module.DatabaseModule;
import com.example.bankapp.ui.LoginActivity;
import com.example.bankapp.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DatabaseModule.class)
public interface DataBaseComponent {
    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
}
