package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;

public class SavedDestinationActivity extends AppCompatActivity{

    private ArrayList<SaveDestinationItem> mSaveList;

    DatabaseFunctions myDb;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private SaveAdapter mAdapter;
    Context mContext;
    public static homescreenActivity homescreen;

    private Button buttonBack;
    private Button buttonSelect;

    private int pos = -1;

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
            mSaveList.add(new SaveDestinationItem(R.drawable.ic_map, res.getString(3), res.getString(0), res.getString(2)));
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
        mAdapter.notifyItemRemoved(position);
        mSaveList.remove(position);
        myDb.deleteRouteData(Integer.toString(position));
    }

    public void changeItem(int position, int image){
        mSaveList.set(position, new SaveDestinationItem(image, mSaveList.get(position).getmText1(), mSaveList.get(position).getmText2(), mSaveList.get(position).getmDestination()));
        mAdapter.notifyItemChanged(position);
    }

    public void setButtons(){
        buttonBack = findViewById(R.id.buttonBack);
        buttonSelect = findViewById(R.id.buttonSelect);

        buttonBack.setOnClickListener(v -> startActivity(new Intent(SavedDestinationActivity.this, homescreenActivity.class)));

        buttonSelect.setOnClickListener(v -> {
            startActivity(new Intent(SavedDestinationActivity.this, homescreenActivity.class));
            Log.d("LOCATIONMARKER:", mSaveList.get(pos).getmDestination());
            homescreen.loadDestination(mSaveList.get(pos).getmText1(), mSaveList.get(pos).getmDestination());


        });
    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

}
