package com.example.routetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseFunctions extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "StorageAgainAgain.db";

    public static final String User_Table_Name = "User_Table";
    public static final String Route_Table_Name = "Route_Table";

    public static final String User_Table_Name_COL_1 = "ID";
    public static final String User_Table_Name_COL_2 = "FirstName";
    public static final String User_Table_Name_COL_3 = "Surname";
    public static final String User_Table_Name_COL_4 = "Gender";
    public static final String User_Table_Name_COL_5 = "Age";

    public static final String User_Table_Name_COL_6 = "Height";
    public static final String User_Table_Name_COL_7 = "HairColour";
    public static final String User_Table_Name_COL_8 = "Weight";
    public static final String User_Table_Name_COL_9 = "Ethnicity";
    public static final String User_Table_Name_COL_10 = "Password";
    public static final String User_Table_Name_COL_11 = "Question";
    public static final String User_Table_Name_COL_12 = "Answer";

    public static final String User_Table_Name_COL_13 = "Distance";
    public static final String User_Table_Name_COL_14 = "Time";
    public static final String User_Table_Name_COL_15 = "EmergencyContact";
    public static final String User_Table_Name_COL_16 = "AlertLevel";

    public static final String Route_Table_Name_COL_1 = "ID";
    public static final String Route_Table_Name_COL_2 = "UserID";
    public static final String Route_Table_Name_COL_3 = "EndDestination";



    public DatabaseFunctions(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    //Creates Tables in the Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + User_Table_Name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRSTNAME TEXT,SURNAME TEXT,GENDER TEXT,AGE TEXT, HEIGHT TEXT, HAIRCOLOUR TEXT,WEIGHT TEXT,ETHNICITY TEXT,PASSWORD TEXT, QUESTION TEXT, ANSWER TEXT, DISTANCE INTEGER, TIME INTEGER, EMERGENCYCONTACT BOOLEAN, ALERTLEVEL STRING)");
        db.execSQL("create table " + Route_Table_Name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER REFERENCES User_Table_Name(ID), ENDDESTINATION INTEGER)");

    }


    //Remakes the database to update it
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + User_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS " + Route_Table_Name);

        onCreate(db);
    }


    //Inserts User Data
    public boolean insertDataUser(String First_Name,String Surname,String Gender,String Age, String Height, String HairColour,String Weight, String Ethnicity,String Password, String Question, String Answer, String Distance, String Time, String EmergencyContact, String Alert_Level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User_Table_Name_COL_2,First_Name);
        contentValues.put(User_Table_Name_COL_3,Surname);
        contentValues.put(User_Table_Name_COL_4,Gender);
        contentValues.put(User_Table_Name_COL_5,Age);
        contentValues.put(User_Table_Name_COL_6,Height);
        contentValues.put(User_Table_Name_COL_7,HairColour);
        contentValues.put(User_Table_Name_COL_8,Weight);
        contentValues.put(User_Table_Name_COL_9,Ethnicity);
        contentValues.put(User_Table_Name_COL_10, Password);
        contentValues.put(User_Table_Name_COL_11, Question);
        contentValues.put(User_Table_Name_COL_12, Answer);
        contentValues.put(User_Table_Name_COL_13, Distance);
        contentValues.put(User_Table_Name_COL_14, Time);
        contentValues.put(User_Table_Name_COL_15, EmergencyContact);
        contentValues.put(User_Table_Name_COL_16, Alert_Level);


        long result = db.insert(User_Table_Name, null, contentValues);

        if (result == -1 ){
            return false;
        }else{
            return true;
        }
    }


    //Returns all User Data
    public Cursor getAllUserData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ User_Table_Name, null);
        return res;
    }

    //Automatically Creates The Database
    public void autoCreateDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();

    }

    //Updates the AlertLevel in UserTable (Requires an ID as reference)
    public boolean updateAlertLevel(String id, String AlertLevel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User_Table_Name_COL_1,id);
        contentValues.put(User_Table_Name_COL_16,AlertLevel);

        db.update(User_Table_Name, contentValues,"ID = ?", new String[] { id });
        return true;
    }

    //Updates User Data (Requires a ID as a reference)
    public boolean updateUserData(String id, String First_Name, String Surname, String Gender, String Age, String Height, String HairColour, String Weight, String Ethnicity, String Password, String Question, String Answer, String Distance, String Time, String EmergencyContact, String Alert_Level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(User_Table_Name_COL_1,id);
        contentValues.put(User_Table_Name_COL_2,First_Name);
        contentValues.put(User_Table_Name_COL_3,Surname);
        contentValues.put(User_Table_Name_COL_4,Gender);
        contentValues.put(User_Table_Name_COL_5,Age);
        contentValues.put(User_Table_Name_COL_6,Height);
        contentValues.put(User_Table_Name_COL_7,HairColour);
        contentValues.put(User_Table_Name_COL_8,Weight);
        contentValues.put(User_Table_Name_COL_9,Ethnicity);
        contentValues.put(User_Table_Name_COL_10, Password);
        contentValues.put(User_Table_Name_COL_11, Question);
        contentValues.put(User_Table_Name_COL_12, Answer);
        contentValues.put(User_Table_Name_COL_13, Distance);
        contentValues.put(User_Table_Name_COL_14, Time);
        contentValues.put(User_Table_Name_COL_15, EmergencyContact);
        contentValues.put(User_Table_Name_COL_16, Alert_Level);

        db.update(User_Table_Name, contentValues,"ID = ?", new String[] { id });
        return true;
    }

    public Boolean checkpassword(String Password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from User_Table where Password=?", new String[]{Password});
        if(cursor.getCount()>0) return true;
        else return false;
    }

    //Deletes a users data, will delete the entire row(Requires an ID as reference)
    public Integer deleteUserData(String id){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(User_Table_Name, "ID = ?",new String[] { id } );
    }

    //Inserts the EndDestination into the RouteTable
    public boolean insertRouteData(String UserID, String EndDestination){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Route_Table_Name_COL_2,UserID);
        contentValues.put(Route_Table_Name_COL_3,EndDestination);



        long result = db.insert(Route_Table_Name, null, contentValues);

        if (result == -1 ){
            return false;
        }else{
            return true;
        }
    }

    //Returns all values in the route table
    public Cursor getAllRouteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ Route_Table_Name, null);
        return res;
    }

    //Returns a User's
    public Cursor getUserRouteData(String UserID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ENDDESTINATION from "+ Route_Table_Name + "where UserID = " + UserID, null);
        return res;
    }



}
