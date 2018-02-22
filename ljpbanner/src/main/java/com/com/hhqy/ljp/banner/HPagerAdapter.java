package com.com.hhqy.ljp.banner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 此类由  刘建培yuxue  创建于 2017/6/16.
 * ViewPager的适配器
 */
class HPagerAdapter extends PagerAdapter implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "HPagerAdapter";
    private View view = null;
    private VideoView videoPlaying;
    private HViewPage banner;
    /**
     * 自定义加载图片的接口
     */
    private ImageLoader imageLoader = null;

    public HPagerAdapter(HViewPage banner) {
        this.banner = banner;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return banner.getPlayListSize();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String path = banner.getPlayList().get(position);
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            Log.e(TAG, "instantiateItem: 路径为null或文件不存在，path=" + path);
            return null;
        }
        if (BannerUtil.judgeType(path) == BannerUtil.PICTURE_TYPE) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (imageLoader != null) {
                imageLoader.loadImage(imageView, path);
            } else {
                imageView.setImageBitmap(getimage(path));
            }
            view = imageView;
        } else if (BannerUtil.judgeType(path) == BannerUtil.VIDEO_TYPE) {
            VideoView videoView = new VideoView(container.getContext()) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    //使VideoView可以全屏播放
                    int width = getDefaultSize(0, widthMeasureSpec);
                    int height = getDefaultSize(0, heightMeasureSpec);
                    setMeasuredDimension(width, height);
                }
            };
            videoView.setMediaController(null);
            videoView.setVideoURI(Uri.parse(path));
            videoView.setOnCompletionListener(this);
            videoView.setOnErrorListener(this);
            view = videoView;
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position == 0 || position == (banner.getPlayListSize() - 1) || banner.getIsTouch()) {//末尾两端的数据源不做处理
            return;
        }
        if (object != null && object instanceof VideoView) {
            videoPlaying = (VideoView) object;
            videoPlaying.setVisibility(View.VISIBLE);
            playVideo();
            MusicPlayer.getInstance().pauseMusic();
        } else {
            stopVideo();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (banner.getAutoPlay()) {
            mp.setLooping(false);
            banner.nextPage();
        } else {
            mp.setLooping(true);
            playVideo();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    /**
     * 播放视频
     */
    public void playVideo() {
        if (videoPlaying != null) {
            videoPlaying.start();
            videoPlaying.seekTo(0);//从头开始播放
            videoPlaying.requestFocus();
            videoPlaying.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 查询视频是否在播放
     *
     * @return ture：正在播放视频
     */
    public boolean isPlaying() {
        if (videoPlaying == null) {
            return false;
        }
        return videoPlaying.isPlaying();
    }

    /**
     * 调用VideoView暂停视频播放
     */
    public void pauseVideo() {
        if (videoPlaying != null) videoPlaying.pause();
    }

    /**
     * 调用VideoView开始视频播放
     */
    public void startVideo() {
        if (videoPlaying != null) videoPlaying.start();
    }

    /**
     * 停止播放视频
     */
    public void stopVideo() {
        if (videoPlaying != null && videoPlaying.isPlaying()) {
            videoPlaying.pause();
            videoPlaying.setVisibility(View.GONE);
        }
    }

    /**
     * 释放内存
     */
    public void recycle() {
        if (videoPlaying != null) {
            videoPlaying.stopPlayback();
        }
        videoPlaying = null;
        banner = null;
        imageLoader = null;
    }

    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 150) { // 循环判断如果压缩后图片是否大于150kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
