package com.example.routetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.SaveViewHolder> {

    private ArrayList<SaveDestinationItem> mSaveList;

    public static class SaveViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public SaveViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView1);
            mTextView2 = itemView.findViewById(R.id.textView2);
        }
    }

    public SaveAdapter(ArrayList<SaveDestinationItem> saveList){
        mSaveList = saveList;
    }

    @Override
    public SaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_destinations_card, parent, false);
        SaveViewHolder svh = new SaveViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SaveViewHolder holder, int position) {
        SaveDestinationItem currentItem = mSaveList.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mTextView2.setText(currentItem.getmText2());
    }

    @Override
    public int getItemCount() {
        return mSaveList.size();
    }

}
