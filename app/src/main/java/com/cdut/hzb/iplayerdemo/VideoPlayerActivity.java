package com.cdut.hzb.iplayerdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";

    private TextureView mTextureView;
    private MediaPlayer mediaPlayer;
    private Surface mSurface;

    private String videoPath;
    private int mScreenWidth, mScreenHeight;
    private int mVideoWidth, mVideoHeight;
    private boolean mIsLandscape;
    private String subtitlePath = "/storage/sdcard1/Land of Mine_TW.srt";

    public static void start(Context context, String videoPath) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
//        intent.putExtra(IntentKey.VIDEO_PATH, videoPath);
        intent.setData(Uri.parse(videoPath));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 默认是unspecified，不设置的话，当手机屏幕旋转开关打开的情况下旋转屏幕，手机不会自动旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        setContentView(R.layout.activity_video_player);
        requestPermission();
        getData();
        mTextureView = (TextureView) findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(listener);

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

    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.d(Constant.DEBUG_LOG, TAG + " --> onSurfaceTextureAvailable()");
            mediaPlayer = new MediaPlayer();

            mSurface = new Surface(surface);
            mediaPlayer.setSurface(mSurface);

            mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(MediaPlayer mp, TimedText text) {
                    if (text != null) {
                        LogUtil.d(Constant.DEBUG_LOG, TAG + " --> text = " + text.getText());
                    }
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            try {
                if (!TextUtils.isEmpty(videoPath)) {
                    mediaPlayer.setDataSource(videoPath);
                    mediaPlayer.prepare();
                    try {
                        mediaPlayer.addTimedTextSource(subtitlePath, MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);

                        MediaPlayer.TrackInfo[]  trackInfos = mediaPlayer.getTrackInfo();
                        if (trackInfos != null && trackInfos.length > 0) {
                            for ( int i = 0; i< trackInfos.length; i++) {
                                MediaPlayer.TrackInfo info = trackInfos[i];
                                if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {

                                }else if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT){
                                    mediaPlayer.selectTrack(i);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mVideoWidth = mediaPlayer.getVideoWidth();
                    mVideoHeight = mediaPlayer.getVideoHeight();
                    setTextureViewLayoutParams(mVideoWidth, mVideoHeight);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.d(Constant.DEBUG_LOG, TAG + " --> onSurfaceTextureDestroyed()");
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mSurface = null;
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void setTextureViewLayoutParams(int videoWidth, int videoHeight) {
        LogUtil.d(Constant.DEBUG_LOG, TAG +" -->setTextureViewLayoutParams() videoWidth = " + videoWidth + " videoHeight = " + videoHeight);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = mScreenWidth;
        layoutParams.height = mScreenWidth * videoHeight / videoWidth;

        if (videoWidth > videoHeight) { // 横屏
        } else {
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
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
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
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = mScreenWidth;
            layoutParams.height = mScreenWidth * mVideoHeight/mVideoWidth;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
