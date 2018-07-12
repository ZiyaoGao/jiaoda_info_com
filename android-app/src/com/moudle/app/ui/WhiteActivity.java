package com.moudle.app.ui;


import android.os.Bundle;
import android.view.KeyEvent;

import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.AppContext;


/**
 * @Description 空白界面 用于提示Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class WhiteActivity extends BaseActivity {
    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white);
        initView();
        appContext = (AppContext) getApplicationContext();
    }

    @Override
    public void initView() {
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
