package com.moudle.app.base;

import android.view.View;

import com.moudle.app.bean.Entity;

/**
 * @Description 需要加入header的BaseListFragment
 * @Author Li Chao
 * @Date 2016/01/15 16:01
 */
public abstract class BaseHaveHeaderListFragment<T extends Entity> extends BaseListFragment<T> {
    @Override
    public void initView(View v) {
        super.initView(v);
        mListView.addHeaderView(initHeaderView());
    }

    protected abstract View initHeaderView();
}
