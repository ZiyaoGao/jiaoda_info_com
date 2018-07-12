package com.moudle.app.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.bean.Constant;
import com.moudle.app.bean.Entity;
import com.moudle.app.bean.User;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.MiddleClient;

import java.io.Serializable;


/**
 * @Description 用户信息修改 例性别
 * @Author Li Chao
 * @Date 2015/12/17 13:29
 */
public class UserInfoModifyActivity extends BaseActivity {
    private AppContext appContext;
    private Button mBtnSubmit; //提交按钮
    private EditText mModifyContent;
    private String content;
    private int userFlag;// 用户修改意图
    private RadioGroup mContentLay;//男女选择
    private TextView mHeadName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_modify);
        appContext = (AppContext) getApplicationContext();
        initView();
        initData();
    }


    @Override
    public void initView() {
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
    }


    @Override
    public void initData() {
        userFlag = getIntent().getIntExtra(User.USERMODIFY, -1);
        switch (userFlag) {
            case User.NICKNAME:
                mHeadName.setText(R.string.user_info_nickname_modify);
                findViewById(R.id.content_lay).setVisibility(View.VISIBLE);
                mModifyContent = (EditText) findViewById(R.id.modify_content);
                break;
            case User.GENDER:
                mHeadName.setText(R.string.user_info_gender_title);
                mContentLay = (RadioGroup) findViewById(R.id.gender_lay);
                if (appContext.user.gender.equals("1")) {
                    ((RadioButton) mContentLay.getChildAt(0)).setChecked(true);
                } else {
                    ((RadioButton) mContentLay.getChildAt(2)).setChecked(true);
                }
                mContentLay.setVisibility(View.VISIBLE);
                mContentLay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Log.e("checkedId", String.valueOf(checkedId));
                    }
                });
                break;
        }
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (userFlag) {
                    case User.NICKNAME:
                        content = mModifyContent.getText().toString();
                        if (StringUtils.isEmpty(content)) {
                            errorPrompt(mModifyContent, R.string.login_phone_prompt);
                            return;
                        }
                        appContext.user.name = content;
                        new UserHeadPortrait().execute(appContext.user.loginId, content);
                        break;
                    case User.GENDER:
                        String gender = String.valueOf(mContentLay.getCheckedRadioButtonId());
                        int sel = Integer.valueOf(gender) % 2;//TODO 重复进该Activity 这个选择值会不断叠加 原因是未释放资源  治标方法
                        if (sel == 0) {
                            appContext.user.gender = gender = "2";
                        } else {
                            appContext.user.gender = gender = "1";
                        }
                        new UserHeadPortrait().execute(appContext.user.loginId, gender);
                        break;
                }
            }
        });
    }

    private class UserHeadPortrait extends AsyncTask<String, Void, Serializable> {
        @Override
        protected Serializable doInBackground(String... params) {
            Serializable serializable = null;
            try {
                switch (userFlag) {
                    case User.NICKNAME:
                        serializable = MiddleClient.modifyUserNickName(params[0], params[1]);
                        break;
                    case User.GENDER:
                        serializable = MiddleClient.modifyUserGender(params[0], params[1]);
                        break;
                }
            } catch (AppException e) {
                AppException.run(e);
            }

            return serializable;
        }

        @Override
        protected void onPostExecute(Serializable result) {
            if (result != null) {
                if (((Entity) result).isStatus()) {
                    switch (userFlag) {
                        case User.NICKNAME:
                            UIHelper.ToastMessage(UserInfoModifyActivity.this, getString(R.string.user_name_modify_success));
                            break;
                        case User.GENDER:
                            UIHelper.ToastMessage(UserInfoModifyActivity.this, getString(R.string.user_gender_modify_success));
                            break;
                    }
                } else {
                    switch (userFlag) {
                        case User.NICKNAME:
                            UIHelper.ToastMessage(UserInfoModifyActivity.this, getString(R.string.user_name_modify_success));
                            break;
                        case User.GENDER:
                            UIHelper.ToastMessage(UserInfoModifyActivity.this, getString(R.string.user_gender_modify_fail));
                            break;
                    }
                }
                sendBroadcast(new Intent(Constant.MOUDLE_ACTION_USER_CHANGE));
                finish();
            } else {
                AppException.getAppExceptionHandler().makeToast(UserInfoModifyActivity.this);
            }
        }
    }
}
