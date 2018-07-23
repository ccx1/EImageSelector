package com.example.e.eimageselector;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class ImageSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);
        List<Photo> list = new ArrayList<>();
        TreeSet<Photo> objects = new TreeSet<>(new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                return (int) (Long.valueOf(o2.createDate) - Long.valueOf(o1.createDate));
            }
        });
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取图片的路径，但是是byte数组的
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片的详细信息
            String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            objects.add(new Photo(new String(data,0,data.length-1),desc));
        }
        list.addAll(objects);
        RecyclerView viewById = (RecyclerView) findViewById(R.id.rv);
        viewById.setLayoutManager(new GridLayoutManager(this,3));
        ImageAdapter imageAdapter = new ImageAdapter(ImageSelectorActivity.this,list);
        viewById.setAdapter(imageAdapter);
        imageAdapter.setOnLongClick(new ImageAdapter.onLongClick() {
            @Override
            public void longClick(Photo photo) {
                Intent intent = new Intent();
                intent.putExtra("filename",  photo.imageFilePath);
                setResult(Conts.PHOTO_SELECT_CODE,intent);
                ImageSelectorActivity.this.finish();
            }
        });

    }
}
