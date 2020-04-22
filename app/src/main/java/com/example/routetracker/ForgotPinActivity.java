package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPinActivity extends AppCompatActivity {

    private DatabaseFunctions myDb;
    private Button submitBtn;
    private EditText editPin, editAns;
    private TextView questionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        myDb  = new DatabaseFunctions(this);
        submitBtn = findViewById(R.id.submitBtn);
        editPin = findViewById(R.id.editPin);
        editAns = findViewById(R.id.editAnswer);
        questionView = findViewById(R.id.textViewQuestion);

        Cursor res = myDb.getUserIDOne();
        res.moveToNext();
        // Display question
        questionView.setText(res.getString(10));
        String correctAns = res.getString(11).toLowerCase();

        submitBtn.setOnClickListener(v -> {
            Log.d("Input ans:", editAns.getText().toString());
            Log.d("correct ans:", correctAns);
            if (!editAns.getText().toString().toLowerCase().equals(correctAns)) {
                Toast.makeText(getApplicationContext(),"Answer Incorrect", Toast.LENGTH_LONG).show();
            }
            else if (editAns.getText().toString().equals(correctAns) && editPin.getText().toString().length() < 4) {
                Toast.makeText(getApplicationContext(),"Please input new 4-digit PIN", Toast.LENGTH_LONG).show();
            }
            else {
                boolean isUpdated = myDb.updateUserData("1",
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getString(8),
                        editPin.getText().toString(),
                        res.getString(10),
                        res.getString(11),
                        res.getInt(12),
                        res.getInt(13),
                        res.getString(14),
                        res.getInt(15),
                        res.getInt(16));

                if (isUpdated) {
                    Toast.makeText(getApplicationContext(), "PIN Changed Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "Not updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
