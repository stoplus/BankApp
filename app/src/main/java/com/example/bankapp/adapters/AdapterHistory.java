package com.example.bankapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bankapp.OnItemListener;
import com.example.bankapp.R;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.History;

import java.util.ArrayList;
import java.util.List;

public class AdapterHistory  extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {
    private LayoutInflater inflater;
    private List<History> list;
    private Context context;

    public AdapterHistory(Context context, List<History> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
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
        View view = inflater.inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    } // onCreateViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView amount;

        private ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.idDate);
            amount = view.findViewById(R.id.idAmount);
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.date.setText(list.get(position).getDate());
        String total = String.valueOf(list.get(position).getAmount());
        holder.amount.setText(String.format("%s %s", total, context.getResources().getString(R.string.uah)));
    }//onBindViewHolder

    public void deleteCardAdapter(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);//updates after removing Item at position
        notifyItemRangeChanged(pos, list.size());//updates the items of the following items
    }//deleteFromListAdapter
}//class Adapter