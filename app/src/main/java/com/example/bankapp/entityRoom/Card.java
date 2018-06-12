package com.example.bankapp.entityRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "cards")
public class Card implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "idUser")
    private int idUser;
    @ColumnInfo(name = "cardNumber")
    private String cardNumber;
    @ColumnInfo(name = "totalAmount")
    private int totalAmount;
    @ColumnInfo(name = "pinCode")
    private String pinCode;


    public Card(int idUser, String cardNumber, int totalAmount, String pinCode) {
        this.idUser = idUser;
        this.cardNumber = cardNumber;
        this.totalAmount = totalAmount;
        this.pinCode = pinCode;
    }


    protected Card(Parcel in) {
        id = in.readInt();
        idUser = in.readInt();
        cardNumber = in.readString();
        totalAmount = in.readInt();
        pinCode = in.readString();
    }


    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idUser);
        dest.writeString(cardNumber);
        dest.writeInt(totalAmount);
        dest.writeString(pinCode);
    }//writeToParcel
}//class Card
