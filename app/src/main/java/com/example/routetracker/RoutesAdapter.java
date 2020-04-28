package com.example.routetracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteVH> {

    private ArrayList<RouteDataItem> mRouteList;
    private RoutesAdapter.onItemClickListener mListener;
    private Context context;
    private homescreenActivity activity;


    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(RoutesAdapter.onItemClickListener listener) {mListener = listener;}

    RoutesAdapter(Context inContext, ArrayList<RouteDataItem> inRouteList, homescreenActivity inActivity) {
        mRouteList = inRouteList;
        context = inContext;
        activity = inActivity;
    }

    @NonNull
    @Override
    public RouteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.route_card, parent, false);
        return new RouteVH(v, mListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RouteVH holder, int position) {

        RouteDataItem currentItem = mRouteList.get(position);

        holder.mImageView.setImageResource(currentItem.getImage());

        if (position == 0) {
            holder.mTextView1.setText("Safest Route");
        }
        else
            holder.mTextView1.setText("Alternative Route");


        holder.mTextView2.setText("Crimes: " + currentItem.getCrimeCount()
                                  + "  Time: " + currentItem.getRouteTime()
                                  + "  Distance: " + currentItem.getRouteDistance());


        holder.parent_layout.setOnClickListener(v -> {

            activity.highlightRoute(currentItem);
        });
    }

    @Override
    public int getItemCount() {return mRouteList.size();}

    static class RouteVH extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextView1, mTextView2;
        RelativeLayout parent_layout;

        RouteVH(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView1);
            mTextView2 = itemView.findViewById(R.id.textView2);
            parent_layout = itemView.findViewById(R.id.parentLayout);

            itemView.setOnClickListener(v -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }


}
