package com.moudle.app.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.AppContext;
import com.moudle.app.AppManager;
import com.moudle.app.common.FileUtils;
import com.moudle.app.common.MethodsCompat;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.UpdateManager;
import com.moudle.app.ui.SimpleBackActivity;

import java.io.File;

/**
 * @Description
 * @Author Li Chao
 * @Date 2016/1/5 13:22
 */
public class SettingsFragment extends BaseFragMent {
    protected AlertDialog mAlertDlg = null;
    private LinearLayout mLayImageQuality;
    private LinearLayout mLayToastSet;
    private LinearLayout mLayCache;
    private LinearLayout mLayUpdate;
    private LinearLayout mLayAbout;
    private LinearLayout mLayExitApp;
    private TextView mTvImageQuality;
    private TextView mTvCacheSize;
    private TextView mTvVersionToast;
    private View mHeadView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mAlertDlg = builder.create();
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        //((TextView) mHeadView.findViewById(R.id.acitivity_name)).setText(R.string.setting_head_title);
        initView(v);
        initData();
        return v;
    }

    @Override
    public void initView(View v) {
        mLayImageQuality = (LinearLayout) v.findViewById(R.id.image_quality_lay);
        mLayToastSet = (LinearLayout) v.findViewById(R.id.toast_set_lay);
        mLayCache = (LinearLayout) v.findViewById(R.id.cache_lay);
        mLayUpdate = (LinearLayout) v.findViewById(R.id.update_lay);
        mLayAbout = (LinearLayout) v.findViewById(R.id.about_lay);
        mLayExitApp = (LinearLayout) v.findViewById(R.id.exit_app);
        mTvImageQuality = (TextView) v.findViewById(R.id.image_quality_text);
        mTvCacheSize = (TextView) v.findViewById(R.id.cache_size);
        mTvVersionToast = (TextView) v.findViewById(R.id.version_toast);

        mLayImageQuality.setOnClickListener(settingClick);
        mLayToastSet.setOnClickListener(settingClick);
        mLayCache.setOnClickListener(settingClick);
        mLayUpdate.setOnClickListener(settingClick);
        mLayAbout.setOnClickListener(settingClick);
        mLayExitApp.setOnClickListener(settingClick);
    }

    @Override
    public void initData() {
        // 清除缓存
        // 计算缓存大小
        long fileSize = 0;
        String cacheSize = "0KB";
        File basefile = getApplication().getFilesDir().getParentFile();
        fileSize += FileUtils.getDirSize(basefile);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat.getExternalCacheDir(getActivity());
            fileSize += FileUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        mTvCacheSize.setText(cacheSize);
    }

    private View.OnClickListener settingClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_quality_lay:
                    mAlertDlg.show();
                    mAlertDlg.setContentView(R.layout.imgload_select_dialog);
                    RadioButton mRbAuto = (RadioButton) mAlertDlg.findViewById(R.id.rb_auto);
                    RadioButton mRbHigh = (RadioButton) mAlertDlg.findViewById(R.id.rb_high);
                    RadioButton mRbNormal = (RadioButton) mAlertDlg.findViewById(R.id.rb_normal);
                    mRbAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            mTvImageQuality.setText(R.string.auto_img_load);
                            mAlertDlg.dismiss();
                        }
                    });
                    mRbHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            mTvImageQuality.setText(R.string.high_img_load);
                            mAlertDlg.dismiss();
                        }
                    });
                    mRbNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            mTvImageQuality.setText(R.string.normal_img_load);
                            mAlertDlg.dismiss();
                        }
                    });
                    break;
                case R.id.toast_set_lay:
                    UIHelper.showSettingNotice(getActivity());
                    break;
                case R.id.cache_lay:
                    mAlertDlg.show();
                    mAlertDlg.setContentView(R.layout.prompt_dialog);
                    ((TextView) mAlertDlg.findViewById(R.id.prompt_content)).setText(R.string.main_menu_cache_clear_confirm);
                    Button btnCancel = (Button) mAlertDlg.findViewById(R.id.btn_cancel);
                    btnCancel.setText(R.string.app_cancel);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDlg.dismiss();
                        }
                    });
                    Button btnOK = (Button) mAlertDlg.findViewById(R.id.btn_ok);
                    btnOK.setText(R.string.app_ok);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIHelper.clearAppCache(getActivity());
                            mTvCacheSize.setText("0KB");
                            mAlertDlg.dismiss();
                        }
                    });
                    break;
                case R.id.update_lay:
                    UpdateManager.getUpdateManager().checkAppUpdate(getActivity(),
                            true);
                    break;
                case R.id.about_lay:

                    break;
                case R.id.exit_app:
                    AppManager.getAppManager().AppExit(getActivity());
                    break;

            }
        }
    };
}
