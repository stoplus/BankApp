package com.example.bankapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bankapp.R;
import com.example.bankapp.entityRoom.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterSpinner extends ArrayAdapter<Card> {
    public List<Card> list;
    private Context context;
    private int textViewResourceId;

    public AdapterSpinner(Context context, int textViewResourceId, List<Card> list) {
        super(context, textViewResourceId, list);
        this.textViewResourceId = textViewResourceId;
        this.list = new ArrayList<>(list);
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater)
                    .inflate(textViewResourceId, parent, false);

            holder = new ViewHolder();

            holder.card = convertView.findViewById(R.id.idCardSpiner);
            holder.amount = convertView.findViewById(R.id.idAmountSpiner);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        String cardNum = list.get(position).getCardNumber();
        if (list.get(position).getId() != 0) {
            cardNum = cardNum.substring(0, 4) + " * " + cardNum.substring(15, 19);
            String total = String.valueOf(list.get(position).getTotalAmount());
            holder.amount.setText(String.format("%s %s", total, context.getResources().getString(R.string.uah)));
        } else holder.amount.setText("");

        holder.card.setText(cardNum);
        return convertView;
    }//getCustomView

    private class ViewHolder {
        public TextView card;
        public TextView amount;
    }//ViewHolder
}//class AdapterSpinner