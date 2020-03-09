package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class SavedDestinationActivity extends AppCompatActivity implements SaveAdapter.OnCardListener{

    DatabaseFunctions myDb;
    EditText editEndDestination;

    private RecyclerView mRV;
    private RecyclerView.LayoutManager mLM;
    private RecyclerView.Adapter mA;
    private ArrayList<String> mData;

    Button btnviewAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_destination);
        myDb = new DatabaseFunctions(this);

        mRV = (RecyclerView) findViewById(R.id.recycler_view);
        mData = new ArrayList<>();

        //hard coding data in
        myDb.insertRouteData("1", "51.471895, -3.157569");
        myDb.insertRouteData("1", "51.479657, -3.171926");

        Cursor res = myDb.getAllRouteData();
        if (res.getCount() == 0){
            //show message
            showMessage("Empty", "No Saves Found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            mData.add("ID :" + res.getString(0)+ "\n UserID :" + res.getString(1)+
                    "\n End Destination :" + res.getString(2)+ "\n\n");
            /*buffer.append("ID :" + res.getString(0)+ "\n");
            buffer.append("UserID :" + res.getString(1)+ "\n");
            buffer.append("End Destination :" + res.getString(2)+ "\n\n");*/
        }
        mLM = new LinearLayoutManager(this);
        mRV.setHasFixedSize(true);
        mA = new SaveAdapter(mData, this);
        mRV.setLayoutManager(mLM);
        mRV.setAdapter(mA);

        btnviewAll = (Button)findViewById(R.id.buttonViewAll);


        //Initalise
        configureBackButton();
        myDb.autoCreateDatabase();

        viewAll();
    }

    private void configureBackButton(){
        Button backButton = findViewById(R.id.buttonSavedDestinationBack);
        backButton.setOnClickListener(v -> finish());
    }

    public void viewAll(){
        btnviewAll.setOnClickListener(
                v -> {

                    Cursor res = myDb.getAllRouteData();
                    if (res.getCount() == 0){
                        //show message
                        showMessage("Error", "Nothing Found");
                        return;
                    }

                    StringBuffer buffer = new StringBuffer();
                    while(res.moveToNext()){
                        buffer.append("ID :" + res.getString(0)+ "\n");
                        buffer.append("UserID :" + res.getString(1)+ "\n");
                        buffer.append("End Destination :" + res.getString(2)+ "\n\n");
                    }
                    //Show All Data
                    showMessage("Data",buffer.toString());
                }
        );
    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    public void onCardClick(int position) {
        /* mData.get(position);
        Intent intent = new Intent(this, );
        startActivity(intent); */
    }

}
