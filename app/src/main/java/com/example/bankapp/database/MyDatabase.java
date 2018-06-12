package com.example.bankapp.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;

import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.History;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.User;
import com.example.bankapp.entityRoom.UserDao;

@Database(entities = {Card.class, History.class, User.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public abstract CardDao cardDao();

    public abstract HistoryDao historyDao();

    public abstract UserDao userDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}//class MyDatabase
