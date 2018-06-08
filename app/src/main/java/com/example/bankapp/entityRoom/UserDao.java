package com.example.bankapp.entityRoom;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY id")
    Flowable<List<User>> allUsers();

    @Query("SELECT COUNT(*) from users")
    int count();

    @Insert
    void insert(User... users);

    @Update
    void update(User user);

    @Delete
    void delete(User... users);
}
