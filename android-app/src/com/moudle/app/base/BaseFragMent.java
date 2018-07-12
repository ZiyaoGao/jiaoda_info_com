package com.moudle.app.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.interf.BaseFragmentInterface;
import com.moudle.app.widget.TitleModel;
import com.moudle.app.widget.pulltorefresh.PullToRefreshBase;


/**
 * @Description FragMent基类
 * @Author Li Chao
 * @Date 2015/12/10 11:23
 */
public class BaseFragMent extends Fragment implements BaseFragmentInterface, View.OnClickListener {
    protected LayoutInflater mInflater;
    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1; //刷新
    public static final int STATE_LOADMORE = 2;//加载更多
    public static final int STATE_NOMORE = 3;//没有更多数据
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;
    protected View.OnClickListener loginButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UIHelper.showLoginDialog(getActivity());
        }
    };

    public AppContext getApplication() {
        return (AppContext) getActivity().getApplication();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mInflater = inflater;
        onInitTitle();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {

    }

    /**
     * 错误消息提醒
     *
     * @param editText
     * @param id
     */
    public void errorPrompt(EditText editText, int id) {
        String prompt = getResources().getText(id).toString();
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(prompt);
        ssbuilder.setSpan(0, 0, prompt.length(), 0);
        editText.requestFocus();
        editText.setError(ssbuilder);
    }

    /**
     * 获取更新时间
     *
     * @param pullToRefreshBase
     */
    public void setLastUpdateTime(PullToRefreshBase pullToRefreshBase) {
        String text = StringUtils.getCurrentDateTime(StringUtils.dateFormater);
        pullToRefreshBase.setLastUpdatedLabel(text);
    }

    /**
     * 操作头部
     */
    protected void onInitTitle() {
        if (getActivity() instanceof RootActivity) {
            TitleModel manager = ((RootActivity) getActivity()).getTitleModel();
            initTitle(manager);
        }
    }

    /**
     * 初始化头部
     *
     * @param titleModel
     */
    protected void initTitle(TitleModel titleModel) {

    }

    public void onBackPressed() {
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {

    }

    public boolean isTvEmpty(TextView... textViews) {
        for (int i = 0; i < textViews.length; i++) {
            if (StringUtils.isEmpty(textViews[i].getText().toString())) {
                UIHelper.ToastMessage(getActivity(), "请全部填写完整");
                return true;
            }
        }
        return false;
    }
}