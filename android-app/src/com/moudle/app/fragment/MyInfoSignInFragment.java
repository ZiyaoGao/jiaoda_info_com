package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.Compress;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.UpImageHelp;
import com.moudle.app.ui.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 我的标记
 * Created by Administrator on 2016/5/7.
 */
public class MyInfoSignInFragment extends BaseFragMent {
    private Button btn_one, btn_two, btn_three, btn_four;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_info_menu, null);
        initView(v);
        initData();
        return v;
    }

    public void initView(View v) {
        btn_one = (Button) v.findViewById(R.id.btn_one);
        btn_two = (Button) v.findViewById(R.id.btn_two);
        btn_three = (Button) v.findViewById(R.id.btn_three);
        btn_four = (Button) v.findViewById(R.id.btn_four);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_one.setText("不重要");
        btn_two.setText("一般");
        btn_three.setText("重要");
        btn_four.setText("非常重要");
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.btn_one:
                args.putInt("signType",1);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_SIGN_IN_INFO, args);
                break;
            case R.id.btn_two:
                args.putInt("signType",2);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_SIGN_IN_INFO, args);
                break;
            case R.id.btn_three:
                args.putInt("signType",3);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_SIGN_IN_INFO, args);
                break;
            case R.id.btn_four:
                args.putInt("signType",4);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_SIGN_IN_INFO, args);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
