package com.moudle.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.Constant;

/**
 * @Description 目前只是展示侧滑界面 ,以后要更改
 */
public class SlideFragment extends BaseFragMent {
    private View mView;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = mInflater.inflate(R.layout.fragment_slide, container, false);
            initView(mView);
        } else {
            initView(mView);
        }
        return mView;
    }

    @Override
    public void initView(View v) {
        limitInit();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.MOUDLE_ACTION_USER_SIGN);
        filter.addAction(Constant.MOUDLE_ACTION_USER_CHANGE);
        filter.addAction(Constant.MOUDLE_ACTION_USER_OPTIONMSG_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private View.OnClickListener mineHeaderClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                default:
                    break;
            }
        }
    };

    /**
     * 有限制的初始化
     */
    private void limitInit() {


    }

    private void executeOnLoadDataError() {
    }
}
