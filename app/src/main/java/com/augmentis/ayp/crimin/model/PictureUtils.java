package com.augmentis.ayp.crimin.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Nutdanai on 8/4/2016.
 */
public class PictureUtils {
    public static Bitmap getScaledBitmap(String path , int destWidth, int destHeight){
        //Read the dimension f the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //return null and put meta data(information about the bitmap)
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
         int inSampleSize = 1;
        if(srcHeight>destHeight||srcWidth>destWidth){
            if(srcWidth>srcHeight){
                inSampleSize = Math.round(srcHeight/destHeight);

            }else{
                inSampleSize = Math.round(srcWidth/destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return  BitmapFactory.decodeFile(path,options);
    }

    public static Bitmap getScaledBitmap(String path , Activity activity){
            Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path,size.x,size.y);
    }



}
