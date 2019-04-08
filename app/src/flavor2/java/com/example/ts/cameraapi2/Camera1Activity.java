/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.example.ts.cameraapi2;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Camera1Activity extends AppCompatActivity {

    Camera mCamera;
    Camera.CameraInfo mCameraBackInfo = null;
    Camera.CameraInfo mCameraFrontInfo = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm);
        getCameraInfo();
        openCamera();
    }

    private void getCameraInfo(){
        int cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i< cameraCount;i++){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i,cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                mCameraBackInfo = cameraInfo;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                mCameraFrontInfo = cameraInfo;
            }
        }
    }

    private void openCamera() {
        if (mCamera != null){
            return;
        }
        if (mCameraFrontInfo != null){
            mCamera = Camera.open(mCameraFrontInfo.facing);
        }else {
            mCamera = Camera.open(mCameraBackInfo.facing);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }
}
