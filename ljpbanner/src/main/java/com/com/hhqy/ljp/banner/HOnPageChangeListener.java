package com.com.hhqy.ljp.banner;

import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.List;

/**
 * 此类由  刘建培yuxue  创建于 2017/6/16.
 */
class HOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private static final String TAG = "HOnPageChangeListener";
    private HViewPage banner;
    private HPagerAdapter adapter;
    private onPageChangeListener listener;
    private List<String> musicList;
    private List<String> playList;

    public HOnPageChangeListener(HViewPage banner, HPagerAdapter adapter) {
        this.banner = banner;
        this.adapter = adapter;
        musicList = banner.getMusicList();
        playList = banner.getPlayList();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (HBanner.DEBUG)
            Log.e("HOnPageChangeListener", "onPageSelected: HOnPageChangeListener-->page=" + position);
        if (adapter != null) {
            adapter.stopVideo();
        }
        MusicPlayer.getInstance().pauseMusic();
        if (listener != null) {
            listener.onPageSelected(position);
        }
        //切换控制左右无限滑动
        if (position >= banner.getPlayListSize() - 1) {//到达最后一页
            banner.setCurrentItem(1, false);
            return;
        } else if (position < 1) {//到达第一页
            banner.setCurrentItem(banner.getPlayListSize() - 2, false);
            return;
        }
        //如果是自动播放开启自动播放模式
        if (banner.getAutoPlay()) {
            banner.startTask(banner.getDelayTime());
        }
        if (banner.getPlayList() != null && banner.getPlayList().size() > 0
                && banner.getMusicList() != null && banner.getMusicList().size() > 0) {
            if (BannerUtil.judgeType(banner.getPlayList().get(position)) == BannerUtil.PICTURE_TYPE) {
                MusicPlayer.getInstance().startMusic(banner.getMusicList());
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setPageListener(onPageChangeListener listener) {
        this.listener = listener;
    }
}
