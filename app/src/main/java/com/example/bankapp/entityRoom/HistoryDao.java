package com.example.bankapp.entityRoom;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM histories WHERE idSenderCard = :idCard")
    Flowable<List<History>> allHistoryCard(int idCard);

    @Insert
    void insert(History... histories);
}
