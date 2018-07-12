package com.moudle.app.fragment;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.Entity;
import com.moudle.app.common.UIHelper;
import com.moudle.app.common.MiddleClient;
import com.moudle.app.ui.SimpleBackActivity;

import java.io.Serializable;

/**
 * @Description 意见反馈
 * @Author Li Chao
 * @Date 2016/1/4 16:03
 */
public class FeedBackFragment extends BaseFragMent {
    private Button mBtnFeedBack;
    private EditText mEditMessage;// 反馈内容
    private Spinner mSpinnerFeedBack;
    protected AlertDialog mAlertDlg = null;
    private View mHeadView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mAlertDlg = builder.create();
        View v = inflater.inflate(R.layout.activity_feedback, container, false);
        //((TextView) mHeadView.findViewById(R.id.acitivity_name)).setText(R.string.feed_back);
        initView(v);
        return v;
    }

    @Override
    public void initView(View v) {
        mBtnFeedBack = (Button) v.findViewById(R.id.btn_feed_back);
        mEditMessage = (EditText) v.findViewById(R.id.message_edit);
        mSpinnerFeedBack = (Spinner) v.findViewById(R.id.spinner_feed_back);
        //声明一个SimpleAdapter独享，设置数据与对应关系
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), R.layout.custom_spinner);
        String level[] = getResources().getStringArray(R.array.feed_back);//资源文件
        for (int i = 0; i < level.length; i++) {
            adapter.add(level[i]);
        }
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        mSpinnerFeedBack.setAdapter(adapter);

        mBtnFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditMessage.length() == 0) {
                    errorPrompt(mEditMessage, R.string.app_feedback_prompt);
                    return;
                } else {
                    mAlertDlg.show();
                    UIHelper.showLoadingDialog(mAlertDlg, getActivity(), R.string.data_loading);
                    new FeedBackNetworkAsyn().execute(getApplication().user.uuid, mEditMessage.getText().toString().trim());
                }
            }
        });
    }


    private class FeedBackNetworkAsyn extends AsyncTask<String, Void, Serializable> {
        @Override
        protected Serializable doInBackground(String... params) {
            Serializable serializable = null;
            try {
                serializable = MiddleClient.sendFeedBack(params[0], params[1]);
            } catch (AppException e) {
                AppException.run(e);
            }
            return serializable;
        }

        @Override
        protected void onPostExecute(Serializable result) {
            mAlertDlg.dismiss();
            if (result != null) {
                UIHelper.ToastMessage(getActivity(), ((Entity) result).getMessage());
            } else {
                executeOnLoadDataError();
            }
        }
    }

    private void executeOnLoadDataError() {

    }
}
