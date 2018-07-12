package com.moudle.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.moudle.app.AppConfig;
import com.moudle.app.R;
import com.moudle.app.adapter.GuidePagerAdapter;
import com.moudle.app.common.CreateShut;

/**
 * @Description 第一次运行的引导页代码 Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class GuideActivity extends Activity implements OnPageChangeListener,
        OnClickListener {
    private Context context;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Button mBtnstart;
    private LinearLayout mLayIndicator;
    private ArrayList<View> mViews;
    private ImageView[] mIndicators = null;
    private int[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        context = this;
        // 创建桌面快捷方式
        new CreateShut(this);
        // 设置引导图片
        images = new int[]{R.drawable.welcome, R.drawable.welcome,
                R.drawable.welcome};
        initView();
    }

    // 初始化视图
    private void initView() {
        // 实例化视图控件
        mViewPager = (ViewPager) findViewById(R.id.viewpage);
        mBtnstart = (Button) findViewById(R.id.start_Button);
        mBtnstart.setOnClickListener(this);
        mLayIndicator = (LinearLayout) findViewById(R.id.indicator);
        mViews = new ArrayList<View>();
        mIndicators = new ImageView[images.length]; // 定义指示器数组大小
        for (int i = 0; i < images.length; i++) {
            // 循环加入图片
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(images[i]);
            mViews.add(imageView);
            // 循环加入指示器
            mIndicators[i] = new ImageView(context);
            mIndicators[i].setBackgroundResource(R.drawable.indicators_default);
            if (i == 0) {
                mIndicators[i].setBackgroundResource(R.drawable.indicators_now);
            }
            mLayIndicator.addView(mIndicators[i]);
        }
        mPagerAdapter = new GuidePagerAdapter(mViews);
        mViewPager.setAdapter(mPagerAdapter); // 设置适配器
        mViewPager.setOnPageChangeListener(this);
    }

    //按钮的点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_Button) {
            SharedPreferences shared = AppConfig.getSharedPreferences(this);
            shared.edit().putBoolean("First", false).commit();

            startActivity(new Intent(GuideActivity.this, FragmentMainActivity.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            this.finish();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 监听viewpage
    @Override
    public void onPageSelected(int arg0) {
        // 显示最后一个图片时显示按钮
        if (arg0 == mIndicators.length - 1) {
            mBtnstart.setVisibility(View.VISIBLE);
        } else {
            mBtnstart.setVisibility(View.INVISIBLE);
        }
        // 更改指示器图片
        for (int i = 0; i < mIndicators.length; i++) {
            mIndicators[arg0].setBackgroundResource(R.drawable.indicators_now);
            if (arg0 != i) {
                mIndicators[i]
                        .setBackgroundResource(R.drawable.indicators_default);
            }
        }
    }
}
