package com.example.routetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseFunctions extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "StorageAgain.db";
    public static final String User_Table_Name = "User_Table";
    public static final String Route_Table_Name = "Route_Table";

    public static final String User_Table_Name_COL_1 = "ID";
    public static final String User_Table_Name_COL_2 = "FirstName";
    public static final String User_Table_Name_COL_3 = "Surname";
    public static final String User_Table_Name_COL_4 = "Gender";
    public static final String User_Table_Name_COL_5 = "Height";
    public static final String User_Table_Name_COL_6 = "HairColour";
    public static final String User_Table_Name_COL_7 = "Weight";
    public static final String User_Table_Name_COL_8 = "Ethnicity";
    public static final String User_Table_Name_COL_9 = "Password";
    public static final String User_Table_Name_COL_10 = "Question";
    public static final String User_Table_Name_COL_11 = "Answer";

    public static final String User_Table_Name_COL_12 = "Distance";
    public static final String User_Table_Name_COL_13 = "Time";
    public static final String User_Table_Name_COL_14 = "EmergencyContact";
    public static final String User_Table_Name_COL_15 = "AlertLevel";




    public static final String Route_Table_Name_COL_1 = "ID";
    public static final String Route_Table_Name_COL_2 = "UserID";
    public static final String Route_Table_Name_COL_3 = "EndDestination";




    public DatabaseFunctions(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + User_Table_Name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRSTNAME TEXT,SURNAME TEXT,GENDER TEXT, HEIGHT TEXT, HAIRCOLOUR TEXT,WEIGHT TEXT,ETHNICITY TEXT,PASSWORD TEXT, QUESTION TEXT, ANSWER TEXT, DISTANCE INTEGER, TIME INTEGER, EMERGENCYCONTACT BOOLEAN, ALERTLEVEL STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + User_Table_Name);
        onCreate(db);
    }

    public boolean insertDataUser(String First_Name,String Surname,String Gender, String Height, String HairColour,String Weight, String Ethnicity,String Password, String Question, String Answer, String Distance, String Time, String EmergencyContact, String Alert_Level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User_Table_Name_COL_2,First_Name);
        contentValues.put(User_Table_Name_COL_3,Surname);
        contentValues.put(User_Table_Name_COL_4,Gender);
        contentValues.put(User_Table_Name_COL_5,Height);
        contentValues.put(User_Table_Name_COL_6,HairColour);
        contentValues.put(User_Table_Name_COL_7,Weight);
        contentValues.put(User_Table_Name_COL_8,Ethnicity);
        contentValues.put(User_Table_Name_COL_9, Password);
        contentValues.put(User_Table_Name_COL_10, Question);
        contentValues.put(User_Table_Name_COL_11, Answer);
        contentValues.put(User_Table_Name_COL_12, Distance);
        contentValues.put(User_Table_Name_COL_13, Time);
        contentValues.put(User_Table_Name_COL_14, EmergencyContact);
        contentValues.put(User_Table_Name_COL_15, Alert_Level);


        long result = db.insert(User_Table_Name, null, contentValues);

        if (result == -1 ){
            return false;
        }else{
            return true;
        }
    }


    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ User_Table_Name, null);
        return res;
    }

    public boolean updateData(String id, String First_Name,String Surname,String Gender, String Height, String HairColour,String Weight, String Ethnicity,String Password, String Question, String Answer, String Distance, String Time, String EmergencyContact, String Alert_Level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(User_Table_Name_COL_1,id);
        contentValues.put(User_Table_Name_COL_2,First_Name);
        contentValues.put(User_Table_Name_COL_3,Surname);
        contentValues.put(User_Table_Name_COL_4,Gender);
        contentValues.put(User_Table_Name_COL_5,Height);
        contentValues.put(User_Table_Name_COL_6,HairColour);
        contentValues.put(User_Table_Name_COL_7,Weight);
        contentValues.put(User_Table_Name_COL_8,Ethnicity);
        contentValues.put(User_Table_Name_COL_9, Password);
        contentValues.put(User_Table_Name_COL_10, Question);
        contentValues.put(User_Table_Name_COL_11, Answer);
        contentValues.put(User_Table_Name_COL_12, Distance);
        contentValues.put(User_Table_Name_COL_13, Time);
        contentValues.put(User_Table_Name_COL_14, EmergencyContact);
        contentValues.put(User_Table_Name_COL_15, Alert_Level);

        db.update(User_Table_Name, contentValues,"ID = ?", new String[] { id });
        return true;
    }

    public Integer deleteUserData(String id){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(User_Table_Name, "ID = ?",new String[] { id } );
    }



}
