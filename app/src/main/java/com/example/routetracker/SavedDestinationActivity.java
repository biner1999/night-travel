package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SavedDestinationActivity extends AppCompatActivity{

    private ArrayList<SaveDestinationItem> mSaveList;

    DatabaseFunctions myDb;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private RecyclerView.Adapter mAdapter;

    private Button buttonRemove;
    private Button buttonBack;
    private Button buttonSelect;
    private EditText editTextRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_destination);

        myDb = new DatabaseFunctions(this);

        createSaveList();
        buildRecyclerView();

        buttonRemove = findViewById(R.id.buttonRemove);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSelect = findViewById(R.id.buttonSelect);
        editTextRemove = findViewById(R.id.edittext_remove);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(pos);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void removeItem(int position){
        mSaveList.remove(position-1);
        mAdapter.notifyItemRemoved(position-1);
    }

    public void createSaveList(){
        myDb.insertRouteData("1", "51.471895, -3.157569");

        Cursor res = myDb.getAllRouteData();

        mSaveList = new ArrayList<>();

        if (res.getCount() == 0){
            //show message
            showMessage("Empty", "No Saves Found");
            return;
        }

        while (res.moveToNext()) {
            mSaveList.add(new SaveDestinationItem(R.drawable.ic_map, res.getString(0), res.getString(2)));
        }
    }

    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManger = new LinearLayoutManager(this);
        mAdapter = new SaveAdapter(mSaveList);

        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

}
