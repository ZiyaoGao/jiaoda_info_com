package com.moudle.app.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.common.UIHelper;


/**
 * @Description 我的模块-帮助Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class HelpActivity extends BaseActivity {
    private AppContext appContext;
    private LinearLayout mLayHelpCenter;
    private LinearLayout mLayFeedBack;
    private LinearLayout mLayOnlineService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initView();
        appContext = (AppContext) getApplicationContext();
    }
    @Override
    public void initView() {
       // ((TextView) findViewById(R.id//.acitivity_name)).setText(R.string.my_bangzhu);
        mLayHelpCenter = (LinearLayout) findViewById(R.id.help_center);
        mLayFeedBack = (LinearLayout) findViewById(R.id.feed_back);
        mLayOnlineService = (LinearLayout) findViewById(R.id.online_service);
        mLayHelpCenter.setOnClickListener(helpOnclick);
        mLayFeedBack.setOnClickListener(helpOnclick);
        mLayOnlineService.setOnClickListener(helpOnclick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener helpOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.feed_back:
                UIHelper.showFeedBack(HelpActivity.this);
                break;
            default:
                UIHelper.showWhite(HelpActivity.this);
                break;
            }
        }
    };
}
