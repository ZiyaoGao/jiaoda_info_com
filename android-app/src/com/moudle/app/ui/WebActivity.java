package com.moudle.app.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;


/**
 * @Description 空白界面 用于提示Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class WebActivity extends BaseActivity {
    private AppContext appContext;
    private WebView mWebView;

    // private ProgressBar mWebProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
        initData();
        appContext = (AppContext) getApplicationContext();
    }

    @Override
    public void initView() {
        mWebView = (WebView) findViewById(R.id.web_View);
        //mWebProgress= (ProgressBar) findViewById(R.id.web_progress);
    }

    @Override
    public void initData() {
        String headTitle = getIntent().getStringExtra("headTitle");
     //   ((TextView) findViewById(R.id.acitivity_name)).setText(headTitle);
        mWebView.loadUrl("http://www.baidu.com");
        //设置浏览器为当前窗口
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onProgressChanged(WebView view, int progress) {
                Log.e("progress", String.valueOf(progress));
            }
        });
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            //设置进度条
//            public void onProgressChanged(WebView view, int progress) {
//                if (progress == 100) {
//                    mWebProgress.setVisibility(View.INVISIBLE);
//                } else {
//                    if (View.INVISIBLE == mWebProgress.getVisibility()) {
//                        mWebProgress.setVisibility(View.VISIBLE);
//                    }
//                    mWebProgress.setProgress(progress);
//                }
//                super.onProgressChanged(view, progress);
//            }
//        });
        // 设置浏览器是否加载JS，是否支持窗口大小
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
