package com.cdut.hzb.iplayerdemo.music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cdut.hzb.iplayerdemo.R;

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
