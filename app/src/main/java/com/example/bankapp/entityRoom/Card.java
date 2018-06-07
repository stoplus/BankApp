package com.example.bankapp.entityRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "card")
public class Card {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "cardNumber")
    private int cardNumber;

    @ColumnInfo(name = "totalAmount")
    private int totalAmount;

    @ColumnInfo(name = "idHistory")
    private int idHistory;

    @ColumnInfo(name = "pinCode")
    private int pinCode;

    public Card(int cardNumber, int totalAmount, int idHistory, int pinCode) {
        this.cardNumber = cardNumber;
        this.totalAmount = totalAmount;
        this.idHistory = idHistory;
        this.pinCode = pinCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(int idHistory) {
        this.idHistory = idHistory;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }
}
