package com.example.e.eimageselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Administrator on 2018/7/22.
 */

public class PhotoUtils {

    static void deleteIsDeleted(List<Photo> photoList){
        File file = null;
        for (int i = 0; i < photoList.size(); i++) {
            Photo photo = photoList.get(i);
            file = null;
            file = new File(photo.imageFilePath);
            if (!file.exists()) {
                photoList.remove(photo);
                i--;
            }
        }
        System.gc();
    }


    //保存拍照数据
    static void dealWithCameraData(byte[] data, Activity activity) {
        //带缓存区的文件输出流
        BufferedOutputStream fos      = null;
        String               tempStr  = Environment.getExternalStorageDirectory() + File.separator;
        String               fileName = tempStr + System.currentTimeMillis() + ".jpg";
        try {
            //图片临时保存位置
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //定义matrix对象，他是用于对图片的各种后期处理(旋转、移动、放大、缩小)
            Matrix matrix = new Matrix();
            matrix.reset();
            //把图片顺时针旋转90
            matrix.postRotate(90);
            //创建旋转后的图片
            Bitmap bmRet = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //回收原始图片
            bitmap.recycle();
            fos = new BufferedOutputStream(new FileOutputStream(fileName));
            //保存图片数据
            bmRet.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Intent intent = new Intent();
            intent.putExtra("filename", fileName);
            activity.setResult(Conts.CAMERA_CODE, intent);
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //使用bitmap保存拍照数据
    public static void dealWithCameraData(Bitmap bitmap ,String fileName) {
        //带缓存区的文件输出流
        BufferedOutputStream fos      = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(fileName));
            //保存图片数据
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
