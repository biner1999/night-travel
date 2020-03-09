package com.example.routetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private OnCardListener mOnCardListener;

    public SaveAdapter(ArrayList<String> data, OnCardListener onCardListener){
        this.mData = data;
        this.mOnCardListener = onCardListener;
    }

    @Override
    public SaveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_destination_row, parent, false);
        return new ViewHolder(v, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(SaveAdapter.ViewHolder holder, int pos){
        holder.mTitle.setText(mData.get(pos));
    }

    public int getItemCount(){
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle;

        OnCardListener onCardListener;

        public ViewHolder(View itemView, OnCardListener onCardListener){
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            this.onCardListener = onCardListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }
    }

    public interface OnCardListener{
        void onCardClick(int position);
    }
}
