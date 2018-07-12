package com.moudle.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.Entity;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.User;
import com.moudle.app.common.UIHelper;
import com.moudle.app.ui.FragmentMainActivity;
import com.moudle.app.ui.LoginActivity;
import com.moudle.app.AppException;
import com.moudle.app.common.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;


/**
 * @Description 注册模块
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class RegFragment extends BaseFragMent {
    private TextView mTvTop;// 标题字
    private EditText mEtUsername, login_name, login_phone;
    private EditText mlEtLoginCode;// 密码
    private Button mBtnReg;// 注册按钮
    private String mUserName;
    private String mPassWord;
    private String mCode;
    private ImageButton mIbBack;
    private InputMethodManager mImm;
    private AlertDialog mAlertDlg = null;
    private FragmentManager fm;
    private Spinner college_spinner;
    private Spinner position_spinner;
    private String[] collectID = new String[]{"IO9ZKKKS", "SoWA777E", "1Eb5333Z",
            "R25GAAAO", "ykYvUUUp", "XYSJ777O", "23OF1113", "M6xFDDDP"};

    private List<String> list = new ArrayList<String>();

    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_reg, null);
        mAlertDlg = new AlertDialog.Builder(getActivity()).create();
        fm = getActivity().getSupportFragmentManager();
        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(v);
        initData();
        return v;
    }


    public void initView(View v) {
        // mTvTop = (TextView) v.findViewById(R.id.acitivity_name);
//        mTvTop.setText(R.string.app_reg);// 设置字体显示
        mEtUsername = (EditText) v.findViewById(R.id.login_username);
        mlEtLoginCode = (EditText) v.findViewById(R.id.login_password);
        mBtnReg = (Button) v.findViewById(R.id.btn_reg);
        login_name = (EditText) v.findViewById(R.id.login_name);
        login_phone = (EditText) v.findViewById(R.id.login_phone);
        college_spinner = (Spinner) v.findViewById(R.id.college_spinner);
        position_spinner = (Spinner) v.findViewById(R.id.position_spinner);

        // mIbBack = (ImageButton) v.findViewById(R.id.btn_back);
        mBtnReg.setOnClickListener(regListen);
//        mIbBack.setOnClickListener(regListen);
        // 打开软键盘
        mImm.showSoftInput(mEtUsername, InputMethodManager.RESULT_SHOWN);
        mImm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private View.OnClickListener regListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUserName = mEtUsername.getText().toString();
            mPassWord = mlEtLoginCode.getText().toString();
            switch (v.getId()) {
                case R.id.btn_reg:
                    if (mPassWord.trim().length() != mPassWord.length()) {
                        UIHelper.ToastMessage(getActivity(), "密码中不能含有空格");
                        return;
                    }
                    if (!StringUtils.checkPassword(mPassWord)) {
                        UIHelper.ToastMessage(getActivity(), " 密码必须包含大小写字母、数字、字符中");
                        return;
                    }
                    if (!StringUtils.checkNameChese(login_name.getText().toString().trim())) {
                        UIHelper.ToastMessage(getActivity(), "姓名只能输入汉字");
                        return;
                    }
                    mAlertDlg.show();
                    UIHelper.showLoadingDialog(mAlertDlg, getActivity(), R.string.activity_registing);
//                    new RegNetworkAsyn(LoginActivity.REQUEST_REG).execute(mUserName, mPassWord, mCode);
                    Person person = new Person();
                    person.setAccount(mUserName);
                    person.setPwd(mPassWord);
                    person.setPhone(login_phone.getText().toString().trim());
                    person.setName(login_name.getText().toString().trim());
                    person.setPosition((String) position_spinner.getSelectedItem());
                    person.setDataStatus(0);
                    person.setCollegeId(collectID[college_spinner.getSelectedItemPosition()]);
                    person.setPhoto("http://p.3761.com/pic/43701399945993.png");
                    person.setLevel(position_spinner.getSelectedItemPosition() + 1);

                    person.save(getActivity(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            mAlertDlg.dismiss();
                            User user = new User();
                            user.uuid = person.getObjectId();
                            user.bmobUuid = person.getObjectId();
                            user.name = person.getName();
                            user.collegeId = person.getCollegeId();
                            user.role = person.getRole();
                            user.account = person.getAccount();
                            user.pwd = person.getPwd();
                            user.photo = person.getPhoto();
                            user.postition = person.getPosition();
                            user.phone = person.getPhone();
                            user.level = person.getLevel();

                            AppContext.getInstance().setUser(user);
                            UIHelper.ToastMessage(getActivity(), "注册成功");

                            Intent intent = new Intent(getActivity(), FragmentMainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            mAlertDlg.dismiss();
                        }
                    });
                    break;
            }
        }
    };

    private class RegNetworkAsyn extends AsyncTask<String, Void, Serializable> {
        private int mDataFlag;

        public RegNetworkAsyn(int dataFlag) {
            this.mDataFlag = dataFlag;
        }

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable serializable = null;
            try {
                switch (mDataFlag) {
                    case LoginActivity.REQUEST_CODE:
                        serializable = getApplication().getCode(params[0]);
                        break;
                    case LoginActivity.REQUEST_REG:
                        serializable = getApplication().regVerify(params[0], params[1], params[2]);
                }
            } catch (AppException e) {
                AppException.run(e);
            }
            return serializable;
        }

        @Override
        protected void onPostExecute(Serializable result) {
            mAlertDlg.dismiss();
            if (result != null) {
                switch (mDataFlag) {
                    case LoginActivity.REQUEST_CODE:
                        UIHelper.ToastMessage(getActivity(), ((Entity) result).message);
                        break;
                    case LoginActivity.REQUEST_REG:
                        if (((Entity) result).isStatus()) {
                            fm.beginTransaction().setCustomAnimations(R.anim.slide_left_enter, R.anim.slide_right_exit).remove(RegFragment.this).commit();
                            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        UIHelper.ToastMessage(getActivity(), ((Entity) result).message);
                        break;
                }
            } else {
                executeOnLoadDataError();
            }
        }
    }

    private void executeOnLoadDataError() {
        AppException.getAppExceptionHandler().makeToast(getActivity());
    }
}
