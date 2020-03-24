package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

public class SavedDestinationActivity extends AppCompatActivity{

    private ArrayList<SaveDestinationItem> mSaveList;

    DatabaseFunctions myDb;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private SaveAdapter mAdapter;
    private Context mContext;

    private Button buttonBack;
    private Button buttonSelect;

    private int pos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_destination);

        myDb = new DatabaseFunctions(this);

        createSaveList();
        buildRecyclerView();
        setButtons();
    }

    public SavedDestinationActivity(Context context){
        //your code.
        this.mContext=context;
    }

    public void removeItem(int position){
        mSaveList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, int image){
        mSaveList.add(position, new SaveDestinationItem(image, mSaveList.get(position).getmText1(), mSaveList.get(position).getmText2()));
        mAdapter.notifyItemChanged(position);
    }

    public void createSaveList(){
        myDb.insertRouteData("1", "51.471895, -3.157569");
        myDb.insertRouteData("1", "51.481890, -3.167560");

        Cursor res = myDb.getAllRouteData();

        mSaveList = new ArrayList<>();

        if (res.getCount() == 0){
            //show message
            showMessage("Empty", "No Saves Found");
            return;
        }

        while (res.moveToNext()) {
            mSaveList.add(new SaveDestinationItem(R.drawable.ic_map, res.getString(0), res.getString(1)));
        }

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
                    changeItem(position, R.drawable.ic_check);
                    pos = position;
                }
                else{
                    for(int i =0; i<mSaveList.size();i++){
                        changeItem(i+1, R.drawable.ic_map);
                    }
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

    public void setButtons(){
        buttonBack = findViewById(R.id.buttonBack);
        buttonSelect = findViewById(R.id.buttonSelect);

        buttonBack.setOnClickListener(v -> startActivity(new Intent(SavedDestinationActivity.this, homescreenActivity.class)));

        buttonSelect.setOnClickListener(v -> {
            startActivity(new Intent(SavedDestinationActivity.this, homescreenActivity.class));
            if(mContext instanceof MainActivity){
                ((homescreenActivity) mContext).markLocation(Integer.parseInt(mSaveList.get(pos).getmText2()));
            }
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
