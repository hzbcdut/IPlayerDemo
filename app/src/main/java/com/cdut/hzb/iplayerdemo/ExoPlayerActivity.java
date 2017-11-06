package com.cdut.hzb.iplayerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerActivity extends AppCompatActivity {

    private String videoPath;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        requestPermission();
        getData();
        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.google_simple_exo_player_view);
        initPlayer();
        bindView();
        preparePlay();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    private void initPlayer() {
        // 1. Create a default TrackSelector
        TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        ;
        TrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);

        DefaultRenderersFactory rendersFactory = new DefaultRenderersFactory(this);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(rendersFactory, trackSelector);

        // 这种方式创建ExoPlayer播放器已过期
//        // 1. Create a default TrackSelector
//        TrackSelection.Factory adaptiveTrackSelectionFactory =   new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);;
//        TrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
//        // 2. Create a default LoadControl
//        LoadControl loadControl = new DefaultLoadControl();
//        // 3. Create the player
//         mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

    }

    private void bindView() {
        mPlayerView.setPlayer(mExoPlayer);
//        // 这个是干什么用的？
        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);  // 填满宽度, 高度按屏幕宽高比进行缩放
//        // 隐藏用户控制面板？
//        mPlayerView.setUseController(false);
//        mPlayerView.setControllerAutoShow(false);
//        mPlayerView.hideController();
    }

    private void preparePlay() {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);
       // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(videoPath),
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        mExoPlayer.prepare(videoSource);

        mExoPlayer.setPlayWhenReady(true); // 资源准备好就播放。
        // TODO: 2017/11/6 0006
        // 控制面板中按钮控制播放是调这个方法吗？
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        mExoPlayer.release();
    }

    // 可以通过Intent.setData方法设置数据源
    public void getData() {
        if (getIntent().getData() != null) {
            videoPath = getIntent().getData().getPath();
        }
    }

}
