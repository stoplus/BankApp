package com.example.bankapp.entityRoom;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CardDao {

    @Query("SELECT cardNumber FROM cards ")
    Flowable<List<String>> allNumbersCards();

    @Query("SELECT * FROM cards WHERE idUser = :idUser")
    Flowable<List<Card>> allCardsUser(int idUser);

    @Query("SELECT COUNT(*) from cards")
    int count();

    @Insert
    void insert(Card... cards);

    @Update
    void update(Card card);

    @Delete
    void delete(Card... cards);
}
