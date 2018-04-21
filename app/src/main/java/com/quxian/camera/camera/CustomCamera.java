package com.quxian.camera.camera;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.quxian.camera.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by quxia on 2018/4/15.
 * 自定义系统相机参数
 */

public class CustomCamera extends AppCompatActivity implements SurfaceHolder.Callback{

    private  Camera camera;

    private ImageButton imageViewCapture;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageButton imageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);


        //拍照按钮
        imageViewCapture = (ImageButton) findViewById(R.id.custom_btn_capture);
        imageViewCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture(view);
            }
        });
        surfaceView = (SurfaceView)findViewById(R.id.custom_sf_show);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        //点击屏幕自动对焦拍摄
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture(view);
            }
        });
    }

    //参数的设置
    public void capture(View view){
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPictureSize(800,400);
        parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if(b){
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes, Camera camera) {
                            //指定图片存放的位置
                            String filePath = Environment.getExternalStorageDirectory().getPath();
                            filePath = filePath + "/" + "temp.png";
                            File file = new File(filePath);
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                try {
                                    fileOutputStream.write(bytes);
                                    fileOutputStream.close();

                                    //显示拍照结果
                                    Intent intent = new Intent(CustomCamera.this,ResultActivity.class);
                                    intent.putExtra("photoPath",file.getAbsolutePath());
                                    startActivity(intent);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    //获取camera对象
    private Camera getCamera(){
        Camera camera;
        try{
            camera = Camera.open();
        }catch (Exception e){
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    //预览相机内容
    private void previewPhoto(Camera camera,SurfaceHolder surfaceHolder){

        try {
          if(surfaceHolder != null){
              camera.setPreviewDisplay(surfaceHolder);
              camera.setDisplayOrientation(90);
              camera.startPreview();
          }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //释放相机对象
    private void releaseCamera(){
        if(camera != null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(camera == null){
            camera = getCamera();
            if(surfaceHolder != null){
               previewPhoto(camera,surfaceHolder);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null){
            camera = getCamera();
            if(surfaceHolder != null){
                previewPhoto(camera,surfaceHolder);
            }
        }else if(surfaceHolder != null){
            previewPhoto(camera,surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {

        camera.stopPreview();
        previewPhoto(camera,surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
