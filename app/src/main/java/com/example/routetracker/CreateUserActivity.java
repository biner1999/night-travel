package com.example.routetracker;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class CreateUserActivity extends AppCompatActivity {

    DatabaseFunctions myDb;
    EditText editFirst_Name,editSurname, editPin, editAnswer, editAge, editHeight;
    Button btnAddData, btnviewAll;
    Spinner editQuestion, editGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        // Initialise database
        myDb = new DatabaseFunctions(this);

        // Initialise form
        editFirst_Name = findViewById(R.id.inputFirstName);
        editSurname= findViewById(R.id.inputSurname);
        editPin = findViewById(R.id.inputPin);
        editQuestion = findViewById(R.id.securQSpinner);
        editAnswer = findViewById(R.id.inputSecAns);
        editAge = findViewById(R.id.inputAge);
        editHeight = findViewById(R.id.inputHeight);
        editQuestion = findViewById(R.id.securQSpinner);
        editGender = findViewById(R.id.genderSpinner);

        // Initialise buttons
        btnAddData = findViewById(R.id.btnSubmit);


        // Initialise spinners
        ArrayAdapter<CharSequence> qAdapter = ArrayAdapter.createFromResource(this,
                R.array.security_array, R.layout.spinner_format);

        ArrayAdapter<CharSequence> gAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, R.layout.spinner_format);

        initSpinner(editQuestion,qAdapter);
        initSpinner(editGender,gAdapter);




        AddData();
    }

    public void AddData() {
        btnAddData.setOnClickListener(
                v -> {
                    boolean isInserted = myDb.insertDataUser(
                            editFirst_Name.getText().toString(),
                            editSurname.getText().toString(),
                            editGender.getSelectedItem().toString(),
                            editAge.getText().toString(),
                            editHeight.getText().toString(),
                            "0",
                            "0",
                            "0",
                            editPin.getText().toString(),
                            editQuestion.getSelectedItem().toString(),
                            editAnswer.getText().toString(),
                            100,
                            15,
                            "",
                            0,
                            1
                            );

                    if(isInserted){
                        Toast.makeText(CreateUserActivity.this,"User Created", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(CreateUserActivity.this,"Data Not Inserted", Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    private void initSpinner(Spinner spinner, ArrayAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}

