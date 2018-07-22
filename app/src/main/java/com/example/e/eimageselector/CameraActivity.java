package com.example.e.eimageselector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
        parameters.setPreviewSize(480,720);
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
        //带缓存区的文件输出流
        BufferedOutputStream fos = null;
        String           tempStr = Environment.getExternalStorageDirectory() + File.separator;
        String fileName = tempStr + System.currentTimeMillis() + ".jpg";
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
            Camera.Parameters parameters = camera.getParameters();
            //设置相机的分辨率,不是随便设置的,先得到相机支持的分辨率
            List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
            if (sizeList.size() > 1) {//在相机支持多种分辨率的情况下，就要判断相机的分辨率在集合中是从小排列还是从大排列
                if (sizeList.get(0).width * sizeList.get(0).height > sizeList.get(sizeList.size() - 1).width * sizeList.get(sizeList.size() - 1).height) {
                    //从大到小排列
                    parameters.setPictureSize(sizeList.get(0).width, sizeList.get(0).height);
                } else {
                    parameters.setPictureSize(sizeList.get(sizeList.size() - 1).width, sizeList.get(sizeList.size() - 1).height);
                }
            } else {//只有一种分辨率
                parameters.setPictureSize(sizeList.get(0).width, sizeList.get(0).height);

            }
            parameters.setPictureFormat(ImageFormat.JPEG);
            camera.setParameters(parameters);
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
