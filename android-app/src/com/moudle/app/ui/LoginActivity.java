package com.moudle.app.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.fragment.LoginFragment;


/**
 * @Description 登陆Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class LoginActivity extends BaseActivity {
    private FragmentManager fm;
    private LoginFragment mLoginFragment;
    public final static int REQUEST_CODE = 0;
    public final static int REQUEST_REG = 1;
    public final static int REQUEST_FORGET_PWD = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fm = getSupportFragmentManager();
        mLoginFragment = new LoginFragment();
        fm.beginTransaction().add(R.id.login_frame, mLoginFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0)
            fm.beginTransaction().setCustomAnimations(R.anim.slide_left_enter, R.anim.slide_right_exit).remove(fm.getFragments().get(fm.getFragments().size() - 1)).commit();
        super.onBackPressed();
    }
}