package com.example.bankapp.entityRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "History")
public class History {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "amount")
    private int amount;

    @ColumnInfo(name = "beneficiaryCard")
    private int beneficiaryCard;

    @ColumnInfo(name = "idSenderCard")
    private int idSenderCard;

    public History(String date, int amount, int beneficiaryCard, int idSenderCard) {
        this.date = date;
        this.amount = amount;
        this.beneficiaryCard = beneficiaryCard;
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

    public int getBeneficiaryCard() {
        return beneficiaryCard;
    }

    public void setBeneficiaryCard(int beneficiaryCard) {
        this.beneficiaryCard = beneficiaryCard;
    }

    public int getIdSenderCard() {
        return idSenderCard;
    }

    public void setIdSenderCard(int idSenderCard) {
        this.idSenderCard = idSenderCard;
    }
}
