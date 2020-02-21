package com.example.routetracker;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class CreateUserActivity extends AppCompatActivity {

    DatabaseFunctions myDb;

    EditText editFirst_Name,editSurname, editGender,editAge, editPassword, editQuestion, editAnswer, editDistance, editTime, editTextId, editEmergencyContact, editAlertLevel;
    Button btnAddData;
    Button btnviewAll;
    Button btnViewUpdate;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        myDb = new DatabaseFunctions(this);


        editFirst_Name = (EditText)findViewById(R.id.editText_FirstName);
        editSurname= (EditText)findViewById(R.id.editText_SurnameName);

        editGender= (EditText)findViewById(R.id.editTextGender);
        editAge= (EditText)findViewById(R.id.editTextAge);
        editPassword = (EditText)findViewById(R.id.editText_Password);
        editQuestion = (EditText)findViewById(R.id.editText_Question);
        editAnswer = (EditText)findViewById(R.id.editText_Answer);
        editEmergencyContact = (EditText)findViewById(R.id.editText_EmergancyContact);


        btnAddData = (Button)findViewById(R.id.buttonCreateAccount);
        btnviewAll = (Button)findViewById(R.id.buttonView);
//        btnViewUpdate = (Button)findViewById(R.id.button_update);
//        btnDelete = (Button)findViewById(R.id.button_delete);

        //Initalise Buttons
        AddData();
        viewAll();
        //UpdateData();
        //DeleteData();
        myDb.autoCreateDatabase();
        configureBackButton();
    }

    public void DeleteData(){
        btnDelete.setOnClickListener(
                v -> {
                    Integer deletedRows = myDb.deleteUserData(editTextId.getText().toString());
                    if(deletedRows > 0){
                        Toast.makeText(CreateUserActivity.this,"Data Deleted", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CreateUserActivity.this,"Data Not Deleted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void UpdateData(){
        btnViewUpdate.setOnClickListener(
                v -> {
                    boolean isUpdate = myDb.updateUserData(editTextId.getText().toString(),
                            editFirst_Name.getText().toString(),
                            editSurname.getText().toString(),
                            editGender.getText().toString(),
                            editAge.getText().toString(),
                            "",
                            "",
                            "",
                            "",
                            editPassword.getText().toString(),
                            editQuestion.getText().toString(),
                            editAnswer.getText().toString(),
                            editDistance.getText().toString(),
                            editTime.getText().toString(),
                            editEmergencyContact.getText().toString(),
                            editAlertLevel.getText().toString()
                            );

                    if(isUpdate){
                        Toast.makeText(CreateUserActivity.this,"Data Updated", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CreateUserActivity.this,"Data Not Updated", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    public void AddData() {
        btnAddData.setOnClickListener(
                v -> {
                    boolean isInserted = myDb.insertDataUser(editFirst_Name.getText().toString(),
                            editSurname.getText().toString(),
                            editGender.getText().toString(),
                            editAge.getText().toString(),
                            "",
                            "",
                            "",
                            "",
                            editPassword.getText().toString(),
                            editQuestion.getText().toString(),
                            editAnswer.getText().toString(),
                            "0",
                            "0",
                            editEmergencyContact.getText().toString(),
                            "False"
                            );

                    if(isInserted){
                        Toast.makeText(CreateUserActivity.this,"Data Inserted", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CreateUserActivity.this,"Data Not Inserted", Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    public void viewAll(){
        btnviewAll.setOnClickListener(
                v -> {

                    Cursor res = myDb.getAllUserData();
                    if (res.getCount() == 0){
                        //show message
                        showMessage("Error", "Nothing Found");
                        return;
                    }

                    StringBuffer buffer = new StringBuffer();
                    while(res.moveToNext()){
                        buffer.append("ID :" + res.getString(0)+ "\n");
                        buffer.append("First Name :" + res.getString(1)+ "\n");
                        buffer.append("Surname Name :" + res.getString(2)+ "\n");
                        buffer.append("Gender :" + res.getString(3)+ "\n");
                        buffer.append("Age :" + res.getString(4)+ "\n");
                        buffer.append("Height :" + res.getString(5)+ "\n");
                        buffer.append("Hair Colour:" + res.getString(6)+ "\n");
                        buffer.append("Weight:" + res.getString(7)+ "\n");
                        buffer.append("Ethnicity:" + res.getString(8)+ "\n");

                        buffer.append("Password :" + res.getString(9)+ "\n");
                        buffer.append("Question :" + res.getString(10)+ "\n");
                        buffer.append("Answer :" + res.getString(11)+ "\n");
                        buffer.append("Distance :" + res.getString(12)+ "\n");
                        buffer.append("Time :" + res.getString(13)+ "\n");
                        buffer.append("Emergency Contact :" + res.getString(14)+ "\n");
                        buffer.append("Alert Level :" + res.getString(15)+ "\n\n");



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

    private void configureBackButton(){
        Button backButton = (Button) findViewById(R.id.buttonCreateUserBack);
        backButton.setOnClickListener(v -> finish());
    }
}

