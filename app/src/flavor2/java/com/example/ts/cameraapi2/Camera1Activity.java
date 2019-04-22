/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.example.ts.cameraapi2;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.OrientationListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.util.List;

public class Camera1Activity extends AppCompatActivity {

    SurfaceView mSurfaceView;
    Camera camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm);
        initView();
    }

    private void initView() {
        mSurfaceView = findViewById(R.id.surfaceview);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new TakePictureView());

        findViewById(R.id.take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Log.e("onPictureTaken", "---------data------" + data);
                    }
                });
            }
        });

        OrientationEventListener listener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                Log.e("onPictureTaken", rotation + "---------orientation------" + orientation);
            }
        };
        listener.enable();
    }

    public class TakePictureView implements SurfaceHolder.Callback {

        Camera.CameraInfo mCameraBackInfo = null;
        Camera.CameraInfo mCameraFrontInfo = null;

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            getCameraInfo();
            openCamera();
            try {
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

                for (Camera.Size s : sizes) {
                    Log.e("------------" + s.width, "------" + s.height);
                }
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }

        private void getCameraInfo() {
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCameraBackInfo = cameraInfo;
                } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraFrontInfo = cameraInfo;
                }
            }
        }

        private void openCamera() {
            if (camera != null) {
                return;
            }
            if (mCameraFrontInfo != null) {
                camera = Camera.open(mCameraFrontInfo.facing);
            } else {
                camera = Camera.open(mCameraBackInfo.facing);
            }
        }
    }
}
