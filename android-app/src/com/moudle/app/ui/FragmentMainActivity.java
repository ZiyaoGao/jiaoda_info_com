package com.moudle.app.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import java.util.Timer;
import java.util.TimerTask;

import com.moudle.app.AppContext;
import com.moudle.app.AppManager;
import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.bean.Constant;
import com.moudle.app.common.ScreenUtils;
import com.moudle.app.common.SystemBarTintManager;
import com.moudle.app.common.UIHelper;
import com.moudle.app.fragment.InfoMenuFragment;
import com.moudle.app.fragment.MyInfoFragment;
import com.moudle.app.fragment.QuerySchollFragment;
import com.moudle.app.widget.TitleModel;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


/**
 * @Description 主Activity 一共2个模块 用Fragment进行管理 一个侧滑界面 一个主界面
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class FragmentMainActivity extends BaseActivity implements OnClickListener {
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;
    private LinearLayout mLlOne;
    private LinearLayout mLlTwo;
    private LinearLayout mLlThree;
    private AppContext appContext;
    private SlidingMenu localSlidingMenu;
    private View mMainView;
    private LinearLayout mContent;
    private LinearLayout mMenuView;
    private TitleModel mTitleModel;
    private TextView user_name;
    private TextView tv_number;
    private AlertDialog mAlertDlg = null;
    private MyInfoFragment myInfoFragment = new MyInfoFragment();
    private QuerySchollFragment queryInfoFragment = new QuerySchollFragment();
    private InfoMenuFragment infoMenuFragment = new InfoMenuFragment();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.MOUDLE_ACTION_USER_SIGN)) {
                tv_number.setVisibility(View.VISIBLE);
                user_name.setText(appContext.user.getName());
                tv_number.setText(appContext.user.getNumber());
            } else if (action.equals(Constant.MOUDLE_ACTION_USER_CHANGE)) {
                tv_number.setVisibility(View.GONE);
                user_name.setText("未登录");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (AppContext) getApplicationContext();
//        if (appContext != null && appContext.isCheckUp()) {
//            UpdateManager.getUpdateManager().checkAppUpdate(this, false);
//        }
        fragmentManager = getSupportFragmentManager();
        mMainView = LayoutInflater.from(this).inflate(R.layout.fragment_slide_main, null);
        setContentView(mMainView);
        initView();
        initStatusBar();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.above_main, infoMenuFragment).add(R.id.above_main, myInfoFragment).add(R.id.above_main, queryInfoFragment);
//        if (appContext.isLogin()) {
//            tv_number.setVisibility(View.VISIBLE);
//            user_name.setText(appContext.user.getName());
//            tv_number.setText(appContext.user.getNumber());
//            mContent.findViewById(R.id.rl_bg).setVisibility(View.GONE);
//        } else {
//            hideFragments(transaction);
//        }
        hideFragments(transaction);
        transaction.commit();
        mAlertDlg = new AlertDialog.Builder(this).create();
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 改变titlebar的高度
            int statusbarHeight = new SystemBarTintManager(this).getConfig().getStatusBarHeight();
            mContent.setPadding(0, statusbarHeight, 0, 0);
            mMenuView.setPadding(0, statusbarHeight, 0, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void initView() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.MOUDLE_ACTION_USER_SIGN);
        filter.addAction(Constant.MOUDLE_ACTION_USER_CHANGE);
        filter.addAction(Constant.MOUDLE_ACTION_USER_OPTIONMSG_CHANGE);
        registerReceiver(mReceiver, filter);
        mTitleModel = new TitleModel(this);
        ImageView leftView = mTitleModel.creatLeftView(ImageView.class);
        leftView.setImageResource(R.drawable.demo_tribe_red_icon);
        leftView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                localSlidingMenu.toggle();
            }
        });
//        TextView rightView = mTitleModel.creatRightView(TextView.class);
//        rightView.setText("+");
//        rightView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        mTitleModel.submit();
        localSlidingMenu = (SlidingMenu) findViewById(R.id.slidingmenulayout);
        localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);//分界处宽度
        localSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mContent = (LinearLayout) localSlidingMenu.getContent();
        mMenuView = (LinearLayout) localSlidingMenu.getMenu();
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.convertDIP2PX(this, 50));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        mContent.addView(mTitleModel.getTitleView(), 0, llp);
        user_name = (TextView) mMenuView.findViewById(R.id.user_name);
        tv_number = (TextView) mMenuView.findViewById(R.id.tv_number);
        mLlOne = (LinearLayout) findViewById(R.id.ll_one);
        mLlTwo = (LinearLayout) findViewById(R.id.ll_two);
        mLlThree = (LinearLayout) findViewById(R.id.ll_three);
        //mLlThree = (LinearLayout) findViewById(R.id.ll_three);
        mLlOne.setOnClickListener(this);
        mLlTwo.setOnClickListener(this);
        mLlThree.setOnClickListener(this);
        //     mLlThree.setOnClickListener(this);
//        localSlidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//            @Override
//            public void transformCanvas(Canvas canvas, float percentOpen) {
//                float scale = (float) (percentOpen * 0.25 + 0.75);
//                canvas.scale(scale, scale, -canvas.getWidth() / 2,
//                        canvas.getHeight() / 2);
//            }
//        });
//        localSlidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//            @Override
//            public void transformCanvas(Canvas canvas, float percentOpen) {
//                float scale = (float) (1 - percentOpen * 0.25);
//                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        mContent.findViewById(R.id.rl_bg).setVisibility(View.GONE);
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (v.getId()) {
            case R.id.ll_one://信息
                localSlidingMenu.toggle();
                transaction.show(infoMenuFragment).commit();
                break;
            case R.id.ll_two://联系人
                localSlidingMenu.toggle();
                transaction.show(queryInfoFragment).commit();
                break;
            case R.id.ll_three://我
                localSlidingMenu.toggle();
                transaction.show(myInfoFragment).commit();
                break;
            default:
                break;
        }
    }


    /**
     * 必须重写该方法，否则fragment将不响应onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        transaction.hide(infoMenuFragment);
        transaction.hide(queryInfoFragment);
        transaction.hide(myInfoFragment);
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean b = localSlidingMenu.onKeyUp(keyCode, event);
        if (b) return b;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return true;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;


    private void exitBy2Click() {
        if (isExit == false) {
            isExit = true; // 准备退出
            UIHelper.ToastMessage(FragmentMainActivity.this, getString(R.string.app_double_exit),
                    Toast.LENGTH_SHORT);
            Timer tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            AppManager.getAppManager().AppExit(FragmentMainActivity.this);
            System.exit(0);
        }
    }

    public void showQueryInfo() {
        queryInfoFragment.refresh();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        transaction.show(queryInfoFragment).commit();
    }
}
