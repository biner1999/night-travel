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

    public static final String User_Table_Name_COL_0 = "ID";
    public static final String User_Table_Name_COL_1 = "FirstName";
    public static final String User_Table_Name_COL_2 = "Surname";
    public static final String User_Table_Name_COL_3 = "Gender";
    public static final String User_Table_Name_COL_4 = "Age";

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
    public static final String User_Table_Name_COL_16 = "AccelerometersAndGryo";
    public static final String User_Table_Name_COL_17 = "FirstLogin";


    public static final String Route_Table_Name_COL_0 = "ID";
    public static final String Route_Table_Name_COL_1 = "UserID";
    public static final String Route_Table_Name_COL_2 = "EndDestination";
    public static final String Route_Table_Name_COL_3 = "Name";
    public static final String Route_Table_Name_COL_4 = "Favourite";

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
    public boolean insertDataUser(String First_Name,String Surname,String Gender,String Age, String Height, String HairColour,String Weight, String Ethnicity,String Password, String Question, String Answer, Integer Distance, Integer Time, String EmergencyContact, Integer Alert_Level, Integer AccelerometersAndGryo, Integer FirstLogin){
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
        contentValues.put(User_Table_Name_COL_12, Distance);
        contentValues.put(User_Table_Name_COL_13, Time);
        contentValues.put(User_Table_Name_COL_14, EmergencyContact);
        contentValues.put(User_Table_Name_COL_15, Alert_Level);
        contentValues.put(User_Table_Name_COL_16, AccelerometersAndGryo);
        contentValues.put(User_Table_Name_COL_17, FirstLogin);



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

    public Cursor getUserIDOne(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from User_Table where ID = 1", null);
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
        contentValues.put(User_Table_Name_COL_0,id);
        contentValues.put(User_Table_Name_COL_15,AlertLevel);

        db.update(User_Table_Name, contentValues,"ID = ?", new String[] { id });
        return true;
    }

    //Updates User Data (Requires a ID as a reference)
    public boolean updateUserData(String id,  String First_Name,String Surname,String Gender,String Age, String Height, String HairColour,String Weight, String Ethnicity,String Password, String Question, String Answer, Integer Distance, Integer Time, String EmergencyContact, Integer Alert_Level, Integer AccelerometersAndGryo, Integer FirstLogin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(User_Table_Name_COL_0,id);
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
    public boolean insertRouteData(String UserID, String EndDestination, String Name, int Favourite){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Route_Table_Name_COL_1,UserID);
        contentValues.put(Route_Table_Name_COL_2,EndDestination);
        contentValues.put(Route_Table_Name_COL_3,Name);
        contentValues.put(Route_Table_Name_COL_4,Favourite);
        long result = db.insert(Route_Table_Name, null, contentValues);

        if (result == -1 ){
            return false;
        }else{
            return true;
        }
    }

    //Deletes a users data, will delete the entire row(Requires an ID as reference)
    public Integer deleteRouteData(String dest){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Route_Table_Name, "EndDestination = ?",new String[] { dest } );
    }

    public boolean favouriteRouteData(String dest){
        System.out.println("111111111111111    "+dest);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Route_Table_Name_COL_4,1);
        db.update(Route_Table_Name,contentValues, "EndDestination = ?",new String[] { dest } );

        return true;
    }

    //Returns all values in the route table
    public Cursor getAllRouteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from Route_Table", null);
        return res;
    }

    //Returns a User's End Destination?
    public Cursor getUserRouteData(String UserID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ENDDESTINATION from "+ Route_Table_Name + "where UserID = " + UserID, null);
        return res;
    }





}
