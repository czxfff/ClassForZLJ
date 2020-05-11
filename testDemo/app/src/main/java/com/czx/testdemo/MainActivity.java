package com.czx.testdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView mVideoView;
    private Button mVideoBt;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int MEDIA_INFO_UNKNOWN = 1;  //媒体信息未知
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; //媒体信息视频跟踪滞后
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3; //媒体信息\视频渲染\开始
    public static final int MEDIA_INFO_BUFFERING_START = 701; //媒体信息缓冲启动
    public static final int MEDIA_INFO_BUFFERING_END = 702; //媒体信息缓冲结束
    public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703; //媒体信息网络带宽（703）
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800; //媒体-信息-坏-交错
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801; //媒体信息找不到
    public static final int MEDIA_INFO_METADATA_UPDATE = 802; //媒体信息元数据更新
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901; //媒体信息不支持字幕
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902; //媒体信息字幕超时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.video_view);
        mVideoBt = findViewById(R.id.video_bt);
        mVideoBt.setOnClickListener(this);

        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            initVideoPath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initVideoPath();
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void initVideoPath() {
        File file = new File(Environment.getExternalStorageDirectory(), "/czx.mp4");
        Log.d("czxtest ", "abso path = " + file.getAbsolutePath());
        Log.d("czxtest ", "normal path = " + file.getPath());
        if (!file.exists()) {
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mVideoView.setVideoPath(file.getPath());//设置视频文件
        mVideoView.start();
    }

    private void initVideoView() {
        File file = new File(Environment.getExternalStorageDirectory(), "demo.mp4");
        if (!file.exists()) {
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mVideoView.setVideoPath(file.getPath());//设置视频文件
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //视频加载完成,准备好播放视频的回调

            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //视频播放完成后的回调

            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //异常回调
                return false;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
            }
        });
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //信息回调

                return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_bt:
                if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                }
                break;
            case 2:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.suspend();
        }
    }
}
