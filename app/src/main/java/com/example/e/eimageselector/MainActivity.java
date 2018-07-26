package com.example.e.eimageselector;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
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
    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = findViewById(R.id.rv);
        findViewById(R.id.camera).setOnClickListener(this);
        findViewById(R.id.select_image).setOnClickListener(this);
        findViewById(R.id.camera2).setOnClickListener(this);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        mImageAdapter = new ImageAdapter(this, mPhotoList);
        rv.setAdapter(mImageAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filename;
        switch (requestCode) {
            case Conts.CAMERA_CODE:
                if (data == null) {
                    // 防止直接返回没有数据回来的问题
                    return;
                }
                filename = addPhoto(data);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filename))));
                break;
            case Conts.PHOTO_SELECT_CODE:
                if (data == null) {
                    // 防止直接返回没有数据回来的问题
                    return;
                }
                addPhoto(data);
                break;
            case Conts.SYSTEM_CAMERA:
                File file = new File(mFileName);
                if (!file.exists()) {
                    // 防止直接返回没有数据回来的问题
                    return;
                }
                updateView(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                break;
        }
    }

    @NonNull
    private String addPhoto(Intent data) {
        Toast.makeText(MainActivity.this, "收到" + data.getStringExtra("filename"), Toast.LENGTH_SHORT).show();
        String filename = data.getStringExtra("filename");
        File   file     = new File(filename);
        updateView(file);
        return filename;
    }

    private void updateView(File file) {
        Photo photo = new Photo(file.getAbsolutePath(), file.lastModified() + "");
        mPhotoList.add(photo);
        PhotoUtils.deleteIsDeleted(mPhotoList);
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        boolean permission = requestPermission();
        if (!permission) {
            Toast.makeText(MainActivity.this, "请先授权", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.camera:
                intent.setClass(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, Conts.CAMERA_CODE);
                break;
            case R.id.camera2:
                // 系统拍照，需要7.0以上FileProvider
                String tempStr = Environment.getExternalStorageDirectory() + File.separator;
                mFileName = tempStr + System.currentTimeMillis() + ".jpg";
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri mUri;
                if (Build.VERSION.SDK_INT >= 23) {
                    mUri = FileProvider.getUriForFile(this, getPackageName(), new File(mFileName));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    mUri = Uri.fromFile(new File(mFileName));
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(intent, Conts.SYSTEM_CAMERA);
                break;
            case R.id.select_image:
                // 选择图片，需要内容提供者
                intent.setClass(MainActivity.this, ImageSelectorActivity.class);
                startActivityForResult(intent, Conts.PHOTO_SELECT_CODE);
                break;
        }

    }


    private boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
            return false;
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            return false;

        }
        return true;
    }
}
