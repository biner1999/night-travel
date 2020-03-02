package com.example.routetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.ViewHolder> {

    private ArrayList<String> mData;

    public SaveAdapter(ArrayList<String> data){
        mData = data;
    }

    public SaveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_destination_row, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(SaveAdapter.ViewHolder holder, int pos){
        holder.mTitle.setText(mData.get(pos));
    }

    public int getItemCount(){
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;

        public ViewHolder(View itemView){
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
        }
    }
}
