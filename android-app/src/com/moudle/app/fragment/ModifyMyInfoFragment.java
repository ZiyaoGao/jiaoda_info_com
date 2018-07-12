package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.InfoType;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.bean.User;
import com.moudle.app.common.Compress;
import com.moudle.app.common.DialogHelp;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.UpImageHelp;
import com.moudle.app.ui.FragmentMainActivity;
import com.moudle.app.ui.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 修改我的信息
 * Created by Administrator on 2016/5/7.
 */
public class ModifyMyInfoFragment extends BaseFragMent {
    private EditText et_login_username, login_phone, login_name;
    private Button btn_modify, btn_modify_pwd;
    private Spinner college_spinner;
    private Spinner position_spinner;
    private String[] collectID = new String[]{"IO9ZKKKS", "SoWA777E", "1Eb5333Z",
            "R25GAAAO", "ykYvUUUp", "XYSJ777O", "23OF1113", "M6xFDDDP"};
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private AlertDialog mAlertDlg = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_modify, null);
        initView(v);
        initData();
        mAlertDlg = new AlertDialog.Builder(getActivity()).create();
        return v;
    }

    public void initView(View v) {
        et_login_username = (EditText) v.findViewById(R.id.et_login_username);
        login_phone = (EditText) v.findViewById(R.id.login_phone);
        login_name = (EditText) v.findViewById(R.id.login_name);
        btn_modify = (Button) v.findViewById(R.id.btn_modify);
        btn_modify_pwd = (Button) v.findViewById(R.id.btn_modify_pwd);
        college_spinner = (Spinner) v.findViewById(R.id.college_spinner);
        position_spinner = (Spinner) v.findViewById(R.id.position_spinner);
        btn_modify.setOnClickListener(this);
        btn_modify_pwd.setOnClickListener(this);

        et_login_username.setText(AppContext.user.account);
        login_phone.setText(AppContext.user.phone);
        login_name.setText(AppContext.user.name);
        login_name.setText(AppContext.user.name);
        int i = 0;
        for (String a : collectID) {
            if (AppContext.user.collegeId.equals(a)) {
                college_spinner.setSelection(i);
                break;
            }
            i++;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify:
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_pwd);
                Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
                EditText login_pwd = (EditText) dialog.findViewById(R.id.login_pwd);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (login_pwd.getText().toString().equals(AppContext.user.pwd)) {
                            UIHelper.showLoadingDialog(mAlertDlg, getActivity(), R.string.please_wait);
//                    new RegNetworkAsyn(LoginActivity.REQUEST_REG).execute(mUserName, mPassWord, mCode);
                            Person person = new Person();
                            person.setObjectId(AppContext.user.bmobUuid);
                            person.setPhone(login_phone.getText().toString().trim());
                            person.setName(login_name.getText().toString().trim());
                            person.setPosition((String) position_spinner.getSelectedItem());
                            person.setDataStatus(0);
                            person.setCollegeId(collectID[college_spinner.getSelectedItemPosition()]);
                            person.setLevel(position_spinner.getSelectedItemPosition() + 1);

                            person.update(getActivity(), new UpdateListener() {
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
                                    UIHelper.ToastMessage(getActivity(), "修改成功");
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    mAlertDlg.dismiss();
                                }
                            });
                        } else {
                            UIHelper.ToastMessage(getActivity(), "密码错误");
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.btn_modify_pwd:
                Dialog pwdDialog = new Dialog(getActivity());
                pwdDialog.setContentView(R.layout.dialog_modify_pwd);
                Button button = (Button) pwdDialog.findViewById(R.id.btn_confirm);
                EditText editText = (EditText) pwdDialog.findViewById(R.id.et_pwd);
                EditText et_new_pwd = (EditText) pwdDialog.findViewById(R.id.et_new_pwd);
                EditText et_new_pwd_again = (EditText) pwdDialog.findViewById(R.id.et_new_pwd_again);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().equals(AppContext.user.pwd)) {
                            if (et_new_pwd.getText().toString().equals(et_new_pwd.getText().toString())) {
                                Person person = new Person();
                                person.setObjectId(AppContext.user.bmobUuid);
                                person.setPwd(et_new_pwd_again.getText().toString());

                                person.update(getActivity(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        mAlertDlg.dismiss();
                                        AppContext.user.pwd = person.getPwd();
                                        AppContext.getInstance().setUser(AppContext.user);
                                        UIHelper.ToastMessage(getActivity(), "密码修改成功");
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        mAlertDlg.dismiss();
                                    }
                                });
                            } else {
                                UIHelper.ToastMessage(getActivity(), "两次密码不一致");
                            }
                        } else {
                            UIHelper.ToastMessage(getActivity(), "密码错误");
                        }
                    }
                });
                pwdDialog.show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
