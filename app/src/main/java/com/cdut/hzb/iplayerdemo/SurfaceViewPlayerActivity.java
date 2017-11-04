package com.cdut.hzb.iplayerdemo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_player);
        getData();

        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        final SurfaceHolder mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setSizeFromLayout();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDisplay(mHolder);
                try {
                    mMediaPlayer.setDataSource(videoPath);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mMediaPlayer.start();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", TAG + " -->onStart()  mMediaPlayer = " + mMediaPlayer);
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
