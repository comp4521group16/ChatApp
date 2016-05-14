package com.example.kalongip.chatapp.Value;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;

/**
 * Created by timothy on 14/5/2016.
 */
public class BitmapRotate {
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

}
