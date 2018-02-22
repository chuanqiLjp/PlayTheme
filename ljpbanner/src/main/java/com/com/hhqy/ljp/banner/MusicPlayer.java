package com.com.hhqy.ljp.banner;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuxue on 2017/7/25.
 */

class MusicPlayer {
    private static final String TAG = "MusicPlayer";
    private static MediaPlayer mediaPlayer = null;
    private Context mContext;
    private static MusicPlayer music;

    private MusicPlayer() {

    }

    public static MusicPlayer getInstance() {
        if (music == null) {
            music = new MusicPlayer();
        }
        return music;
    }

    public int playMusic(String musicPath) {
        File file = new File(musicPath);
        if (!file.exists()) {
            return -1;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            // 在播放音频资源之前，必须调用Prepare方法完成些准备工作
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();// 释放音频资源
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                try {
                    /* 发生错误时也解除资源与MediaPlayer的赋值 */
                    mp.release();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                return true;
            }
        });
        return 0;
    }

    private static int position = 0;

    private int playMusic(final List<String> musicList) {
        pauseMusic();
        if (musicList == null || musicList.size() < 1) {
            return -1;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(musicList.get(position));
            // 在播放音频资源之前，必须调用Prepare方法完成些准备工作
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                position++;
                position = position > musicList.size() - 1 ? 0 : position;
                if (HBanner.DEBUG)
                    Log.d(TAG, "onCompletion: position=" + position + ",path=" + musicList.get(position));
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicList.get(position));
                    // 在播放音频资源之前，必须调用Prepare方法完成些准备工作
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (HBanner.DEBUG)
                    Log.d(TAG, "onError: position=" + position);
                position++;
                position = position > musicList.size() - 1 ? 0 : position;
                try {
                    mediaPlayer.setDataSource(musicList.get(position));
                    // 在播放音频资源之前，必须调用Prepare方法完成些准备工作
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        return 0;
    }

    public void startMusic(List<String> musicList) {
        if (HBanner.DEBUG)
            Log.d(TAG, "startMusic: mediaPlayer=" + mediaPlayer);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            playMusic(musicList);
        }
    }

    public void pauseMusic() {
        if (HBanner.DEBUG)
            Log.d(TAG, "pauseMusic: mediaPlayer=" + mediaPlayer);
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stopMusic() {
        Log.d(TAG, "stopMusic: ");
        position = 0;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean getIsPlaying() {
        if (mediaPlayer == null) {
            return false;
        }
        return mediaPlayer.isPlaying();
    }
}
