package com.cdut.hzb.iplayerdemo.music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cdut.hzb.iplayerdemo.R;

/**
 * MusicPresent启动MusicService服务播放音乐（娃娃机claw音乐）
 */
public class MusicActivity extends AppCompatActivity {

    private MusicPresent musicPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        musicPresent = new MusicPresent(this);
    }

    public void start(View view) {
        musicPresent.play();
    }

    public void stop(View view) {
        musicPresent.stop();
    }

    public void rePlay(View view) {
        musicPresent.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPresent.destroy();
    }
}
