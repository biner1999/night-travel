package com.example.routetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
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
    EditText editFirst_Name,editSurname, editPin, editAnswer, editNumber, editAge, editHeight, editWeight, editEthnicity, editHair;
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
        editNumber = findViewById(R.id.inputNumber);
        editAge = findViewById(R.id.inputAge);
        editHeight = findViewById(R.id.inputHeight);
        editQuestion = findViewById(R.id.securQSpinner);
        editGender = findViewById(R.id.genderSpinner);
        editEthnicity = findViewById(R.id.inputEthnicity);
        editHair = findViewById(R.id.inputHairColour);
        editWeight = findViewById(R.id.inputWeight);

        // Initialise buttons
        btnAddData = findViewById(R.id.btnSubmit);


        // Initialise spinners
        ArrayAdapter<CharSequence> qAdapter = ArrayAdapter.createFromResource(this,
                R.array.security_array, R.layout.spinner_format);

        ArrayAdapter<CharSequence> gAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, R.layout.spinner_format);

        initSpinner(editQuestion,qAdapter);
        initSpinner(editGender,gAdapter);

        alertMessageNew();




        AddData();
    }

    public void AddData() {


        btnAddData.setOnClickListener(
                v -> {
                    if (validate()) {
                        boolean isInserted = myDb.insertDataUser(
                                editFirst_Name.getText().toString(),
                                editSurname.getText().toString(),
                                editGender.getSelectedItem().toString(),
                                editAge.getText().toString(),
                                editHeight.getText().toString(),
                                editHair.getText().toString(),
                                editWeight.getText().toString(),
                                editEthnicity.getText().toString(),
                                editPin.getText().toString(),
                                editQuestion.getSelectedItem().toString(),
                                editAnswer.getText().toString(),
                                250,
                                100,
                                editNumber.getText().toString(),
                                0,
                                1,
                                1);

                        if (isInserted) {
                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG).show();
                            Intent loginScreen = new Intent(CreateUserActivity.this, LoginActivity.class);
                            startActivity(loginScreen);
                            CreateUserActivity.this.finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Not Inserted", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        );
    }

    private void initSpinner(Spinner spinner, ArrayAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void alertMessageNew() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Welcome to Route Tracker! Please create an account to get started.\n" +
                "NB - All information is stored on your device and is not shared with anyone.").setCancelable(false)
                .setPositiveButton("Create Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private boolean validate() {
        boolean valid = true;

        // First Name
        if (editFirst_Name.getText().toString().isEmpty()) {
            editFirst_Name.setError("Please input your first name");
            valid = false;
        }else{
            editFirst_Name.setError(null);
        }

        // Surname
        if (editSurname.getText().toString().isEmpty()) {
            editSurname.setError("Please input your surname");
            valid = false;
        }else{
            editSurname.setError(null);
        }

        // PIN
        if (editPin.getText().toString().isEmpty() || editPin.getText().toString().length() < 4) {
            editPin.setError("Please input a 4-digit PIN");
            valid = false;
        }else{
            editPin.setError(null);
        }

        // Security Answer
        if (editAnswer.getText().toString().isEmpty()) {
            editAnswer.setError("Please input an answer to the security question");
            valid = false;
        }else{
            editAnswer.setError(null);
        }

        // Emergency Contact
        if (editNumber.getText().toString().length() < 9) {
            editNumber.setError("Please input a valid phone number");
            valid = false;
        }else{
            editNumber.setError(null);
        }

        // Age
        if (editAge.getText().toString().isEmpty()) {
            editAge.setError("Please input your age");
            valid = false;
        }else{
            editAge.setError(null);
        }

        // Height
        if (editHeight.getText().toString().isEmpty()) {
            editHeight.setError("Please input your height in cm");
            valid = false;
        }else{
            editSurname.setError(null);
        }
        return valid;
    }
}

