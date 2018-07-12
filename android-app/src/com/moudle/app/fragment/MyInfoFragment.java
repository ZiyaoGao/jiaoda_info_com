package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.IdCard;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.Compress;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.UpImageHelp;
import com.moudle.app.ui.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 我的信息详情
 * Created by Administrator on 2016/5/7.
 */
public class MyInfoFragment extends BaseFragMent {
    private String photoPath;
    private ImageView iv_photo;
    private TextView tv_name, tv_position;
    private AlertDialog mAlertDlg;
    private String mPhotoUrl;
    private Button btn_my_collection;
    private Button btn_sign_in;
    private Button btn_out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_my_info, null);
        initView(v);
        initData();
        return v;
    }

    public void initView(View v) {
        mAlertDlg = new AlertDialog.Builder(getActivity()).create();
        iv_photo = (ImageView) v.findViewById(R.id.iv_photo);
        tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_position = (TextView) v.findViewById(R.id.tv_position);
        tv_name.setText(AppContext.user.getName());
        ImageLoader.getInstance().displayImage(AppContext.user.photo, iv_photo);
        btn_my_collection = (Button) v.findViewById(R.id.btn_my_collection);
        btn_sign_in = (Button) v.findViewById(R.id.btn_sign_in);
        btn_out = (Button) v.findViewById(R.id.btn_out);
        tv_position.setText(AppContext.user.postition);
        btn_my_collection.setOnClickListener(this);
        btn_out.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_out:
                AppContext.getInstance().Logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.btn_my_collection:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_COLLECTION_INFO);
                break;
            case R.id.btn_sign_in:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.QUERY_SIGN_IN_INFO_MENU);
                break;
            case R.id.iv_photo:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.MODIFY_MY_INFO);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UpImageHelp.REQUEST_CODE_CAMERA) {
            if (new File(photoPath).length() > 300 * 1024) {
                try {
                    photoPath = Compress.CompressImage(photoPath, 600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mAlertDlg.show();
            UIHelper.showLoadingDialog(mAlertDlg, getActivity(), R.string.app_img_updating);
            BmobFile bmobFile = new BmobFile(new File(photoPath));
            bmobFile.uploadblock(getActivity(), new UploadFileListener() {
                @Override
                public void onSuccess() {
                    mAlertDlg.dismiss();
                    mPhotoUrl = bmobFile.getFileUrl(getActivity());
                    ImageLoader.getInstance().displayImage(mPhotoUrl, iv_photo);
                    UIHelper.ToastMessage(getActivity(), "上传成功");
                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }

                @Override
                public void onFailure(int code, String msg) {
                    mAlertDlg.dismiss();
                    UIHelper.ToastMessage(getActivity(), msg);
                    //toast("上传文件失败：" + msg);
                }
            });
        }
    }
}
