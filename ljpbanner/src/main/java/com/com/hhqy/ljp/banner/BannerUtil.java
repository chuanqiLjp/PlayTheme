package com.com.hhqy.ljp.banner;

import android.text.TextUtils;

/**
 * Created by yuxue on 2017/7/25.
 */

public class BannerUtil {
    /**
     * 图片类型
     */
    public static final int PICTURE_TYPE = 1;
    /**
     * 视频类型
     */
    public static final int VIDEO_TYPE = 2;
    /**
     * 音乐类型
     */
    public static final int MUSIC_TYPE = 3;
    /**
     * 无法识别的类型
     */
    public static final int UNKNOWN_TYPE = 4;

    /**
     * 判断图片、视频、音频的类型
     *
     * @param name 文件名或文件的绝对路径
     * @return PICTURE_TYPE：图片类型，VIDEO_TYPE：视频类型 ，MUSIC_TYPE：音乐类型，UNKNOWN_TYPE：无法识别的类型
     */
    public static int judgeType(String name) {
        if (TextUtils.isEmpty(name)) {
            return UNKNOWN_TYPE;
        }
        String lowerPath = name.toLowerCase();
        if (lowerPath.endsWith(".bmp") || lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")
                || lowerPath.endsWith(".webp") || lowerPath.endsWith(".png") || lowerPath.endsWith(".gif")) {
//图片的所有格式：BMP EMF EPS JPG MET PGM PNG GIF ODD OTG PBM PCT PDF PPM RAS STD SVM PSD TIF SXD ICO TGA TIFF VOR WBMP WEBP XPM
            return PICTURE_TYPE;
        } else if (lowerPath.endsWith(".mp4") || lowerPath.endsWith(".flv") || lowerPath.endsWith(".3gp")
                || lowerPath.endsWith(".avi") || lowerPath.endsWith(".wmv")) {
            //支持的视频格式：avi,3gp,flv,mp4,WMV(部分支持)
            //不支持的格式：swf(Flash),WMV(部分不支持),
            return VIDEO_TYPE;
        } else if (lowerPath.endsWith(".mp3") || lowerPath.endsWith(".wav")) {
            return MUSIC_TYPE;
        }
        return UNKNOWN_TYPE;
    }
}
