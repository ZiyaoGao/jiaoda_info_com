package com.moudle.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.common.PollingUtils;
import com.moudle.app.ui.SimpleBackActivity;

/**
 * @Description 系统设置
 * @Author Li Chao
 * @Date 2016/1/5 14:14
 */
public class SettingsNotificationFragment extends BaseFragMent {
    private LinearLayout mLayPushSet;
    private LinearLayout mLayVoiceToast;
    private LinearLayout mLayVibrationToast;
    private LinearLayout mLayCheckup;
    private CheckBox mCbPush;
    private CheckBox mCbVoice;
    private CheckBox mCbVibration;
    private CheckBox mCbCheckup;
    private View mHeadView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_settings_notifcation, container, false);
        //((TextView) mHeadView.findViewById(R.id.acitivity_name)).setText(R.string.toast_setting);
        initView(v);
        return v;
    }

    @Override
    public void initView(View v) {
        mLayPushSet = (LinearLayout) v.findViewById(R.id.push_set_lay);
        mLayVoiceToast = (LinearLayout) v.findViewById(R.id.voice_toast_lay);
        mLayVibrationToast = (LinearLayout) v.findViewById(R.id.vibration_toast_lay);
        mLayCheckup = (LinearLayout) v.findViewById(R.id.checkup_lay);
        mCbPush = (CheckBox) v.findViewById(R.id.push_check);
        mCbVoice = (CheckBox) v.findViewById(R.id.voice_check);
        mCbVibration = (CheckBox) v.findViewById(R.id.vibration_check);
        mCbCheckup = (CheckBox) v.findViewById(R.id.checkup_check);

        mCbPush.setChecked(getApplication().isRecommends());
        mCbVoice.setChecked(getApplication().isVoice());
        mCbVibration.setChecked(getApplication().isVibration());
        mCbCheckup.setChecked(getApplication().isCheckUp());

        mLayPushSet.setOnClickListener(settingNoticeClick);
        mLayVoiceToast.setOnClickListener(settingNoticeClick);
        mLayVibrationToast.setOnClickListener(settingNoticeClick);
        mLayCheckup.setOnClickListener(settingNoticeClick);
    }

    private View.OnClickListener settingNoticeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.push_set_lay:
                    mCbPush.toggle();

                    break;
                case R.id.voice_toast_lay:
                    mCbVoice.toggle();
                    getApplication().setConfigVoice(mCbVoice.isChecked());
                    PollingUtils.setRecommendSound(getActivity(), mCbVoice.isChecked());
                    break;
                case R.id.vibration_toast_lay:
                    mCbVibration.toggle();
                    getApplication().setConfigVibration(mCbVibration.isChecked());
                    PollingUtils.setRecommendVibration(getActivity(), mCbVibration.isChecked());
                    break;
                case R.id.checkup_lay:
                    mCbCheckup.toggle();
                    getApplication().setConfigCheckUp(mCbCheckup.isChecked());
                    break;
            }
        }
    };
}
