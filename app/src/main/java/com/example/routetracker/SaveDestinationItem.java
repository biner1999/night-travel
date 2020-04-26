package com.example.routetracker;

public class SaveDestinationItem {

    private int mImageResource;
    private String mText1;
    private String mDestination;
    private String mFavourite;
    private int mFavValue;


    public SaveDestinationItem(int ImageResource, String text1, int favourite, String inDest){
        mImageResource = ImageResource;
        mText1 = text1;
        mDestination = inDest;
        mFavValue = favourite;
        if (favourite == 0){
            mFavourite = "";
        }
        else{
            mFavourite = "Favourite";
        }



    }

    public int getmImageResource(){
        return mImageResource;
    }

    public String getmText1(){
        return mText1;
    }

    public String getmDestination(){
        return mDestination;
    }

    public String getmFavourite(){
        return mFavourite;
    }

    public int getmFavValue(){
        return mFavValue;
    }
}
