/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.example.ts.cameraapi2;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends AppCompatActivity {

    CameraManager mManager;
    //后置
    CameraCharacteristics mBackCharacteristics;
    String mBackCameraId;
    //前置
    CameraCharacteristics mFrontCharacteristics;
    String mFrontCameraId;

    //camera
    CameraDevice mCameraDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            init();
        }
    }

    private void init() {
        //1.camera 入口
        mManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //2.检测是否支持camera2特性
        checkCameraCharacteristics();
    }

    private void checkCameraCharacteristics(){
        try {
            String[] cameraIds = mManager.getCameraIdList();
            Log.e("------camear2----","-----cameraIds----" + cameraIds.length);
            for (String cId : cameraIds) {
                CameraCharacteristics characteristics = mManager.getCameraCharacteristics(cId);
                //判断是否支持camera2（FULL/LEVEL_3），其他类型用camera1
                if (isSupportCamera2Hardware(characteristics)){
                    if (CameraCharacteristics.LENS_FACING_BACK == characteristics.get(CameraCharacteristics.LENS_FACING)){
                        //判断是后置摄像头
                        mBackCharacteristics = characteristics;
                        mBackCameraId = cId;
                    }else if (CameraCharacteristics.LENS_FACING_FRONT == characteristics.get(CameraCharacteristics.LENS_FACING)){
                        //判断是前置摄像头
                        mFrontCharacteristics = characteristics;
                        mFrontCameraId = cId;
                    }
                    //3.开启相机预览
                    openCamera();
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void openCamera(){
        try {
            if (null != mBackCameraId){
                //开启相机预览
                openCamera(mBackCameraId);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private boolean isSupportCamera2Hardware(CameraCharacteristics characteristics) {
        Log.e("------camear2----","-----cameraId level---" + characteristics.get(CameraCharacteristics
                .INFO_SUPPORTED_HARDWARE_LEVEL));
        Toast.makeText(Camera2Activity.this,"not support",Toast.LENGTH_LONG).show();
        return characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
                || characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3;
    }

    private void openCamera(String cameraId) throws CameraAccessException {
        mManager.openCamera(cameraId, new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCameraDevice = camera;
                Toast.makeText(Camera2Activity.this,"opened",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {

            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                camera.close();
            }

            @Override
            public void onClosed(@NonNull CameraDevice camera) {
                super.onClosed(camera);
                Log.e("", "camera was closed");
            }
        },null);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //4.关闭相机
        if (null != mCameraDevice){
            mCameraDevice.close();
        }
    }
}
