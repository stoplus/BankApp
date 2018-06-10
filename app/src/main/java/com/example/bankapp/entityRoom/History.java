package com.example.bankapp.entityRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "histories")
public class History {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "amount")
    private int amount;

    @ColumnInfo(name = "recipientCard")
    private String recipientCard;

    @ColumnInfo(name = "idSenderCard")
    private int idSenderCard;

    public History(String date, int amount, int idSenderCard, String recipientCard) {
        this.date = date;
        this.amount = amount;
        this.recipientCard = recipientCard;
        this.idSenderCard = idSenderCard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getRecipientCard() {
        return recipientCard;
    }

    public void setRecipientCard(String recipientCard) {
        this.recipientCard = recipientCard;
    }

    public int getIdSenderCard() {
        return idSenderCard;
    }

    public void setIdSenderCard(int idSenderCard) {
        this.idSenderCard = idSenderCard;
    }
}
