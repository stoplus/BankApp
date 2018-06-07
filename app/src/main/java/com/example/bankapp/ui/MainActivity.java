package com.example.bankapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bankapp.MyApp;
import com.example.bankapp.R;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    @Inject
    CardDao cardDao;
    @Inject
    HistoryDao historyDao;
    @Inject
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApp.app().appComponent().inject(this);
    }
}
