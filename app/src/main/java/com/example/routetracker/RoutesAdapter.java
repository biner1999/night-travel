package com.example.routetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteVH> {

    private ArrayList<RouteDataItem> mRouteList;
    private RoutesAdapter.onItemClickListener mListener;
    private Context context;

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(RoutesAdapter.onItemClickListener listener) {mListener = listener;}

    RoutesAdapter(Context inContext, ArrayList<RouteDataItem> inRouteList) {
        mRouteList = inRouteList;
        context = inContext;
    }

    @NonNull
    @Override
    public RouteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.route_card, parent, false);
        return new RouteVH(v, mListener);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RouteVH holder, int position) {

        RouteDataItem currentItem = mRouteList.get(position);

        holder.mImageView.setImageResource(currentItem.getImage());
        holder.mTextView1.setText("Route " + currentItem.getID());
        holder.mTextView2.setText("Crimes: " + currentItem.getCrimeCount()
                                  + "  Time: " + currentItem.getRouteTime()
                                  + "  Distance: " + currentItem.getRouteDistance());
    }

    @Override
    public int getItemCount() {return mRouteList.size();}

    static class RouteVH extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextView1, mTextView2;

        RouteVH(@NonNull View itemView, final RoutesAdapter.onItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView1);
            mTextView2 = itemView.findViewById(R.id.textView2);

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
