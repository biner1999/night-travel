package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    DatabaseFunctions myDb;
    EditText editHeight,editHairColour, editAge,editWeight, editEmergancyContact;
    Switch editPoliceContact, editAccelAndGyro;
    SeekBar editDistance, editTime;
     TextView text_view_distance, getText_view_time;
     Button btnSaveChanges;

     String firsName, surname, gender, question, answer, ethnicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configureBackButtonSettings();

        myDb = new DatabaseFunctions(this);

        Cursor res = myDb.getUserIDOne();

        if (res.getCount() == 0){
            //show message
            showMessage("Error", "Nothing Found");
            return;
        }


        editAge = findViewById(R.id.editAge);
        editHeight = findViewById(R.id.editTextHeight);
        editHairColour = findViewById(R.id.editTextHairColour);
        editWeight = findViewById(R.id.editTextWeight);
        editPoliceContact = findViewById(R.id.switchPoliceContact);
        editAccelAndGyro = findViewById(R.id.switchAccelerometerAndGyroscope);
        editDistance = findViewById(R.id.seekBarDistance);
        editTime = findViewById(R.id.seekBarTime);
        editEmergancyContact = findViewById(R.id.editTextEmergancyContact);

        //editDistance.s(0);

        btnSaveChanges = findViewById(R.id.buttonSaveChanges);

        editDistance.setMax(200);
        editTime.setMax(60);


        while(res.moveToNext()){
            editAge.setText(res.getString(4));
            editHeight.setText(res.getString(5));
            editHairColour.setText(res.getString(6));
            editWeight.setText(res.getString(7));
            editDistance.setProgress(res.getInt(12));
            editTime.setProgress(res.getInt(13));
            editEmergancyContact.setText(res.getString(14));
            editPoliceContact.setChecked(alertLevel(res.getInt(15)));
            editAccelAndGyro.setChecked(AccelerometersAndGryo(res.getInt(16)));

        }

        saveChanges();
    }

    public void seebbarr(){
        editDistance = findViewById(R.id.seekBarDistance);
        editTime = findViewById(R.id.seekBarTime);
        text_view_distance.setText("Distance : " + editDistance.getProgress());
    }

    private void configureBackButtonSettings(){
        Button backButton = findViewById(R.id.buttonCreateUserBack);
        backButton.setOnClickListener(v -> finish());
    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public boolean alertLevel(Integer AlertLevel){

        if(AlertLevel == 0){
            return false;
        }else{
            return true;
        }
    }

    public boolean AccelerometersAndGryo(Integer AccelerometersAndGryo){
        if(AccelerometersAndGryo == 0){
            return false;
        }else{
            return true;
        }
    }

    public void saveChanges(){
        Cursor res = myDb.getUserIDOne();

        btnSaveChanges.setOnClickListener(
                v -> {
                    boolean isUpdated = myDb.updateUserData("1",
                            firsName,
                            surname,
                            gender,
                            editAge.getText().toString(),
                           editHeight.getText().toString(),
                            editHairColour.getText().toString(),
                            editWeight.getText().toString(),
                            ethnicity,
                            //TODO CHANGE PASSWORD SECTION
                            "1234",
                            question,
                            answer,
                            editDistance.getProgress(),
                            editTime.getProgress(),
                            //TODO CHANGE EMERGENCY CONTACT SECTION
                            editEmergancyContact.getText().toString(),
                            state(editPoliceContact.isChecked()),
                            state(editAccelAndGyro.isChecked())
                            );

                    if(isUpdated){
                        Toast.makeText(SettingsActivity.this,"Changes Made", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(SettingsActivity.this,"Changes Not Made", Toast.LENGTH_LONG).show();

                    }
                }
        );

    }

    public Integer state(Boolean State){
        if (State){
            return 1;
        }else {
            return 0;
        }
    }


}
