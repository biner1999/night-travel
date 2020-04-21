package com.example.routetracker;

public class SaveDestinationItem {

    private int mImageResource;
    private String mText1;
    private String mText2;
    private String mDestination;


    public SaveDestinationItem(int ImageResource, String text1, String text2, String inDest){
        mImageResource = ImageResource;
        mText1 = text1;
        mText2 = text2;
        mDestination = inDest;


    }

    public int getmImageResource(){
        return mImageResource;
    }

    public String getmText1(){
        return mText1;
    }

    public String getmText2(){
        return mText2;
    }

    public String getmDestination(){return mDestination;}
}
