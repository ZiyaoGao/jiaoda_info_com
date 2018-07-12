package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.AlarmClock;
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
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.IdCard;
import com.moudle.app.bean.Info;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.Compress;
import com.moudle.app.common.DialogHelp;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.UpImageHelp;
import com.moudle.app.widget.EmptyLayout;
import com.moudle.app.widget.TitleModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 消息详情
 * Created by Administrator on 2016/5/7.
 */
public class InfoDetailFragment extends BaseFragMent {
    private Info mInfo;
    private Dialog mAlertDlg;
    private AlertDialog singleAlerDialog;
    private AlertDialog singleSignIn;
    private TextView et_name, et_info_type, tv_content;
    private Button btn_delete, btn_send, btn_calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mInfo = (Info) getArguments().getSerializable("Info");
        View v = inflater.inflate(R.layout.fragment_info_detail, null);
        initView(v);
        initData();
        return v;
    }

    public void initView(View v) {
        mAlertDlg = new AlertDialog.Builder(getActivity()).create();
        et_name = (TextView) v.findViewById(R.id.et_name);
        et_info_type = (TextView) v.findViewById(R.id.et_info_type);
        tv_content = (TextView) v.findViewById(R.id.tv_content);
        btn_send = (Button) v.findViewById(R.id.btn_send);
        btn_delete = (Button) v.findViewById(R.id.btn_delete);
        btn_calendar = (Button) v.findViewById(R.id.btn_calendar);
        btn_delete.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        et_name.setText(mInfo.getAuthor());
        et_info_type.setText(mInfo.getInfoType());
        tv_content.setText(mInfo.getContent());
        if (AppContext.user.postition.equals("普通教师")) {
            btn_send.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                Person person = new Person();
//                person.setObjectId(mInfo.getUserID());
                person.setName(mInfo.getAuthor());
                person.setObjectId(mInfo.getSendUserID());
                Bundle args = new Bundle();
                args.putSerializable("Person", person);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ADD_INFO, args);
                getActivity().finish();
                break;
            case R.id.btn_delete:
                AlertDialog dialog = DialogHelp.getConfirmDialog(getActivity(), "确认删除吗？",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mInfo.getType() == 2) {
                                    mInfo.setType(3);
                                } else {
                                    mInfo.setType(2);
                                    mInfo.setIsSignType(0);
                                    mInfo.setCollection(false);
                                }
                                mInfo.update(getActivity(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        UIHelper.ToastMessage(getActivity(), "删除成功");
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        if (i == 9009) {

                                        } else {
                                            UIHelper.ToastMessage(getActivity(), s);
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.btn_calendar:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity"));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 初始化头部
     *
     * @param titleModel
     */
    protected void initTitle(TitleModel titleModel) {
        ImageView rightIv = titleModel.creatRightView(ImageView.class);
        rightIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.demo_tribe_red_icon));
        rightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleAlerDialog = DialogHelp.getSingleChoiceDialog(getActivity(), "请选择",
                        new String[]{"收藏", "标记", "日历"}, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mInfo.setCollection(true);
                                        mInfo.update(getActivity(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                UIHelper.ToastMessage(getActivity(), "收藏成功");
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                if (i == 9009) {

                                                } else {
                                                    UIHelper.ToastMessage(getActivity(), s);
                                                }
                                            }
                                        });
                                        break;
                                    case 1:
                                        signIn();
                                        break;
                                    case 2:
                                        Intent intent = new Intent();
                                        intent.setComponent(new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity"));
                                        startActivity(intent);
                                        break;
                                }
                                singleAlerDialog.dismiss();
                            }
                        }).create();
                singleAlerDialog.show();
            }
        });
        titleModel.submit();
    }

    private void signIn() {
        singleSignIn = DialogHelp.getSingleChoiceDialog(getActivity(), "请选择",
                new String[]{"不重要", "一般", "重要", "非常重要"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInfo.setIsSignType(which + 1);
                        mInfo.update(getActivity(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                UIHelper.ToastMessage(getActivity(), "标记成功");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                if (i == 9009) {

                                } else {
                                    UIHelper.ToastMessage(getActivity(), s);
                                }
                            }
                        });
                        singleSignIn.dismiss();
                    }
                }).create();
        singleSignIn.show();
    }
}
