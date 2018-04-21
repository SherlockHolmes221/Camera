package com.quxian.camera.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.quxian.camera.R;

public class DefineCamera extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUESTCODE = 100;
    private static PermissionListener mListener;
    private TextView textView;
    private Camera camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_camera);

       camera = getCamera();

        findViewById(R.id.define_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(DefineCamera.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DefineCamera.this, "请先打开相机权限", Toast.LENGTH_SHORT).show();
                }else
                    startActivity(new Intent(DefineCamera.this, CustomCamera.class));

            }
        });

        findViewById(R.id.request_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //requestPermission();
                permission();
            }
        });
        permission();

        textView = (TextView) findViewById(R.id.define_text);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        Camera.Parameters parameters = camera.getParameters();
        appendText(textView,parameters.flatten());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage("申请相机权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //申请相机权限
                                ActivityCompat.requestPermissions(DefineCamera.this,
                                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }
                        })
                        .show();
//            } else {
//                //申请相机权限
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//            }
        }else
            Toast.makeText(this, "获取了相机权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "相机权限已申请", Toast.LENGTH_SHORT).show();
            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //动态权限
    public void requestRunPermission(PermissionListener listener) {
        mListener = listener;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //没有授权
            mListener.onDenied();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUESTCODE);
        } else {
            //已经授权
            mListener.onGranted();
        }
    }

    private void permission() {
        requestRunPermission(new PermissionListener() {
            @Override
            public void onGranted() {
                Toast.makeText(DefineCamera.this, "相机权限已申请", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied() {
                Toast.makeText(DefineCamera.this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //打印相机参数
    public TextView appendText(TextView tv, String str) {
        str = add(str,";","\n");
        //if (str.equals("")) return tv;
        CharSequence t = tv.getText();
//        if (t.length() > 50) t = "Log cleared.";
        tv.setText(t + "\n" + str);
        return tv;
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

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    //释放相机对象
    private void releaseCamera(){
        if(camera != null){
            camera.release();
            camera = null;
        }
    }

    public static String add(String s,String org,String ob) {
        String  newString="";
        int  first=0;
        while(s.indexOf(org)!=-1) {
            first=s.indexOf(org);
            if(first!=s.length()) {
                newString=newString+s.substring(0,first+1)+ob;
                s=s.substring(first+org.length(),s.length());
            }
        }
        newString=newString+s;
        return   newString;
    }
}
