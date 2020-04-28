package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
     TextView textViewTime, textViewDistance;
     Button btnSaveChanges, btnChangePin;
     int timeMin = 50, timeMax = 200, timeCurrent;
     int distanceMin = 100, distanceMax = 750, distanceCurrent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        editDistance.setMax(distanceMax - distanceMin);
        editTime.setMax(timeMax - timeMin);


        while(res.moveToNext()){
            editAge.setText(res.getString(4));
            editHeight.setText(res.getString(5));
            editHairColour.setText(res.getString(6));
            editWeight.setText(res.getString(7));
            distanceCurrent = res.getInt(12);
            editDistance.setProgress(distanceCurrent - distanceMin);
            timeCurrent = res.getInt(13);
            editTime.setProgress(timeCurrent - timeMin);
            editEmergancyContact.setText(res.getString(14));
            editPoliceContact.setChecked(alertLevel(res.getInt(15)));
            editAccelAndGyro.setChecked(AccelerometersAndGryo(res.getInt(16)));

        }

        textViewTime = (TextView) findViewById(R.id.textViewTimeValue);
        textViewTime.setText(timeCurrent + "% of the journey time before the last alert goes off");
        editTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeCurrent = progress + timeMin;
                textViewTime.setText(timeCurrent + "% of the journey time before the last alert goes off");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textViewDistance = (TextView) findViewById(R.id.textViewDistanceValue);
        textViewDistance.setText(distanceCurrent/10 + "m before alert goes off");
        editDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceCurrent = progress + distanceMin;
                textViewDistance.setText(distanceCurrent/10 + "m before alert goes off");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        saveChanges();
        changePIN();

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
        res.moveToNext();
        btnSaveChanges.setOnClickListener(
                v -> {
                    boolean isUpdated = myDb.updateUserData(
                            res.getString(1),
                            res.getString(2),
                            res.getString(3),
                            editAge.getText().toString(),
                            editHeight.getText().toString(),
                            editHairColour.getText().toString(),
                            editWeight.getText().toString(),
                            res.getString(8),
                            res.getString(9),
                            res.getString(10),
                            res.getString(11),
                            distanceCurrent,
                            timeCurrent,
                            editEmergancyContact.getText().toString(),
                            state(editPoliceContact.isChecked()),
                            state(editAccelAndGyro.isChecked()),
                            res.getInt(17));

                    if(isUpdated){
                        Toast.makeText(getApplicationContext(),"Changes Made", Toast.LENGTH_LONG).show();
                        Log.d("DISTANCE: ", String.valueOf(editDistance.getProgress()));
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Changes Not Made", Toast.LENGTH_LONG).show();

                    }
                }
        );

    }

    private void changePIN() {
        btnChangePin = findViewById(R.id.buttonChangePin);

        btnChangePin.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
                SettingsActivity.this.getSupportFragmentManager().beginTransaction().add(android.R.id.content, ChangePinFragment.newInstance(getApplicationContext())).commit();
            }
        });
    }

    public Integer state(Boolean State){
        if (State){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SettingsActivity.this, homescreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }


}
