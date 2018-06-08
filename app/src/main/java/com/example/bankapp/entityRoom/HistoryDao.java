package com.example.bankapp.entityRoom;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM histories ORDER BY id")
    Flowable<List<History>> allHistories();

    @Query("SELECT COUNT(*) from histories")
    int count();

    @Insert
    void insert(History... histories);

    @Update
    void update(History history);

    @Delete
    void delete(History... histories);
}
