package com.cdut.hzb.iplayerdemo.music;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.cdut.hzb.iplayerdemo.LogUtil;


/**
 * Created by hans on 2017/12/4 0004.
 */

public class MusicPresent {
    private static final String TAG = "MusicPresent";

    private Activity act;
    private ServiceConnection mSc;
    private boolean musicOff;
    private MusicService musicService;

    public MusicPresent(Activity activity) {
        act = activity;
        mSc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtil.d(Constant.DEBUG_LOG, TAG + " -->绑定服务成功回调 onServiceConnected()  ComponentName = " + name + "  IBinder  service = " + service);
                musicService  = ((MusicService.PlayBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindMusicService();
    }

    /**
     * 绑定服务
     */
    private void bindMusicService() {
        Intent intent = new Intent(act, MusicService.class);
        act.bindService(intent, mSc, Context.BIND_AUTO_CREATE);
    }

    /**
     * 开始播放音乐
     */
    public void play() {
        musicService.startPlay();
    }

    /**
     * 停止播放音乐
     */
    public void stop() {
        musicService.stopPlay();
    }

    public void destroy() {
        unBindMusicService();
    }

    /**
     * 取消绑定服务
     */
    private void unBindMusicService() {
        act.unbindService(mSc);
    }
}
