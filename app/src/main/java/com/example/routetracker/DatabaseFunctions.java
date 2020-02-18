package com.example.routetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseFunctions extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Storage.db";
    public static final String User_Table_Name = "User_Table";
    public static final String Route_Table_Name = "Route_Table";

    public static final String User_Table_Name_COL_1 = "ID";
    public static final String User_Table_Name_COL_2 = "Name";
    public static final String User_Table_Name_COL_3 = "Password";
    public static final String User_Table_Name_COL_4 = "Question";
    public static final String User_Table_Name_COL_5 = "Answer";
    public static final String User_Table_Name_COL_6 = "Distance";
    public static final String User_Table_Name_COL_7 = "Time";


    public static final String Route_Table_Name_COL_1 = "ID";
    public static final String Route_Table_Name_COL_2 = "UserID";
    public static final String Route_Table_Name_COL_3 = "EndDestination";




    public DatabaseFunctions(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + User_Table_Name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,PASSWORD TEXT, QUESTION TEXT, ANSWER TEXT, DISTANCE INTEGER, TIME INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + User_Table_Name);
        onCreate(db);
    }

    public boolean insertDataUser(String Name,String Password, String Question, String Answer, String Distance, String Time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User_Table_Name_COL_2,Name);
        contentValues.put(User_Table_Name_COL_3, Password);
        contentValues.put(User_Table_Name_COL_4, Question);
        contentValues.put(User_Table_Name_COL_5, Answer);
        contentValues.put(User_Table_Name_COL_6, Distance);
        contentValues.put(User_Table_Name_COL_7, Time);

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

    public boolean updateData(String id, String Name,String Password, String Question, String Answer, String Distance, String Time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(User_Table_Name_COL_1,id);
        contentValues.put(User_Table_Name_COL_2,Name);
        contentValues.put(User_Table_Name_COL_3, Password);
        contentValues.put(User_Table_Name_COL_4, Question);
        contentValues.put(User_Table_Name_COL_5, Answer);
        contentValues.put(User_Table_Name_COL_6, Distance);
        contentValues.put(User_Table_Name_COL_7, Time);
        db.update(User_Table_Name, contentValues,"ID = ?", new String[] { id });
        return true;
    }

    public Integer deleteUserData(String id){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(User_Table_Name, "ID = ?",new String[] { id } );
    }



}
