package com.cdut.hzb.iplayerdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<VideoInfo> videoInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoUtils.getVideoFile(videoInfos, Environment.getExternalStorageDirectory());
            }
        }).start();

        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        int screenWidth =  displayMetrics.widthPixels;
        Log.d("debug", " -->screenWidth =  " + screenWidth);
    }
}
