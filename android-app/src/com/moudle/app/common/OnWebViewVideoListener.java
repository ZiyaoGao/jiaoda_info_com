package com.moudle.app.common;

/**
 * 监听webview上的视频
 */
public interface OnWebViewVideoListener {

    /**
     * 点击webview上的图片，传入视频Url
     *
     * @param videoUrl
     */
    void onVideoClick(String videoUrl);

}
