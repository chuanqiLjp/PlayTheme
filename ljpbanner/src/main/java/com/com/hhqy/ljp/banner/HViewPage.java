package com.com.hhqy.ljp.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 此类由  刘建培yuxue  创建于 2017/6/14.
 *
 * @author ljp
 */
//TODO 1.添加首尾进行判断是否是视频，是视频添加预览图片；2.增加标题；3.增加指示器；4.增加动画；5.增加样式（标题的位置和指示器等）
class HViewPage extends ViewPager {
    private static final String TAG = "HBanner";
    /**
     * 广告图片间的延时
     */
    private int delayTime;
    /**
     * 是否自动播放
     */
    private boolean isAutoPlay;
    /**
     * 播放列表，存储播放内容的绝对路径
     */
    private List<String> playList;
    /**
     * 背景音乐的播放列表
     */
    private List<String> musicList;
    private HPagerAdapter adapter;
    /**
     * 真实的播发列表数目
     */
    private int realCount;
    /**
     * 是否可以滑动切换
     */
    private boolean isScroll;
    /**
     * 定时器，用于切换定时
     */
    private TimerTask timerTask;
    /**
     * 倒计时的剩余时间
     */
    private int endTime;
    /**
     * 手是否按压在屏幕上
     */
    private boolean isTouch;
    private HBannerHandler handler = new HBannerHandler();
    /**
     * 自定义加载图片的接口
     */
    private ImageLoader imageLoader = null;
    /**
     * 是否继续发送消息
     */
    private boolean isHandlerLooing = true;
    /**
     * 记录上次是否还在播放
     */
    private boolean lastVideoIsPlaying = false;
    /**
     * 记录上次音乐是否还在播放
     */
    private boolean lastMusicIsPlaying = false;
    /**
     * 默认的保留页面或预加载页面的个数
     */
    private final int PAGE_LIMIT = 3;
    /**
     * 开始播放的起始位置，从1开始
     */
    private int startPage = 1;
    private onPageChangeListener listener;
    private HOnPageChangeListener hOnPageChangeListener;

    public HViewPage(Context context) {
        this(context, null);
    }

    public HViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        delayTime = 3;
        isAutoPlay = true;
        playList = new ArrayList<>();
        musicList = new ArrayList<>();
        adapter = new HPagerAdapter(this);
        isScroll = true;
        realCount = 0;
        isTouch = false;
        isHandlerLooing = true;
        hOnPageChangeListener = new HOnPageChangeListener(this, adapter);
        addOnPageChangeListener(hOnPageChangeListener);//设置ViewPager的页数变化监听

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isScroll) {//禁止手势滑动
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                stopTask();
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (isAutoPlay) {//手指离开屏幕上了，设置自动播放就需要让消息循环起来
                    startTask(delayTime);
                }
                break;
        }
        return isScroll && super.onTouchEvent(ev);
    }

    /**
     * 与Activity的生命周期进行绑定  当 onResume() 时调用
     */
    public void onResume() {
        if (adapter != null && lastVideoIsPlaying) {
            adapter.startVideo();
        } else if (lastMusicIsPlaying) {
            MusicPlayer.getInstance().startMusic(getMusicList());
        }
        if (lastAutoPlay) {
            startAutoPlay();
        }
    }

    /**
     * 记录上次是否是自动播放
     */
    boolean lastAutoPlay = true;

    /**
     * 与Activity的生命周期进行绑定 当 onPause()时调用
     */
    public void onPause() {
        if (adapter != null) {
            lastVideoIsPlaying = adapter.isPlaying();
            adapter.pauseVideo();
        }
        lastAutoPlay = getAutoPlay();
        if (lastAutoPlay) {
            stopAutoPlay();
        }
        lastMusicIsPlaying = MusicPlayer.getInstance().getIsPlaying();
        MusicPlayer.getInstance().pauseMusic();
    }

    /**
     * 与Activity的生命周期进行绑定 当 onDestory()时调用
     */
    public void onDestory() {
        MusicPlayer.getInstance().stopMusic();
        adapter.stopVideo();
        stopAutoPlay();
        recycle();
    }

    /**
     * 释放内存
     */
    private void recycle() {
        handler = null;
        isTouch = false;
        adapter.recycle();
        adapter = null;
        playList.clear();
        playList = null;
        removeAllViews();
    }

    public int getRealCount() {
        return realCount;
    }

    public boolean getIsTouch() {
        return isTouch;
    }

    /**
     * 设置图片播放的间隔时间
     *
     * @param delayTime 间隔时长，单位：秒
     * @return
     */
    public HViewPage setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    public int getDelayTime() {
        return delayTime;
    }

    /**
     * 设置是否允许自动播放
     *
     * @param isAutoPlay ture：表示自动播放
     * @return
     */
    public HViewPage setIsAutoPlay(boolean isAutoPlay) {
        isHandlerLooing = isAutoPlay;
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public boolean getAutoPlay() {
        return isAutoPlay;
    }

    /**
     * 设置播放列表，一个List可以包含图片或视频的绝对路径
     *
     * @param playList 可以包含图片或视频的绝对路径
     * @return
     */
    public HViewPage setPlayList(List<String> playList) {
        if (playList == null || playList.size() < 1) {
            throw new NullPointerException("播放列表不能为空");
        }
        this.playList = playList;
        realCount = playList.size();
        return this;
    }

    public List<String> getPlayList() {
        return playList;
    }

    public int getPlayListSize() {
        return playList.size();
    }

    /**
     * 设置背景音乐的播放列表
     *
     * @param musicList 背景音乐的播放列表
     */
    public HViewPage setMusicList(List<String> musicList) {
        this.musicList.clear();
        this.musicList.addAll(musicList);
        return this;
    }

    public List<String> getMusicList() {
        return musicList;
    }

    /**
     * 设置允许手势滑动操作
     *
     * @param isScrollable ture：可以使用手势进行左右滑动
     * @return
     */
    public HViewPage setGestureScroll(boolean isScrollable) {
        isScroll = isScrollable;
        return this;
    }

    public boolean getGestureScroll() {
        return isScroll;
    }

    public HViewPage setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }


    public HViewPage setPageListener(onPageChangeListener listener) {
        this.listener = listener;
        hOnPageChangeListener.setPageListener(listener);
        return this;
    }

    public HViewPage setStartPage(int startPage) {
        startPage = startPage == 0 ? 1 : startPage;
        startPage = startPage == (realCount + 1) ? (realCount + 1) : startPage;
        this.startPage = startPage;
        return this;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setOffscreenPageLimit(int limit) {
        super.setOffscreenPageLimit(limit);
    }

    public int getOffscreenPageLimit() {
        return super.getOffscreenPageLimit();
    }

    /**
     * 启动播放
     */
    public void startPlay() {
        //TODO 交叉添加，判断是否是视频，若是视频添加预览图片
        playList.add(playList.size(), playList.get(0));
        playList.add(0, playList.get(playList.size() - 2));
        if (isScroll && realCount > 1) {
            setGestureScroll(true);
        } else {
            setGestureScroll(false);
        }
        if (adapter == null) {
            adapter = new HPagerAdapter(this);
        }
        setOffscreenPageLimit(PAGE_LIMIT);
        setAdapter(adapter);
        adapter.setImageLoader(imageLoader);
        adapter.notifyDataSetChanged();
        adapter.stopVideo();
        setCurrentItem(startPage);
        if (isAutoPlay) {
            startAutoPlay();
        }
        MusicPlayer.getInstance().startMusic(musicList);
        setVisibility(VISIBLE);
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        stopAutoPlay();
        adapter.stopVideo();
        setVisibility(GONE);
        MusicPlayer.getInstance().stopMusic();
    }

    /**
     * 开始自动播放
     */
    public void startAutoPlay() {
        stopAutoPlay();
        setIsAutoPlay(true);
        startTask(delayTime);
        setVisibility(VISIBLE);
    }

    /**
     * 停止自动播放
     */
    public void stopAutoPlay() {
        setIsAutoPlay(false);
        stopTask();
    }

    /**
     * 开始滑动翻页的计时
     *
     * @param times 需要倒计时的时间
     */
    public void startTask(int times) {
        endTime = times;
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (endTime <= 0) {
                        stopTask();
                        handler.sendEmptyMessage(HBannerHandler.NEXT_PAGE);
                    }
                    endTime--;
                }
            };
            new Timer().schedule(timerTask, 100, 1000);
        }
    }

    /**
     * 结束计时
     */
    private void stopTask() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
        if (listener != null) {
            listener.onPageSelected(item);
        }
    }

    /**
     * 下一页
     */
    public void nextPage() {
        setCurrentItem(getCurrentItem() + 1);
    }

    /**
     * 上一页
     */
    public void previousPage() {
        setCurrentItem(getCurrentItem() - 1);
    }


    /**
     * 一个用于自动播放时发送消息的Handler
     */
    private class HBannerHandler extends Handler {
        public static final int NEXT_PAGE = 1;
        public static final int PREVIOUS_PAGE = 2;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (adapter.isPlaying()) {
                return;
            }
            switch (msg.what) {
                case NEXT_PAGE:
                    if (!isTouch) {   //手指没有按压在屏幕上可以切换 ，否则不切换
                        nextPage();
                    }
                    break;
                case PREVIOUS_PAGE:
                    if (!isTouch) {
                        previousPage();
                    }
                    break;
            }
        }

        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) { //通过重载发送消息的方法决定是否发送消息
            return isHandlerLooing ? super.sendMessageAtTime(msg, uptimeMillis) : false;
        }
    }
}
