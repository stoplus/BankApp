package com.example.bankapp;

import android.view.View;

public interface OnItemListener {
    void onItemClick(int position, View v);
    void onItemLongClick(int position, View v);
}
