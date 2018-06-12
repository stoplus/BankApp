package com.example.bankapp;

import android.view.View;
import android.widget.TextView;

public interface OnItemListener {
    void onItemClick(int position, View v, TextView cardNumber, TextView totalAmount);

    void onItemLongClick(int position, View v);
}
