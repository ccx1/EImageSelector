package com.example.e.eimageselector;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageAdapter mImageAdapter;
    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        findViewById(R.id.camera).setOnClickListener(this);
        findViewById(R.id.select_image).setOnClickListener(this);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        mImageAdapter = new ImageAdapter(this, mPhotoList, 0);
        rv.setAdapter(mImageAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filename;
        File   file;
        Photo  photo;
        switch (resultCode) {

            case Conts.CAMERA_CODE:

                Toast.makeText(MainActivity.this, "收到" + data.getStringExtra("filename"), Toast.LENGTH_SHORT).show();
                filename = data.getStringExtra("filename");
                file = new File(filename);
                photo = new Photo(filename, file.lastModified() + "");
                mPhotoList.add(photo);
                mImageAdapter.notifyDataSetChanged();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));
                break;
            case Conts.PHOTO_SELECT_CODE:

                Toast.makeText(MainActivity.this, "收到" + data.getStringExtra("filename"), Toast.LENGTH_SHORT).show();
                filename = data.getStringExtra("filename");
                file = new File(filename);
                photo = new Photo(filename, file.lastModified() + "");
                mPhotoList.add(photo);
                mImageAdapter.notifyDataSetChanged();

                break;


        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.camera:
                intent.setClass(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, Conts.CAMERA_CODE);
                break;
            case R.id.select_image:
                // 选择图片，需要内容提供者
                intent.setClass(MainActivity.this, ImageSelectorActivity.class);
                startActivityForResult(intent, Conts.PHOTO_SELECT_CODE);
                break;
        }

    }
}
