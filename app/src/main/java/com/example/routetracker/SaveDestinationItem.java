package com.example.routetracker;

class SaveDestinationItem {

    private int mImageResource;
    private String mText1;
    private String mDestination;
    private String mFavourite;
    private int mFavValue;


    SaveDestinationItem(int ImageResource, String text1, int favourite, String inDest){
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

    int getmImageResource(){
        return mImageResource;
    }

    String getmText1(){
        return mText1;
    }

    String getmDestination(){
        return mDestination;
    }

    String getmFavourite(){
        return mFavourite;
    }

    int getmFavValue(){
        return mFavValue;
    }
}
