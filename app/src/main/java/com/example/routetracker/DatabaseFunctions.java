package com.example.routetracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseFunctions extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NightTravelDatabase.db";

    private static final String User_Table_Name = "User_Table";
    private static final String Route_Table_Name = "Route_Table";

    private static final String User_Table_Name_COL_0 = "ID";
    private static final String User_Table_Name_COL_1 = "FirstName";
    private static final String User_Table_Name_COL_2 = "Surname";
    private static final String User_Table_Name_COL_3 = "Gender";
    private static final String User_Table_Name_COL_4 = "Age";

    private static final String User_Table_Name_COL_5 = "Height";
    private static final String User_Table_Name_COL_6 = "HairColour";
    private static final String User_Table_Name_COL_7 = "Weight";
    private static final String User_Table_Name_COL_8 = "Ethnicity";
    private static final String User_Table_Name_COL_9 = "Password";
    private static final String User_Table_Name_COL_10 = "Question";
    private static final String User_Table_Name_COL_11 = "Answer";

    private static final String User_Table_Name_COL_12 = "Distance";
    private static final String User_Table_Name_COL_13 = "Time";
    private static final String User_Table_Name_COL_14 = "EmergencyContact";
    private static final String User_Table_Name_COL_15 = "AlertLevel";
    private static final String User_Table_Name_COL_16 = "AccelerometersAndGryo";
    private static final String User_Table_Name_COL_17 = "FirstLogin";


    private static final String Route_Table_Name_COL_1 = "UserID";
    private static final String Route_Table_Name_COL_2 = "EndDestination";
    private static final String Route_Table_Name_COL_3 = "Name";
    private static final String Route_Table_Name_COL_4 = "Favourite";

    public DatabaseFunctions(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    //Creates Tables in the Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + User_Table_Name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRSTNAME TEXT,SURNAME TEXT,GENDER TEXT,AGE TEXT, HEIGHT TEXT, HAIRCOLOUR TEXT,WEIGHT TEXT,ETHNICITY TEXT,PASSWORD TEXT, QUESTION TEXT, ANSWER TEXT, DISTANCE INTEGER, TIME INTEGER, EMERGENCYCONTACT TEXT, ALERTLEVEL BOOLEAN, ACCELEROMETERSANDGRYO BOOLEAN, FIRSTLOGIN BOOLEAN)");
        db.execSQL("create table " + Route_Table_Name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER REFERENCES User_Table_Name(ID), ENDDESTINATION INTEGER, NAME TEXT, FAVOURITE INTEGER)");

    }

    //Remakes the database to update it
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + User_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS " + Route_Table_Name);
        onCreate(db);
    }


    //Inserts User Data
    boolean insertDataUser(String First_Name, String Surname, String Gender, String Age, String Height, String HairColour, String Weight, String Ethnicity, String Password, String Question, String Answer, String EmergencyContact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User_Table_Name_COL_1,First_Name);
        contentValues.put(User_Table_Name_COL_2,Surname);
        contentValues.put(User_Table_Name_COL_3,Gender);
        contentValues.put(User_Table_Name_COL_4,Age);
        contentValues.put(User_Table_Name_COL_5,Height);
        contentValues.put(User_Table_Name_COL_6,HairColour);
        contentValues.put(User_Table_Name_COL_7,Weight);
        contentValues.put(User_Table_Name_COL_8,Ethnicity);
        contentValues.put(User_Table_Name_COL_9, Password);
        contentValues.put(User_Table_Name_COL_10, Question);
        contentValues.put(User_Table_Name_COL_11, Answer);
        contentValues.put(User_Table_Name_COL_12, 250);
        contentValues.put(User_Table_Name_COL_13, 100);
        contentValues.put(User_Table_Name_COL_14, EmergencyContact);
        contentValues.put(User_Table_Name_COL_15, 0);
        contentValues.put(User_Table_Name_COL_16, 1);
        contentValues.put(User_Table_Name_COL_17, 1);



        long result = db.insert(User_Table_Name, null, contentValues);

        return result != -1;
    }

    //Returns all User Data
    Cursor getAllUserData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+ User_Table_Name, null);
    }

    Cursor getUserIDOne(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from User_Table where ID = 1", null);
    }


    //Updates User Data (Requires a ID as a reference)
    boolean updateUserData(String First_Name, String Surname, String Gender, String Age, String Height, String HairColour, String Weight, String Ethnicity, String Password, String Question, String Answer, Integer Distance, Integer Time, String EmergencyContact, Integer Alert_Level, Integer AccelerometersAndGryo, Integer FirstLogin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(User_Table_Name_COL_0, "1");
        contentValues.put(User_Table_Name_COL_1,First_Name);
        contentValues.put(User_Table_Name_COL_2,Surname);
        contentValues.put(User_Table_Name_COL_3,Gender);
        contentValues.put(User_Table_Name_COL_4,Age);
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
        contentValues.put(User_Table_Name_COL_16, AccelerometersAndGryo);
        contentValues.put(User_Table_Name_COL_17, FirstLogin);


        db.update(User_Table_Name, contentValues,"ID = ?", new String[] {"1"});
        return true;
    }

    Boolean checkpassword(String Password){
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from User_Table where Password=?", new String[]{Password});
        return cursor.getCount() > 0;
    }

    //Inserts the EndDestination into the RouteTable
    void insertRouteData(String EndDestination, String Name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Route_Table_Name_COL_1, "1");
        contentValues.put(Route_Table_Name_COL_2,EndDestination);
        contentValues.put(Route_Table_Name_COL_3,Name);
        contentValues.put(Route_Table_Name_COL_4, 0);
         db.insert(Route_Table_Name, null, contentValues);
    }

    //Deletes a users data, will delete the entire row(Requires an ID as reference)
    void deleteRouteData(String dest){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Route_Table_Name, "EndDestination = ?", new String[]{dest});
    }

    void favouriteRouteData(String dest){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Route_Table_Name_COL_4,1);
        db.update(Route_Table_Name,contentValues, "EndDestination = ?",new String[] { dest } );

    }

    //Returns all values in the route table
    Cursor getAllRouteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from Route_Table", null);
    }







}
