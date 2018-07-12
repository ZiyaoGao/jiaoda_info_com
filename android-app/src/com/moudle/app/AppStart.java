package com.moudle.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.moudle.app.common.StringUtils;
import com.moudle.app.ui.GuideActivity;
import com.moudle.app.ui.FragmentMainActivity;
import com.moudle.app.ui.LoginActivity;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 */
public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.start, null);
        setContentView(view);

        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

        //兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
        AppContext appContext = (AppContext) getApplicationContext();
        String cookie = appContext.getProperty("cookie");
        if (StringUtils.isEmpty(cookie)) {
            String cookie_name = appContext.getProperty("cookie_name");
            String cookie_value = appContext.getProperty("cookie_value");
            if (!StringUtils.isEmpty(cookie_name) && !StringUtils.isEmpty(cookie_value)) {
                cookie = cookie_name + "=" + cookie_value;
                appContext.setProperty("cookie", cookie);
                appContext.removeProperty("cookie_domain", "cookie_name", "cookie_value", "cookie_version", "cookie_path");
            }
        }
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        SharedPreferences shared = AppConfig.getSharedPreferences(this);
        boolean b = shared.getBoolean("First", true);
        Intent intent;
        /**
         * @TODO 强制引导页
         */
        if (true) {
            intent = new Intent(this, GuideActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}