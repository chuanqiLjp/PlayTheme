package com.ljp.testplaytheme;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.hhqy.ljp.banner.HBanner;
import com.com.hhqy.ljp.banner.ImageLoader;
import com.com.hhqy.ljp.banner.onPageChangeListener;
import com.hhqy.learnplaytheme.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * 文件基本路径
     */
    public static final String BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
    //    public static final String BASE = "/storage/emulated/0/download";
    private HBanner hbanner;
    private List<String> playList = new ArrayList<>();
    private List<String> musicList = new ArrayList<>();
    private List<File> musicList1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hbanner = (HBanner) findViewById(R.id.main_banner);
        List<File> fileList = new ArrayList<>();
        fileList.addAll(readNowLasterFile(BASE, ".jpg"));
        fileList.addAll(readNowLasterFile(BASE, ".mp4"));
        fileList.addAll(readNowLasterFile(BASE, ".avi"));
        fileList.addAll(readNowLasterFile(BASE, ".jpg"));
        musicList1 = readNowLasterFile(BASE + "/voice", ".mp3");
        for (File file : fileList) {
            playList.add(file.getAbsolutePath());
            Log.d(TAG, "onCreate: path=" + file.getAbsolutePath());
        }
        for (File file : musicList1) {
            musicList.add(file.getAbsolutePath());
            Log.d(TAG, "onCreate: path=" + file.getAbsolutePath());
        }
        reset(null);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
        hbanner.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        hbanner.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        hbanner.onDestory();
        hbanner = null;
        HHQYApplication.getRefWatcher(this).watch(this);
    }

    /**
     * 读取特定文件后缀名的文件，返回处理后的文件集合
     *
     * @param path   指定的路径
     * @param laster 后缀名
     * @return 返回处理后的文件集合
     */
    public static List<File> readNowLasterFile(String path, final String laster) {
        List<File> list = new ArrayList<File>(); // 保存读取到的特定文件
        File file = new File(path);
        // 自定义一个文件过滤器
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                String lowerCaseName = name.toLowerCase();
                String lowerCaseLaster = laster.toLowerCase();
                // 若文件名的后缀是 laster 则返回true
                return lowerCaseName.endsWith(lowerCaseLaster);
            }
        };
        File[] listFiles = file.listFiles(filter);
        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                list.add(listFiles[i]);
            }
        }
        return list;
    }

    public void stopAutoPlay(View view) {
        hbanner.stopAutoPlay();
    }

    public void refresh(View view) {
        List<String> list = new ArrayList<>();
        for (int i = 3; i < playList.size() - 2; i++) {
            list.add(playList.get(i));
        }
        startPlay(list, 1, true, true);
    }

    public void onePic(View view) {
        List<String> list = new ArrayList<>();
        list.add(playList.get(0));
        startPlay(list, 3, true, true);
    }

    public void oneVideo(View view) {
        List<String> list = new ArrayList<>();
        list.add(playList.get(playList.size() - 2));
        startPlay(list, 3, true, true);
    }

    public void onePicOneVideo(View view) {
        List<String> list = new ArrayList<>();
        list.add(playList.get(0));
        list.add(playList.get(playList.size() - 2));
        startPlay(list, 3, true, true);
    }

    public void reset(View view) {
        List<String> list = new ArrayList<>();
        list.addAll(playList);
        startPlay(list, 3, true, true);
    }

    private void startPlay(List<String> list, int delay, boolean isScrool, boolean isAuto) {
        hbanner.setPlayList(list)
                .setMusicList(musicList)
                .setDelayTime(delay)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void loadImage(ImageView imageView, String absolutePath) {
//                        Picasso.with(MainActivity.this).load(new File(absolutePath)).into(imageView);
                        Glide.with(MainActivity.this).load(new File(absolutePath)).into(imageView);
                    }
                })
                .setOnPageChangeListener(new onPageChangeListener() {
                    @Override
                    public void onPageSelected(int page) {
                        if (HBanner.DEBUG)
                            Log.e(TAG, "onPageSelected: MainActivity-->page=" + page);
                    }
                })
                .setStartPage(1)
                .setGestureScroll(isScrool)
                .setIsAutoPlay(isAuto)
                .setOffscreenPageLimit(3)
                .setDebug(true)
                .startPlay();
    }

    boolean isTouch = true;

    public void isTouch(View view) {
        show("isTouch=" + isTouch);
        hbanner.setGestureScroll(isTouch);
        isTouch = !isTouch;
    }

    boolean isAuto = true;

    public void isAuto(View view) {
        show("isAuto=" + isAuto);
        hbanner.setIsAutoPlay(isAuto);
        isAuto = !isAuto;
    }

    int delayTime = 1;

    public void delayTime(View view) {
        show("delayTime=" + delayTime);
        hbanner.setDelayTime(delayTime);
        delayTime++;
        if (delayTime == 10) {
            delayTime = 1;
        }
    }

    public void stopPlay(View view) {
        hbanner.stopPlay();
    }

    public void show(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void startAutoPlay(View view) {
        hbanner.startAutoPlay();
    }

    public void onlyPic(View view) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < playList.size() - 3; i++) {
            list.add(playList.get(i));
        }
        startPlay(list, 3, true, true);
    }

    public void onlyVideo(View view) {
        List<String> list = new ArrayList<>();
        for (int i = playList.size() - 3; i < playList.size(); i++) {
            list.add(playList.get(i));
            Log.e(TAG, "onlyVideo: path=" + playList.get(i));
        }
        startPlay(list, 3, true, true);
    }

    public void finishActivity(View view) {
//        startActivity(new Intent(this,Main2Activity.class));
        finish();
    }

    public void goToActivity(View view) {
        startActivity(new Intent(this, NextActivity.class));
    }

    public void upVoice(View view) {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);

    }

}
