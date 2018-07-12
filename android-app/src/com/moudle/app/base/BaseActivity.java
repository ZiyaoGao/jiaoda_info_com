package com.moudle.app.base;


import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.moudle.app.interf.BaseViewInterface;
import com.moudle.app.AppManager;

/**
 * @Description 应用程序Activity的基类
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class BaseActivity extends RootActivity implements BaseViewInterface {
    // 是否允许销毁
    private boolean allowDestroy = true;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    public void setAllowDestroy(boolean allowDestroy) {
        this.allowDestroy = allowDestroy;
    }

    public void setAllowDestroy(boolean allowDestroy, View view) {
        this.allowDestroy = allowDestroy;
        this.view = view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
            view.onKeyDown(keyCode, event);
            if (!allowDestroy) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 错误消息提醒
     *
     * @param editText
     * @param id
     */
    public void errorPrompt(EditText editText, int id) {
        String prompt = getResources().getText(id).toString();
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(prompt);
        ssbuilder.setSpan(0, 0, prompt.length(), 0);
        editText.requestFocus();
        editText.setError(ssbuilder);
    }

    @Override
    public void initView() {
        getTitleModel().creatLeftView(ImageView.class);
    }

    @Override
    public void initData() {

    }
}
