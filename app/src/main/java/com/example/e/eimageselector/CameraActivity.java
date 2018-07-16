package com.example.e.eimageselector;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SurfaceView sfv = (SurfaceView) findViewById(R.id.sfv);

        findViewById(R.id.click_camera).setOnClickListener(this);
        sfv.setOnClickListener(this);
        SurfaceHolder holder = sfv.getHolder();
        holder.addCallback(this);
        getSupportActionBar().hide();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null){
            mCamera = Camera.open();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sfv:
                if (mCamera!= null) {
                    mCamera.autoFocus(null);
                }
                break;
            case R.id.click_camera:
                startTakephoto();
                break;
        }

    }


    private void startTakephoto() {
        //获取到相机参数
        Camera.Parameters parameters = mCamera.getParameters();
        //设置图片保存格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //设置图片大小
        parameters.setPreviewSize(480, 720);
        //设置对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //设置自动对焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            dealWithCameraData(data);
                        }
                    });
                }
            }
        });
    }


    //保存拍照数据
    private void dealWithCameraData(byte[] data) {
        FileOutputStream fos     = null;
        String           tempStr = Environment.getExternalStorageDirectory() + File.separator;
        //图片临时保存位置
        String fileName = tempStr + System.currentTimeMillis() + ".jpg";
        try {
            fos = new FileOutputStream(fileName);
            //保存图片数据
            fos.write(data);
            fos.close();
            Intent intent = new Intent();
            intent.putExtra("filename",fileName);
            setResult(Conts.CAMERA_CODE,intent);
            CameraActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 当surfaceview创建的时候
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        previceCamera(mCamera, holder);
    }

    // 当内容改变的时候
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        previceCamera(mCamera, holder);
    }

    // 当销毁的时候
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放相机资源
            mCamera.release();
            mCamera = null;
        }
    }

    private void previceCamera(Camera camera, SurfaceHolder holder) {
        try {
            //摄像头设置SurfaceHolder对象，把摄像头与SurfaceHolder进行绑定
            camera.setPreviewDisplay(holder);
            //调整系统相机拍照角度
            camera.setDisplayOrientation(90);
            //调用相机预览功能
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放相机资源
            mCamera.release();
            mCamera = null;
        }
    }
}
