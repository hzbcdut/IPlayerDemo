package com.cdut.hzb.iplayerdemo.music;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cdut.hzb.iplayerdemo.LogUtil;

import java.io.IOException;

/**
 * Created by hans on 2017/12/4 0004.
 */

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer;
    private PlayBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(Constant.DEBUG_LOG, TAG + " --> onBind()  ");
        if (mBinder == null) {
            mBinder = new PlayBinder();
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        return super.onUnbind(intent);
    }


    public void startPlay() {

        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor adf = assetManager.openFd("Shindig_8bit_423845.mp3");
            // 放在本地路径可以播放
//            musicPath =  Environment.getExternalStorageDirectory() + File.separator + "Shindig_8bit_423845.mp3";
//            LogUtil.d(Constant.DEBUG_LOG, TAG + "-->音乐文件大小 length = " + new File(musicPath).length());
//            mediaPlayer.setDataSource(musicPath);

            mediaPlayer.reset();
//            mediaPlayer.setDataSource(adf.getFileDescriptor()); // 这样设置错误, onError中what字段返回1，未知错误
            mediaPlayer.setDataSource(adf.getFileDescriptor(), adf.getStartOffset(), adf.getLength());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(Constant.DEBUG_LOG, TAG + " -->onBind e = " + e);
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(Constant.DEBUG_LOG, TAG + " -->onBind() --  onPrepared ");
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.d(Constant.DEBUG_LOG, TAG + " -->onError() what = " + what + " extra = " + extra);
                return false;
            }
        });
    }


    /**
     * 停止播放音乐
     */
    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer = null;
        }
    }



    public class PlayBinder extends Binder{
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
