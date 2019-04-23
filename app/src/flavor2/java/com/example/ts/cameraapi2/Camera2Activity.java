/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.example.ts.cameraapi2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    //获得屏幕宽高
    int windowHeigth;
    int windowWidth;

    //预览画面配置
    Size previewSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm2);
        //获取屏幕的默认分辨率
        DisplayMetrics display = getResources().getDisplayMetrics();
        windowHeigth = display.heightPixels;
        windowWidth = display.widthPixels;

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

    private void createSurface(final CameraCharacteristics characteristics) {

        //创建预览画面
        TextureView textureView = findViewById(R.id.camera_preview);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                previewSize = getPreviewSize(characteristics);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                surface.setDefaultBufferSize(previewSize.getWidth(),previewSize.getHeight());
                final Surface previewSurface = new Surface(surface);
                List<Surface> outputs = new ArrayList<>();
                outputs.add(previewSurface);
                try {
                    //创建 CaptureRequest 对象
                    mCameraDevice.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            CaptureRequest.Builder requestBuilder = null;
                            try {
                                requestBuilder = mCameraDevice.createCaptureRequest(CameraDevice
                                        .TEMPLATE_PREVIEW);
                                requestBuilder.addTarget(previewSurface);
                                CaptureRequest captureRequest = requestBuilder.build();
                                session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                                    @Override
                                    public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                        super.onCaptureStarted(session, request, timestamp, frameNumber);
                                    }

                                    @Override
                                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                        super.onCaptureCompleted(session, request, result);

//                                        session.stopRepeating();
                                    }
                                    @Override
                                    public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                                        super.onCaptureFailed(session, request, failure);
                                    }
                                },null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    },null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    openCamera(characteristics);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void openCamera(CameraCharacteristics characteristics){
        try {
            if (null != mBackCameraId){
                //开启相机预览
                openCamera(mBackCameraId,characteristics);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private boolean isSupportCamera2Hardware(CameraCharacteristics characteristics) {
        Log.e("------camear2----","-----cameraId level---" + characteristics.get(CameraCharacteristics
                .INFO_SUPPORTED_HARDWARE_LEVEL));
        boolean support = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
                || characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3;
        Toast.makeText(Camera2Activity.this,"is support " + support,Toast.LENGTH_LONG).show();
        return support;
    }

    private void openCamera(String cameraId, final CameraCharacteristics characteristics) throws CameraAccessException {
        mManager.openCamera(cameraId, new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCameraDevice = camera;
                createSurface(characteristics);
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

    private Size getPreviewSize(CameraCharacteristics characteristics){
        float rate = windowHeigth / windowWidth;
        //return preview size,获取预览尺寸。
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
        if (null != sizes){
            for (Size size : sizes){
                if (isGoodSize(size,rate)){
                    return size;
                }
            }
        }
        return null;
    }

    private boolean isGoodSize(Size size, float rate){
        return size.getHeight() / size.getWidth() == rate
                && size.getHeight() <= windowHeigth && size.getWidth() <= windowWidth;
    }
}
