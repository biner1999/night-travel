package com.example.routetracker;

public class SaveDestinationItem {

    private int mImageResource;
    private String mText1;
    private String mText2;

    public SaveDestinationItem(int ImageResource, String text1, String text2){
        mImageResource = ImageResource;
        mText1 = text1;
        mText2 = text2;
    }

    public void changeImage(int image){
        mImageResource = image;
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
}
