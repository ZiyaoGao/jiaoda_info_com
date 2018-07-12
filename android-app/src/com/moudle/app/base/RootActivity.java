package com.moudle.app.base;/**
 * Created by x on 16-4-14.
 */

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moudle.app.R;
import com.moudle.app.common.ScreenUtils;
import com.moudle.app.common.SystemBarTintManager;
import com.moudle.app.widget.TitleModel;

/**
 * @Description
 * @Author Li Chao
 * @Date 16-4-14 10:15
 */
public class RootActivity extends FragmentActivity {
    private TitleModel titleModel;
    private boolean isSetID = false;

    protected boolean titleVertical() {
        return true;
    }

    SystemBarTintManager tintManager;

    @Override
    public void setContentView(int layoutResID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.home_top);//通知栏所需颜色
        }
        isSetID = true;
        titleModel = new TitleModel(this);
        if (titleVertical()) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.convertDIP2PX(this, 50));
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(titleModel.getTitleView(), llp);
            LayoutInflater.from(this).inflate(layoutResID, layout, true);
            setContentView(layout);
            if (tintManager != null) {
                SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
                layout.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            }
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.convertDIP2PX(this, 50));
            RelativeLayout rl_title_vertical = new RelativeLayout(this);
            LayoutInflater.from(this).inflate(layoutResID, rl_title_vertical, true);
            rl_title_vertical.addView(titleModel.getTitleView(), lp);
            setContentView(rl_title_vertical);
            if (tintManager != null) {
                SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
                rl_title_vertical.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            }
        }
    }

    public TitleModel getTitleModel() {
        return titleModel;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
