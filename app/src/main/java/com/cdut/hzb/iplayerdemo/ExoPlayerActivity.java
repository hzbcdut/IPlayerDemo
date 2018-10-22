package com.cdut.hzb.iplayerdemo;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.List;

public class ExoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ExoPlayerActivity";

    private String videoPath;
    private String subtitlePath;
    private ImageView playIv, pauseIv;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private boolean mIsLandscape;
    private int mScreenWidth, mScreenHeight;
    private int mVideoWidth, mVideoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 默认是unspecified，不设置的话，当手机屏幕旋转开关打开的情况下旋转屏幕，手机不会自动旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        setContentView(R.layout.activity_exo_player);
        requestPermission();
        getData();
        playIv = (ImageView) findViewById(R.id.exo_play);
        playIv.setOnClickListener(this);
        pauseIv = (ImageView) findViewById(R.id.exo_pause);
        pauseIv.setOnClickListener(this);

        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.google_simple_exo_player_view);
        initPlayer();
        bindView();
        preparePlay();
        setPlayerListener();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
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

        TrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


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
//        videoPath = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=18376&editionType=high&source=ucloud";
        videoPath = "http://uc.cdn.kaiyanapp.com/1490499356527_f752d403_1280x720.mp4?t=1540181204&k=a70c364834e4e3bf";

//        videoPath = "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv";
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(videoPath),
                dataSourceFactory, extractorsFactory, null, null);

//        subtitlePath = "/storage/sdcard1/under_sandet.sub";
//        subtitlePath = "/storage/sdcard1/Land of Mine_TW.srt"; // 不支持srt字幕格式

        // 添加字幕文件
        if (!TextUtils.isEmpty(subtitlePath) && new File(subtitlePath).exists()) {
            Format format = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "zh");
            MediaSource subtitleSource = new SingleSampleMediaSource(Uri.parse(subtitlePath), dataSourceFactory, format, C.TIME_UNSET);
            MergingMediaSource mergedSource =
                    new MergingMediaSource(videoSource, subtitleSource);
            mExoPlayer.prepare(mergedSource);
        }else {

            // Prepare the player with the source.
            mExoPlayer.prepare(videoSource);
        }
        mExoPlayer.setPlayWhenReady(true); // 资源准备好就播放。
        // TODO: 2017/11/6 0006
        // 控制面板中按钮控制播放是调这个方法吗？


        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d("debug", " -> ExoPlaybackException error =   " +  error);

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

    }

    /**
     * 设置播放器的监听回调
     */
    private void setPlayerListener() {
        // 接收Video事件
        mExoPlayer.addVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                LogUtil.d(Constant.DEBUG_LOG, TAG + " -->onVideoSizeChanged()  width = " + width + " height = " + height
                        + "  unappliedRotationDegrees = " + unappliedRotationDegrees + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
                mVideoWidth = width;
                mVideoHeight = height;
            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });
        // 接收MetaData事件
        mExoPlayer.addMetadataOutput(new MetadataRenderer.Output() {
            @Override
            public void onMetadata(Metadata metadata) {
                LogUtil.d(Constant.DEBUG_LOG, TAG + " --> metadata = " + metadata);
            }
        });
        // 接收文本事件
        mExoPlayer.addTextOutput(new TextRenderer.Output() {
            @Override
            public void onCues(List<Cue> cues) {

            }
        });
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

    @Override
    public void onClick(View v) {
        // TODO: 2017/11/6 0006  播放按钮点击事件无效？
        Log.d("debug", " --> onClick v = " + v);
        if (v == playIv) {
            mExoPlayer.setPlayWhenReady(true);
        }else if (v == pauseIv) {
            mExoPlayer.stop();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.d(Constant.DEBUG_LOG, TAG + " --> onConfigurationChanged(Configuration newConfig) ");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            //设置全屏即隐藏状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mIsLandscape = true;

            //横屏 视频充满全屏
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mPlayerView.getLayoutParams();
            // 因为屏幕旋转了， 所以这里的宽是屏幕的高    根据视频比例等比缩放
//            layoutParams.width = mScreenHeight;
//            layoutParams.height = mScreenHeight * mVideoHeight/mVideoWidth;
            // 或者不精确一点可以这么设置
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){// 竖屏
            //恢复状态栏
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mIsLandscape = false;

            //竖屏 视频显示固定大小
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mPlayerView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = mScreenWidth;
            layoutParams.height = mScreenWidth * mVideoHeight/mVideoWidth;
        }
    }
}
