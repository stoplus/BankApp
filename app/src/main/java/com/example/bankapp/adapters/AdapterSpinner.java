package com.example.bankapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bankapp.R;
import com.example.bankapp.entityRoom.Card;

import java.util.ArrayList;
import java.util.List;

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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(textViewResourceId, parent, false);

//            convertView = inflater.inflate(R.layout.item_card, parent, false);
            holder = new ViewHolder();

            holder.card = convertView.findViewById(R.id.idCardSpiner);
            holder.amount = convertView.findViewById(R.id.idAmountSpiner);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String cardNum = list.get(position).getCardNumber();
        if (list.get(position).getId() != 0) {
            cardNum = cardNum.substring(0, 4) + " * " + cardNum.substring(15, 19);
            String total = String.valueOf(list.get(position).getTotalAmount());
            holder.amount.setText(String.format("%s %s", total, context.getResources().getString(R.string.uah)));
        }else {
            holder.amount.setText("");
        }
        holder.card.setText(cardNum);

        return convertView;
    }

    private class ViewHolder {
        public TextView card;
        public TextView amount;
    }


}

// extends RecyclerView.Adapter<AdapterSpinner.ViewHolder> {
//    private LayoutInflater inflater;
//    private List<Card> list;
//    private Context context;
//
//    public AdapterSpinner(Context context, List<Card> list) {
//        this.inflater = LayoutInflater.from(context);
//        this.list = new ArrayList<>(list);
//        this.context = context;
//    }//AdapterForAdmin
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }//getItemCount
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }//getItemId
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.spiner, parent, false);
//        return new ViewHolder(view);
//    } // onCreateViewHolder
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView card;
//        TextView amount;
//
//        private ViewHolder(View view) {
//            super(view);
//            card = view.findViewById(R.id.idCardSpiner);
//            amount = view.findViewById(R.id.idAmountSpiner);
//        }//ViewHolder
//    }//class ViewHolder
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        String cardNum = list.get(position).getCardNumber();
//        if (list.get(position).getId() != 0) {
//            cardNum = cardNum.substring(0, 4) + " * " + cardNum.substring(16, 19);
//        }
//
//        holder.card.setText(cardNum);
//        String total = String.valueOf(list.get(position).getTotalAmount());
//        holder.amount.setText(String.format("%s %s", total, context.getResources().getString(R.string.uah)));
//    }//onBindViewHolder
//}//class Adapter