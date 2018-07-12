package com.moudle.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

import com.moudle.app.R;
import com.moudle.app.base.BaseActivity;
import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.bean.Entity;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;


/**
 * @Description 修改密码Activity
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class PwdModifyActivity extends BaseActivity {

    private Button mBtnModify;
    private EditText mEtNewPassword;
    private EditText mEtOldPassword;
    private CheckBox mTogglePassword;
    private AlertDialog mAlertDlg = null;
    private TextView mActivityName;
    private AppContext appContext;
    private InputMethodManager imm;
    String newPassword;
    String oldPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_modify);
        appContext = (AppContext) getApplicationContext();
        mAlertDlg = new AlertDialog.Builder(this).create();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
    }


    // 初始化视图控件
    @Override
    public void initView() {
        mBtnModify = (Button) findViewById(R.id.btn_modify_pwd);
        mEtOldPassword = (EditText) findViewById(R.id.login_old_pwd);
        mEtNewPassword = (EditText) findViewById(R.id.login_new_pwd);
     //   mActivityName = (TextView) findViewById(R.id.acitivity_name);
        mActivityName.setText(R.string.mine_password_modify);
        mTogglePassword = (CheckBox) findViewById(R.id.password_checkbox);
        mBtnModify.setOnClickListener(listener);
        mTogglePassword.setOnCheckedChangeListener(changeListener);
        // 打开软键盘
        imm.showSoftInput(mEtOldPassword, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 显示隐藏密码
     */
    private CompoundButton.OnCheckedChangeListener changeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        mEtNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        mEtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    // 切换后将EditText光标置于末尾
                    CharSequence charSequence = mEtNewPassword.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }

                }
            };

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_modify_pwd:
                    newPassword = mEtNewPassword.getText().toString().trim();
                    oldPassword = mEtOldPassword.getText().toString().trim();
                    if (StringUtils.isEmpty(oldPassword)) {
                        errorPrompt(mEtOldPassword, R.string.modify_oldpwd_prompt);
                        return;
                    }
                    if (StringUtils.isEmpty(newPassword)) {
                        errorPrompt(mEtNewPassword, R.string.modify_newpwd_prompt);
                        return;
                    }
                    mAlertDlg.show();
                    UIHelper.showLoadingDialog(mAlertDlg, PwdModifyActivity.this, R.string.login_dialog_loading);
                /*
                 * User user = appContext.user; params.put("oldpwd",
                 * oldPassword); params.put("newpwd", newPassword);
                 * params.put("uuid", user.uuid);
                 */
                    new PwdModifyNetworkAsyn().execute(oldPassword, newPassword);
                    break;
            }
        }
    };

    private class PwdModifyNetworkAsyn extends AsyncTask<String, Void, Serializable> {

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable serializable = null;
            try {
                serializable = appContext.changePassword(params[0], params[1]);
            } catch (AppException e) {
                AppException.run(e);
            }

            return serializable;
        }

        @Override
        protected void onPostExecute(Serializable result) {
            mAlertDlg.dismiss();
            if (result != null) {
                if (((Entity) result).isStatus()) {
                    UIHelper.ToastMessage(PwdModifyActivity.this,
                            getResources().getText(R.string.modify_pwd_success).toString());
                    finish();
                }
                UIHelper.ToastMessage(PwdModifyActivity.this, ((Entity) result).message);

            } else {
                executeOnLoadDataError();
            }
        }

    }

    private void executeOnLoadDataError() {
        AppException.getAppExceptionHandler().makeToast(this);
    }

}
