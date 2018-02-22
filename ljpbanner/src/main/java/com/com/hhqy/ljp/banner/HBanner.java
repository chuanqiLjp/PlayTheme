package com.com.hhqy.ljp.banner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ljpbanner.R;

import java.util.List;

/**
 * Created by yuxue on 2017/6/25.
 */

public class HBanner extends FrameLayout {
    private static final String TAG = "HBanner";
    private  final  String version="Banner-1.0.3";
    public static boolean DEBUG = false;
    private HViewPage viewPage;
    private LinearLayout pointIndicatorLayout;

    public HBanner(@NonNull Context context) {
        this(context, null);
    }

    public HBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HBanner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewPage = new HViewPage(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(viewPage, layoutParams);
        viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (HBanner.DEBUG)
                    Log.e(TAG, "onPageSelected: HBanner-->page=" + position);
                createIndicator(getRealCount(), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pointIndicatorLayout = new LinearLayout(context);
        pointIndicatorLayout.setVerticalGravity(LinearLayout.HORIZONTAL);
        pointIndicatorLayout.setBackgroundColor(Color.argb(0, 255, 100, 100));
        pointIndicatorLayout.setPadding(10, 10, 10, 10);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        addView(pointIndicatorLayout, lp);
    }

    /**
     * 与Activity的生命周期进行绑定  当 onResume() 时调用
     */
    public void onResume() {
        viewPage.onResume();
    }

    /**
     * 与Activity的生命周期进行绑定 当 onPause()时调用
     */
    public void onPause() {
        viewPage.onPause();
    }

    /**
     * 与Activity的生命周期进行绑定 当 onDestory()时调用
     */
    public void onDestory() {
        viewPage.onDestory();
        removeAllViews();
        viewPage = null;
    }


    public int getRealCount() {
        return viewPage.getRealCount();
    }

    public boolean getIsTouch() {
        return viewPage.getIsTouch();
    }

    /**
     * 设置图片播放的间隔时间
     *
     * @param delayTime 间隔时长，单位：秒
     * @return
     */
    public HBanner setDelayTime(int delayTime) {
        viewPage.setDelayTime(delayTime);
        return this;
    }

    public int getDelayTime() {
        return viewPage.getDelayTime();
    }

    /**
     * 设置是否允许自动播放
     *
     * @param isAutoPlay ture：表示自动播放
     * @return
     */
    public HBanner setIsAutoPlay(boolean isAutoPlay) {
        viewPage.setIsAutoPlay(isAutoPlay);
        return this;
    }

    public boolean getAutoPlay() {
        return viewPage.getAutoPlay();
    }

    /**
     * 设置播放列表，一个List可以包含图片或视频的绝对路径
     *
     * @param playList 可以包含图片或视频的绝对路径
     * @return
     */
    public HBanner setPlayList(List<String> playList) {
        viewPage.setPlayList(playList);
        return this;
    }

    public List<String> getPlayList() {
        return viewPage.getPlayList();
    }

    public int getPlayListSize() {
        return viewPage.getPlayListSize();
    }

    /**
     * 设置背景音乐的播放列表
     *
     * @param musicList 背景音乐的播放列表
     */
    public HBanner setMusicList(List<String> musicList) {
        viewPage.setMusicList(musicList);
        return this;
    }

    /**
     * 设置允许手势滑动操作
     *
     * @param isScrollable ture：可以使用手势进行左右滑动
     * @return
     */
    public HBanner setGestureScroll(boolean isScrollable) {
        viewPage.setGestureScroll(isScrollable);
        return this;
    }

    public boolean getGestureScroll() {
        return viewPage.getGestureScroll();
    }

    public HBanner setImageLoader(ImageLoader imageLoader) {
        viewPage.setImageLoader(imageLoader);
        return this;
    }

    public HBanner setOnPageChangeListener(onPageChangeListener listener) {
        viewPage.setPageListener(listener);
        return this;
    }

    public HBanner setStartPage(int startPage) {
        viewPage.setStartPage(startPage);
        return this;
    }

    public int getStartPage() {
        return viewPage.getStartPage();
    }

    public HBanner setOffscreenPageLimit(int limit) {
        viewPage.setOffscreenPageLimit(limit);
        return this;
    }

    public int getOffscreenPageLimit() {
        return viewPage.getOffscreenPageLimit();
    }

    /**
     * 启动播放
     */
    public void startPlay() {
        viewPage.startPlay();
        setVisibility(VISIBLE);
        if (getRealCount() == 1) {
            pointIndicatorLayout.setVisibility(GONE);
        } else {
            pointIndicatorLayout.setVisibility(VISIBLE);
            createIndicator(getRealCount(), 1);
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        viewPage.stopPlay();
        setVisibility(GONE);
    }

    /**
     * 开始自动播放
     */
    public void startAutoPlay() {
        viewPage.startAutoPlay();
        setVisibility(VISIBLE);
        if (getRealCount() == 1) {
            pointIndicatorLayout.setVisibility(GONE);
        } else {
            pointIndicatorLayout.setVisibility(VISIBLE);
            createIndicator(getRealCount(), 1);
        }
    }

    private void createIndicator(int total, int current) {
        pointIndicatorLayout.removeAllViews();
        ImageView imageView = null;
//        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams layoutParams = new LayoutParams(40, 40);
        for (int i = 1; i <= total; i++) {
            imageView = new ImageView(getContext());
            if (current == i) {
                imageView.setImageResource(R.drawable.white_radius);
            } else {
                imageView.setImageResource(R.drawable.gray_radius);
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(10, 10, 10, 10);
            pointIndicatorLayout.addView(imageView, layoutParams);
        }
    }

    /**
     * 停止自动播放
     */
    public void stopAutoPlay() {
        viewPage.stopAutoPlay();
    }

    /**
     * 下一页
     */
    public void nextPage() {
        viewPage.setCurrentItem(viewPage.getCurrentItem() + 1);
    }

    /**
     * 上一页
     */
    public void previousPage() {
        viewPage.setCurrentItem(viewPage.getCurrentItem() - 1);
    }

    public HBanner setDebug(boolean debug) {
        DEBUG = debug;
        return this;
    }

    public String getVersion(){
        return version;
    }
}
