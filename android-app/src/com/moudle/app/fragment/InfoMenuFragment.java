package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.IdCard;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.Compress;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.UpImageHelp;
import com.moudle.app.ui.FragmentMainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 信息菜单
 * Created by Administrator on 2016/5/7.
 */
public class InfoMenuFragment extends BaseFragMent {
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
        if (AppContext.user.postition.equals("普通教师")) {
            btn_one.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.btn_one:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ADD_INFO, null);
                break;
            case R.id.btn_two:
                args.putInt("infoType", 0);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_INFO, args);
                break;
            case R.id.btn_three:
                args.putInt("infoType", 1);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_INFO, args);
                break;
            case R.id.btn_four:
                args.putInt("infoType", 2);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_INFO, args);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
