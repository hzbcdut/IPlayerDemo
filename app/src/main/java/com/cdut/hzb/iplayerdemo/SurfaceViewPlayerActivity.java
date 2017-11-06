package com.cdut.hzb.iplayerdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class SurfaceViewPlayerActivity extends AppCompatActivity {
    private static final String TAG = "SurfaceViewPlayerActivity";

    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;


    private String videoPath;
    private boolean isPause;
    private int currentPos;

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_player);
        requestPermission();
        getData();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        final SurfaceHolder mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setSizeFromLayout();
                mMediaPlayer = new MediaPlayer(); // 在surfaceDestroyed释放资源， 就得在surfaceCreated初始化MediaPlayer
                mMediaPlayer.setDisplay(mHolder);
                try {
                    mMediaPlayer.setDataSource(videoPath);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (!isPause) {
                                mMediaPlayer.start();
                            }else {
                                Log.d("debug_log", TAG + " --> currentPos = " + currentPos);
                                mMediaPlayer.seekTo(currentPos);
                                mMediaPlayer.start();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", TAG + " -->onResume()  mMediaPlayer = " + mMediaPlayer);
        isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", TAG + " -->onPause()  mMediaPlayer = " + mMediaPlayer);
        if (mMediaPlayer != null) {
            currentPos = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            isPause = true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getData();
    }

    // 可以通过Intent.setData方法设置数据源
    public void getData() {
        if (getIntent().getData() != null) {
            videoPath = getIntent().getData().getPath();
        }
    }


}
