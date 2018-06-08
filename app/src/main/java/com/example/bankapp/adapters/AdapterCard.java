package com.example.bankapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.bankapp.R;

import com.example.bankapp.OnItemListener;
import com.example.bankapp.entityRoom.Card;

import java.util.ArrayList;
import java.util.List;

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.ViewHolder> {
    private LayoutInflater inflater;
    private List<Card> list;
    private OnItemListener onItemListener;
    private Context context;

    public AdapterCard(Context context, List<Card> list, OnItemListener onItemListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.onItemListener = onItemListener;
        this.context = context;
    }//AdapterForAdmin

    @Override
    public int getItemCount() {
        return list.size();
    }//getItemCount

    @Override
    public long getItemId(int position) {
        return position;
    }//getItemId

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    } // onCreateViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber;
        TextView totalAmount;
        LinearLayout layout;


        private ViewHolder(View view) {
            super(view);
            cardNumber = view.findViewById(R.id.idTextView);
            totalAmount = view.findViewById(R.id.idTotalAmount);
            layout = view.findViewById(R.id.idConstraintLayout);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onItemClick(getAdapterPosition(), v);
                }
            });
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemListener.onItemLongClick(getAdapterPosition(), v);
                    return false;
                }
            });
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.cardNumber.setText(list.get(position).getCardNumber());
        String total = String.valueOf(list.get(position).getTotalAmount());
        holder.totalAmount.setText(String.format("%s %s", total, context.getResources().getString(R.string.uah)));
    }//onBindViewHolder

    public void deleteCardAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//updates after removing Item at position
        notifyItemRangeChanged(pos, list.size());//updates the items of the following items
    }//deleteFromListAdapter
}//class Adapter