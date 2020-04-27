package com.example.routetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedDestinationActivity extends AppCompatActivity{

    private ArrayList<SaveDestinationItem> mSaveList;

    DatabaseFunctions myDb;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private SaveAdapter mAdapter;
    public static homescreenActivity homescreen;

    private Button buttonSelect;
    private Button buttonFav;
    private Switch switchFilter;

    private int pos = -1;
    private boolean selected = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_destination);

        myDb = new DatabaseFunctions(this);

        Cursor res = myDb.getAllRouteData();

        mSaveList = new ArrayList<>();

        if (res.getCount() == 0){
            //show message
            showMessage("Empty", "No Saves Found");
            return;
        }


        while (res.moveToNext()) {
            mSaveList.add(new SaveDestinationItem(R.drawable.ic_map, res.getString(3), res.getInt(4), res.getString(2)));
        }


        buildRecyclerView();
        setButtons();
    }

    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManger = new LinearLayoutManager(this);
        mAdapter = new SaveAdapter(mSaveList);

        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SaveAdapter.onItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if(!(pos==-1)){
                    changeItem(pos, R.drawable.ic_map);
                    changeItem(position, R.drawable.ic_check);
                    pos = position;
                }
                else{
                    changeItem(position, R.drawable.ic_check);
                    pos = position;
                }
                selected = true;
                setButtons();

            }
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);

            }
        });
    }

    public SavedDestinationActivity(){
        //no args constructor
    }

    public void removeItem(int position){
        myDb.deleteRouteData(mSaveList.get(position).getmDestination());
        mSaveList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, int image){
        mSaveList.set(position, new SaveDestinationItem(image, mSaveList.get(position).getmText1(), mSaveList.get(position).getmFavValue(), mSaveList.get(position).getmDestination()));
        mAdapter.notifyItemChanged(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setButtons(){
        buttonSelect = findViewById(R.id.buttonSelect);
        if (selected) {
            buttonSelect.setOnClickListener(v -> {
                startActivity(new Intent(SavedDestinationActivity.this, homescreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                homescreen.loadDestination(mSaveList.get(pos).getmText1(), mSaveList.get(pos).getmDestination());
                finish();
            });
        }
        else{
            buttonSelect.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "No Route Selected", Toast.LENGTH_SHORT).show();
            });
        }

        buttonFav = findViewById(R.id.buttonFav);
        if (selected) {
            buttonFav.setOnClickListener(v -> {
                myDb.favouriteRouteData(mSaveList.get(pos).getmDestination());
                Cursor res = myDb.getAllRouteData();
                while(res.moveToNext()){
                    System.out.println(res.getString(4));
                }
            });
        }
        else{
            buttonFav.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "No Route Selected", Toast.LENGTH_SHORT).show();
            });
        }

        switchFilter = findViewById(R.id.filter);
        switchFilter.setOnClickListener(v -> {
            myDb = new DatabaseFunctions(this);
            Cursor res = myDb.getAllRouteData();

            mSaveList = new ArrayList<>();

            if (!switchFilter.isChecked()){
                while (res.moveToNext()) {
                    mSaveList.add(new SaveDestinationItem(R.drawable.ic_map, res.getString(3), res.getInt(4), res.getString(2)));
                }
            }
            else{
                while (res.moveToNext()) {
                    System.out.println(res.getInt(4));
                    if (res.getInt(4) == 1){
                        mSaveList.add(new SaveDestinationItem(R.drawable.ic_map, res.getString(3), res.getInt(4), res.getString(2)));
                    }

                }
            }
            buildRecyclerView();

        });
    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SavedDestinationActivity.this, homescreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

}
