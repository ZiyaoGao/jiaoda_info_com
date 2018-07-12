package com.moudle.app.ui;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.SimpleBackPage;

import java.lang.ref.WeakReference;

/**
 * @Description 所有回退类Fragment关联这个Activity
 * @Author Li Chao
 * @Date 2016/1/4 10:21
 */
public class SimpleBackActivity extends BaseActivity {
    public final static String BUNDLE_KEY_PAGE = "BUNDLE_KEY_PAGE";
    public final static String BUNDLE_KEY_ARGS = "BUNDLE_KEY_ARGS";
    private static final String TAG = "FLAG_TAG";
    protected WeakReference<Fragment> mFragment;
    protected int mPageValue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_fragment);
        initView();
        init(savedInstanceState);
    }


    private void init(Bundle savedInstanceState) {
        if (mPageValue == -1) {
            mPageValue = getIntent().getIntExtra(BUNDLE_KEY_PAGE, 0);
        }
        initFromIntent(mPageValue, getIntent());
    }

    private void initFromIntent(int pageValue, Intent data) {
        if (data == null) {
            throw new RuntimeException("you must provide a page info to display");
        }
        SimpleBackPage page = SimpleBackPage.getPageByValue(pageValue);
        getTitleModel().creatCenterView(TextView.class).setText(page.getTitle());
        getTitleModel().submit();
        if (page == null) {
            throw new IllegalArgumentException("can not find page by value:" + pageValue);
        }
        try {
            Fragment fragment = (Fragment) page.getClz().newInstance();
            Bundle args = data.getBundleExtra(BUNDLE_KEY_ARGS);
            if (args != null) {
                fragment.setArguments(args);
            }
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, fragment, TAG);
            trans.commitAllowingStateLoss();
            mFragment = new WeakReference<Fragment>(fragment);
        } catch (Exception e) {
            AppException.run(e);
            throw new IllegalArgumentException("generate fragment error. by value:" + pageValue);
        }
    }

    @Override
    public void onBackPressed() {
        BaseFragMent fragment = (BaseFragMent) getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onBackPressed();
    }

}
