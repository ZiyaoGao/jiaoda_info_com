package com.moudle.app.fragment;

import android.app.AlertDialog;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moudle.app.AppConfig;
import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.base.BaseListFragment;
import com.moudle.app.base.RootActivity;
import com.moudle.app.bean.Constant;
import com.moudle.app.bean.Entity;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.bean.User;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.ui.FragmentMainActivity;
import com.moudle.app.ui.LoginActivity;
import com.moudle.app.widget.EmptyLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * @Description 登陆模块
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class LoginFragment extends BaseFragMent {
    private Button mBtnMessage;// 短信验证登陆
    private Button mBtnLogin;// 普通登陆
    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button btn_register;// 注册
    //    private TextView mTvRegister;
    private String mUsername;
    private String mPassword;
    private ImageButton mIbBack;// 回退按钮
    private RegFragment mRegFragment;
    private CheckBox mCbPassword;// 密码是否明文显示
    private InputMethodManager mImm;
    private AlertDialog mAlertDlg = null;
    private FragmentManager fm;
    private CheckBox cb_rember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_login, null);
        mAlertDlg = new AlertDialog.Builder(getActivity()).create();
        fm = getActivity().getSupportFragmentManager();
        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(v);
        initData();
        return v;
    }

    public void initView(View v) {
        ((LoginActivity) getActivity()).getTitleModel().creatCenterView(TextView.class).setText("登录");
        ((LoginActivity) getActivity()).getTitleModel().submit();
        //  ((TextView) v.findViewById(R.id.acitivity_name)).setText(R.string.app_login);
        mBtnLogin = (Button) v.findViewById(R.id.btn_login);
        //   mTvRegister = (TextView) v.findViewById(R.id.btn_register);
        mEtUsername = (EditText) v.findViewById(R.id.login_username);
        mEtPassword = (EditText) v.findViewById(R.id.login_password);
        btn_register = (Button) v.findViewById(R.id.btn_register);
        mBtnMessage = (Button) v.findViewById(R.id.btn_message);
        //  mIbBack = (ImageButton) v.findViewById(R.id.btn_back);
        mCbPassword = (CheckBox) v.findViewById(R.id.password_checkbox);
        cb_rember = (CheckBox) v.findViewById(R.id.cb_rember);
        mBtnLogin.setOnClickListener(listener);
        btn_register.setOnClickListener(listener);

        mBtnMessage.setOnClickListener(listener);
        //mIbBack.setOnClickListener(listener);
        mCbPassword.setOnCheckedChangeListener(changeListener);
        // 打开软键盘
        mImm.showSoftInput(mEtUsername, InputMethodManager.RESULT_SHOWN);
        mImm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        cb_rember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppContext.getInstance().setProperty(AppConfig.USER_STATUS, "login");
                } else {
                    AppContext.getInstance().setProperty(AppConfig.USER_STATUS, "");
                }
            }
        });
        if (AppContext.getInstance().isLogin()) {
            mEtUsername.setText(AppContext.user.account);
            mEtPassword.setText(AppContext.user.pwd);
            cb_rember.setChecked(true);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
//            fragmentTransaction.setCustomAnimations(
//                    R.anim.slide_right_enter,  R.anim.slide_right_enter,
//                    R.anim.slide_right_enter, R.anim.slide_right_enter);
            fragmentTransaction.setCustomAnimations(R.anim.slide_right_enter, R.anim.slide_left_exit);
            switch (v.getId()) {
                case R.id.btn_login:
                    mUsername = mEtUsername.getText().toString().trim();
                    mPassword = mEtPassword.getText().toString().trim();
                    if (StringUtils.isEmpty(mUsername)) {
                        ((BaseActivity) getActivity()).errorPrompt(mEtUsername, R.string.login_account_prompt);
                        return;
                    }
                    if (StringUtils.isEmpty(mPassword)) {
                        ((BaseActivity) getActivity()).errorPrompt(mEtPassword, R.string.login_password_prompt);
                        return;
                    }
                    mImm.hideSoftInputFromWindow(mEtUsername.getWindowToken(), 0);
                    mAlertDlg.show();
                    UIHelper.showLoadingDialog(mAlertDlg, getActivity(), R.string.login_dialog_loading);
                    // mPassword = MD5.get32MD5(mPassword);
                    BmobQuery<Person> query = new BmobQuery<Person>();
                    query.addWhereEqualTo("account", mUsername);
                    query.addWhereEqualTo("pwd", mPassword);
                    query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
                    query.findObjects(getActivity(), new FindListener<Person>() {
                        @Override
                        public void onSuccess(List<Person> object) {
                            mAlertDlg.dismiss();
                            if (object.size() != 0) {
                                User user = new User();
                                user.uuid = object.get(0).getObjectId();
                                user.bmobUuid = object.get(0).getObjectId();
                                user.name = object.get(0).getName();
                                user.collegeId = object.get(0).getCollegeId();
                                user.role = object.get(0).getRole();
                                user.account = object.get(0).getAccount();
                                user.pwd = object.get(0).getPwd();
                                user.photo = object.get(0).getPhoto();
                                user.postition = object.get(0).getPosition();
                                user.phone = object.get(0).getPhone();

                                AppContext.getInstance().setUser(user);
                                UIHelper.ToastMessage(getActivity(), "登录成功");

                                Intent intent = new Intent(getActivity(), FragmentMainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                UIHelper.ToastMessage(getActivity(), "账号或密码错误");
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            mAlertDlg.dismiss();
                            if (i == 9009) {
//                                mEmptyLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
                            } else {
                                UIHelper.ToastMessage(getActivity(), s);
                            }
                        }
                    });

//                    new LoginAsyn().execute(mUsername, mPassword);
                    break;
                case R.id.btn_register://
//                    fragmentTransaction.hide(LoginFragment.this).add(R.id.login_frame, mForgetPwdFragment).addToBackStack(null).commit();
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.REG);
                    break;
            }
        }
    };
    /**
     * 显示隐藏密码
     */
    private CompoundButton.OnCheckedChangeListener changeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        mEtPassword.setTransformationMethod(HideReturnsTransformationMethod// 显示密码
                                .getInstance());
                    } else {
                        mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    // 切换后将EditText光标置于末尾
                    CharSequence charSequence = mEtPassword.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }
                }
            };

    private void executeOnLoadDataError() {
        AppException.getAppExceptionHandler().makeToast(getActivity());
    }
}
