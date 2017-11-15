package com.cdut.hzb.iplayerdemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<VideoInfo> videoInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoUtils.getVideoFile(videoInfos, getExternalStorageDirectory());
            }
        }).start();

        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        int screenWidth =  displayMetrics.widthPixels;
        Log.d("debug", " -->screenWidth =  " + screenWidth);

        getSDCard();

//         -->所有存储位置  path = /storage/emulated/0
//        -->所有存储位置  path = /storage/sdcard1
        String[]  dirs = getExtSDCardPath();
        for (String path:  dirs) {
            LogUtil.d(Constant.DEBUG_LOG, TAG + " -->所有存储位置  path = " + path);
        }
    }


    private  void getSDCard() {
        // 内置SD卡？  /storage/emulated/0
        File  externalStorageDir = Environment.getExternalStorageDirectory();
        LogUtil.d(Constant.DEBUG_LOG, TAG + " --> externalStorageDir = " + externalStorageDir.getPath());
        // 列出这个目录下的所有文件和目录
        String[] files =  externalStorageDir.list();
        for (String s:  files) {
            LogUtil.d(Constant.DEBUG_LOG, TAG + " --> s = " + s);
        }
    }

    public String[] getExtSDCardPath() {
        StorageManager storageManager = (StorageManager)getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[])invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
