package com.example.routetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private ArrayList<RouteDataItem> mParam1;
    private Context context;
    private homescreenActivity activity;

    public RoutesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment RoutesFragment.
     */
    // TODO: Rename and change types and number of parameters
    static RoutesFragment newInstance(Context c, ArrayList<RouteDataItem> routeData, homescreenActivity inActivity) {
        RoutesFragment fragment = new RoutesFragment();
        fragment.mParam1 = routeData;
        fragment.context = c;
        fragment.activity = inActivity;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (ArrayList<RouteDataItem>) getArguments().get(ARG_PARAM1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.fragment_routes,null);

        RecyclerView routesRecycler = rootView.findViewById(R.id.routeRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        routesRecycler.setLayoutManager(layoutManager);

        RoutesAdapter mAdapter = new RoutesAdapter(context, mParam1, activity);
        routesRecycler.setAdapter(mAdapter);

        return rootView;
    }
}
