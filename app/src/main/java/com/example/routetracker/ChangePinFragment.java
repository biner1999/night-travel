package com.example.routetracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePinFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private DatabaseFunctions myDb;
    private Context context;

    public ChangePinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChangePinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePinFragment newInstance(Context c) {
        ChangePinFragment fragment = new ChangePinFragment();
        fragment.context = c;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = (Context) getArguments().get(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String firsName, surname, gender, age, height, hair, weight, ethnicity, question, answer,
                emergencyctc;
        int dist, time , police, gyro;

        View rootView = inflater.inflate(R.layout.fragment_change_pin, null);
        myDb = new DatabaseFunctions(context);
        Cursor res = myDb.getUserIDOne();
        res.moveToNext();
        firsName = res.getString(1);
        surname = res.getString(2);
        gender = res.getString(3);
        age = res.getString(4);
        height = res.getString(5);
        hair = res.getString(6);
        weight = res.getString(7);
        ethnicity = res.getString(8);
        question = res.getString(10);
        answer = res.getString(11);
        dist = res.getInt(12);
        time = res.getInt(13);
        emergencyctc = res.getString(14);
        police = res.getInt(15);
        gyro = res.getInt(16);



        String currentPin = res.getString(9);

        Log.d("CurrentPIN", currentPin);

        EditText existingPin = rootView.findViewById(R.id.existingPin);
        EditText newPin = rootView.findViewById(R.id.newpin);

        Button confirmBtn = rootView.findViewById(R.id.confirmNewPinBtn);


        confirmBtn.setOnClickListener(v -> {

            boolean updated;
            if (existingPin.getText().toString().equals(currentPin)) {
                //Existing PIN is correct
                if (newPin.getText().toString().equals(currentPin)) {
                    //PINs are same, do not change PIN
                    Toast.makeText(context,"New PIN cannot be same as existing", Toast.LENGTH_LONG).show();
                } else if (newPin.getText().toString().isEmpty() || newPin.getText().toString().length() < 4) {
                    //New PIN is too short, do not change PIN
                    Toast.makeText(context,"Please input new 4-digit PIN", Toast.LENGTH_LONG).show();
                } else {
                    //New PIN is valid and existing PIN is correct, change PIN
                    updated = myDb.updateUserData("1",
                            firsName,
                            surname,
                            gender,
                            age,
                            height,
                            hair,
                            weight,
                            ethnicity,
                            newPin.getText().toString(),
                            question,
                            answer,
                            dist,
                            time,
                            emergencyctc,
                            police,
                            gyro);

                    if(updated) {
                        Toast.makeText(context, "PIN Changed", Toast.LENGTH_LONG).show();
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                    else
                        Toast.makeText(context,"PIN Not Changed", Toast.LENGTH_LONG).show();
                    }
                }else{
                    //Existing PIN incorrect
                    Toast.makeText(context,"Incorrect existing PIN", Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
}
