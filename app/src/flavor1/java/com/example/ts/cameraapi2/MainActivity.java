/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.example.ts.cameraapi2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView view = findViewById(R.id.cn);
        int c = Camera.getNumberOfCameras();
        view.setText("摄像头个数： " + c);

        findViewById(R.id.c1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("-------","----wxy---" + getStatus(MainActivity.this,"wxy"));
//                startCamera();
            }
        });

        findViewById(R.id.c2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    public static boolean getStatus(Context context,String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "vat_mode", Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(key, false);
    }
    private void startCamera(){
        Intent intent = new Intent(MainActivity.this,Camera1Activity.class);
        startActivity(intent);
    }

}
