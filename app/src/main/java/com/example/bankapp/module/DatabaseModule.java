package com.example.bankapp.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.bankapp.database.MyDatabase;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    private Context context;

    public DatabaseModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public MyDatabase provideMyDatabase(Context context) {
        return Room.databaseBuilder(context, MyDatabase.class, "my-db").build();
    }

    @Singleton
    @Provides
    public CardDao provideCardDao(MyDatabase myDatabase) {
        return myDatabase.cardDao();
    }

    @Singleton
    @Provides
    public UserDao provideUserDao(MyDatabase myDatabase) {
        return myDatabase.userDao();
    }

    @Singleton
    @Provides
    public HistoryDao provideHistoryDao(MyDatabase myDatabase) {
        return myDatabase.historyDao();
    }

}//DatabaseModule
