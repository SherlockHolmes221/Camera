package com.quxian.camera.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.quxian.camera.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button buttonSave;
    private Button buttonDefine;

    private ImageView imageView;
    private ImageView imageViewSave;

    private String filePath;

    private static int REQUEST_CODE = 1;
    private static int REQUEST_CODE_SAVE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.main_btn_open);
        buttonSave =  (Button) findViewById(R.id.main_btn_save);
        buttonSave = (Button) findViewById(R.id.main_btn_define);

        imageView = (ImageView)findViewById(R.id.main_iv_show);
        imageViewSave = (ImageView)findViewById(R.id.main_iv_show_save);

        //指定图片存放的位置
        filePath = Environment.getExternalStorageDirectory().getPath();
        filePath = filePath + "/" + "temp.png";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraAndSave();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,DefineCamera.class));
            }
        });

    }

    //返回缩略图
    public void startCamera(){
        //打开系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CODE);
    }

    //返回高清图
    public void startCameraAndSave(){
        //打开系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = Uri.fromFile(new File(filePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        startActivityForResult(intent,REQUEST_CODE_SAVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //获取相片,data返回的是缩略图，返回的图片像素低
        if(resultCode ==  RESULT_OK && requestCode == REQUEST_CODE){
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);

        }

        //直接读取文件
        else if(resultCode ==  RESULT_OK && requestCode == REQUEST_CODE_SAVE){
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                imageViewSave.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
