package com.example.e.eimageselector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by v_chicunxiang on 2018/7/16.
 */

public class RotateCenterCrop extends BitmapTransformation {
    private final int Rotate;
    private int mWidth = -1;
    private int mHeight = -1;

    public RotateCenterCrop(Context context, int Rotate) {
        super(context);
        this.Rotate = Rotate;
    }

    @Override
    public String getId() {
        return "rotate" + Rotate;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();

        matrix.postRotate(Rotate);
        if (mWidth == -1){
            mWidth = toTransform.getWidth();
        }

        if (mHeight == -1){
            mHeight = toTransform.getHeight();
        }

        return Bitmap.createBitmap(toTransform, 0, 0, mWidth, mHeight, matrix, true);

    }

    public void setWidthAndHeight(int width, int height) {
        this.mWidth = width;
        this.mHeight=height;
    }
}
