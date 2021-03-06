package com.czx.testdemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends Activity implements View.OnClickListener {

    private VideoView mVideoView;
    private ImageButton mVideoStart;
    private ImageView mFirstFrame;
    private String mVideoPath;
    private MediaController mMediaController;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int CHOSE_VIDEO = 2;
    private boolean isFirstLaunch = true;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无title
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.video_view);
        mVideoStart = findViewById(R.id.video_start);
        mFirstFrame = findViewById(R.id.first_frame);
        mVideoStart.setOnClickListener(this);
        mVideoView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            initVideoView();
            mVideoStart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[grantResults.length - 1] == PackageManager.PERMISSION_GRANTED) {
                    initVideoView();
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void initVideoPath() {
        File file = null;
        if (isFirstLaunch) {
            Uri innerVideoUri = Uri.parse("android.resource://com.czx.testdemo/"+R.raw.demo);
            mFirstFrame.setImageBitmap(getLocalVideoBitmap(innerVideoUri));
            mFirstFrame.setVisibility(View.VISIBLE);
            mVideoView.setVideoURI(innerVideoUri);
//            file = new File(Environment.getExternalStorageDirectory(), "/czx.mp4");
//            mVideoPath = file.getPath();
        } else {
            file = new File(mVideoPath);
            if (file != null && file.exists()) {
                mFirstFrame.setImageBitmap(getLocalVideoBitmap(file.getPath()));
                mFirstFrame.setVisibility(View.VISIBLE);
                mVideoView.setVideoPath(file.getPath());//设置视频文件
            } else {
                Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initVideoView() {
        initVideoPath();
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
                mVideoStart.setVisibility(View.VISIBLE);
                mFirstFrame.setVisibility(View.VISIBLE);
                if (mMediaController != null) {
                    mMediaController.hide();
                    mMediaController = null;
                }
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
            case R.id.video_start:
                if (!mVideoView.isPlaying()) {
                    mMediaController = new MediaController(MainActivity.this);
                    mVideoStart.setVisibility(View.GONE);
                    mFirstFrame.setVisibility(View.GONE);
//                    mVideoView.setMediaController(mMediaController);//控制栏
                    mVideoView.start();
                }
                break;
            case R.id.video_view:
                if (!mVideoView.isPlaying()) {
                    openAlbum();
                }
                break;
            default:
                break;
        }
    }

    public Bitmap getLocalVideoBitmap(String localPath) {
        File file = new File(localPath);
        String[] splitPath = localPath.split("\\.");
        boolean isJpg = isJpg = "jpg".equals(splitPath[splitPath.length - 1]);
        if (!file.exists() || isJpg) {
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    public Bitmap getLocalVideoBitmap(Uri localPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(this, localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    public void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOSE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOSE_VIDEO:
                if (data != null && resultCode == RESULT_OK) {
                    isFirstLaunch = false;
                    Uri selectedVideo = data.getData();
                    /** 数据库查询操作。
                     * 第一个参数 uri：为要查询的数据库+表的名称。
                     * 第二个参数 projection ： 要查询的列。
                     * 第三个参数 selection ： 查询的条件，相当于SQL where。
                     * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
                     * 第四个参数 sortOrder ： 结果排序。
                     */
                    Cursor cursor = getContentResolver().query(selectedVideo,
                            null, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        mVideoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        cursor.close();
                    }
                    Log.d("czxtest ", "video path = " + mVideoPath);
                }
                break;
            default:
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
