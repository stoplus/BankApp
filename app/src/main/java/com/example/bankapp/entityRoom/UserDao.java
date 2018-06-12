package com.example.bankapp.entityRoom;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY id")
    Flowable<List<User>> allUsers();

    @Insert
    void insert(User... users);
}
