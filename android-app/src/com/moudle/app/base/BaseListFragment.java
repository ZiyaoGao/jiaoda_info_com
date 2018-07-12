package com.moudle.app.base;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.bean.Entity;
import com.moudle.app.bean.Operation;
import com.moudle.app.cache.CacheManager;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.EmptyLayout;
import com.moudle.app.widget.pulltorefresh.PullToRefreshBase;
import com.moudle.app.widget.pulltorefresh.PullToRefreshListView;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description pull_list基类
 * @Author Li Chao
 * @Date 2016/01/12 15:39
 */
public abstract class BaseListFragment<T extends Entity> extends BaseFragMent implements AdapterView.OnItemClickListener {
    protected ListView mListView;
    protected PullToRefreshListView mPullListView;
    public final static String BUNDLE_NEWS_TYPE = "BUNDLE_NEWS_TYPE";
    protected int mCatalog;
    protected boolean mOperation = true;//操作类型 true是上下拉 false 为其它动作
    protected AsyncTask<String, Void, List<T>> mCacheTask;
    // 操作结果
    protected Operation mResult;
    protected EmptyLayout mEmptyLayout;
    protected int mCurrentPage = 1;
    protected BaseListAdapter<T> mAdapter;
    protected int mStoreEmptyState = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(BUNDLE_NEWS_TYPE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_pull_refresh_listview, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    @Override
    public void initView(View v) {
        mPullListView = new PullToRefreshListView(getActivity());
        mPullListView = (PullToRefreshListView) v.findViewById(R.id.pull_to_list);
        mEmptyLayout = (EmptyLayout) v.findViewById(R.id.error_layout);
        // 上拉加载可用
        mPullListView.setScrollLoadEnabled(true);
        // 得到实际的ListView
        mListView = mPullListView.getRefreshableView();
        mListView.setDivider(null);
        mListView.setDividerHeight(20);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setAlwaysDrawnWithCacheEnabled(true);
        mListView.setOnItemClickListener(this);
        // 设置下拉刷新的listener
        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mOperation = true;
                mState = STATE_REFRESH;
                mCurrentPage = 1;
                new NetWorkTask().execute(String.valueOf(mCurrentPage), String.valueOf(AppContext.PAGE_SIZE));
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mOperation = true;
                mState = STATE_LOADMORE;
                mCurrentPage++;
                requestData(false);
            }
        });
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOperation = true;
                mCurrentPage = 1;
                mState = STATE_REFRESH;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);
            if (requestDataIfViewCreated()) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                mState = STATE_NONE;
                requestData(false);//进行数据加载
            } else {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }
    }


    @Override
    public void onDestroyView() {
        mStoreEmptyState = mEmptyLayout.getErrorState();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        cancelReadCacheTask();
        super.onDestroy();
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    protected List<T> readList(Serializable seri) {
        return (List<T>) seri;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    protected String getCacheKey() {
        return new StringBuilder(getCacheKeyPrefix()).append("_")
                .append(mCurrentPage).toString();
    }

    /**
     * @return 返回缓存Key组成字段部分
     */
    protected String getCacheKeyPrefix() {
        return this.getClass().getSimpleName();
    }


    /**
     * 保存缓存
     */
    protected class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        public SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    /**
     * 网络请求异步处理
     */
    public class NetWorkTask extends AsyncTask<String, Void, Serializable> {

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable serializable = null;
            try {
                serializable = getSerializable(params);
            } catch (AppException e) {
                AppException.run(e);
            }
            return serializable;
        }

        @Override
        protected void onPostExecute(Serializable result) {
            executeOnLoadFinish();
            if (result != null) {
                netWorkPostExecute(result);
                if (mOperation) {
                    new SaveCacheTask(getActivity(), result, getCacheKey()).execute();
                }
            } else {
                executeOnLoadDataError("");
            }
        }
    }

    protected abstract Serializable getSerializable(String... params) throws AppException;

    /**
     * 获取的网络数据进行处理
     *
     * @param result
     */
    protected void netWorkPostExecute(Serializable result) {
//        if (isAdded()) {//该Fragment是否在栈顶
//            if (mState == STATE_REFRESH) {
//                onRefreshNetworkSuccess();
//            }
//        }
        executeOnLoadDataSuccess((List<T>) result);
    }

    /**
     * 读取缓存
     */
    private class CacheTask extends AsyncTask<String, Void, List<T>> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected List<T> doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return readList(seri);
            }
        }

        @Override
        protected void onPostExecute(List<T> list) {
            executeOnLoadFinish();
            if (list != null) {
                executeOnLoadDataSuccess(list);
            } else {
                executeOnLoadDataError("");
            }
        }
    }

    /**
     * 所有数据加载后的统一处理处
     * 设置了缓存失效时间,第一次数据请求是缓存,再下拉刷新的时候,增加几条新数据,上拉的时候又是有缓存,所以导致丢失了某些数据
     *
     * @param data
     */
    protected void executeOnLoadDataSuccess(List<T> data) {
        if (data == null) {
            data = new ArrayList<T>();
        }
        if (mResult != null && !mResult.isStatus()) {
            UIHelper.ToastMessage(getActivity(), mResult.getMessage());
            AppContext.getInstance().Logout();
        }
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (mCurrentPage == 1) {
            mAdapter.clear();
        }
        for (int i = 0; i < data.size(); i++) {//移除同个数据
            if (compareTo(mAdapter.getData(), data.get(i))) {
                data.remove(i);
                i--;
            }
        }
        if ((mAdapter.getCount() + data.size()) == 0) {
            if (needShowEmptyNoData()) {
                mEmptyLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            }
        } else if (data.size() == 0
                || (data.size() < getPageSize())) {
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.addData(data);
    }

    /**
     * 是否需要隐藏listview，显示无数据状态
     *
     * @author 火蚁 2015-1-27 下午6:18:59
     */
    protected boolean needShowEmptyNoData() {
        return true;
    }

    protected int getPageSize() {
        return AppContext.PAGE_SIZE;
    }

    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (enity.getObjectId().equals(data.get(i).getObjectId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param error
     */
    protected void executeOnLoadDataError(String error) {
        //mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        mEmptyLayout.setErrorMessage(error);
    }


    /**
     * 获取列表数据
     *
     * @param refresh
     * @return void
     * @author 火蚁 2015-2-9 下午3:16:12
     */
    protected void requestData(boolean refresh) {
        String key = getCacheKey();
        if (isReadCacheData(refresh)) {
            readCacheData(key);
        } else {
            // 取新的数据
            sendRequestData();
        }
    }

    /**
     * 取最新数据
     */
    protected void sendRequestData() {
        new NetWorkTask().execute(String.valueOf(mCurrentPage), String.valueOf(AppContext.PAGE_SIZE));
    }


    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    /**
     * 判断是否需要读取缓存的数据
     *
     * @param refresh
     * @return
     * @author 火蚁 2015-2-10 下午2:41:02
     */
    protected boolean isReadCacheData(boolean refresh) {
        String key = getCacheKey();
        if (!getApplication().isNetworkConnected()) {
            return true;
        }
        // 第一页若不是主动刷新，缓存存在，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key) && !refresh
                && mCurrentPage == 1) {
            return true;
        }
        // 其他页数的，缓存存在以及还没有失效，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key)
                && !CacheManager.isCacheDataFailure(getActivity(), key)
                && mCurrentPage != 1) {
            return true;
        }
        return false;
    }

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    /**
     * 完成刷新
     */
    protected void executeOnLoadFinish() {
        setLastUpdateTime(mPullListView);
        switch (mState) {
            case STATE_REFRESH:
                mPullListView.onPullDownRefreshComplete();
                break;
            case STATE_LOADMORE:
                mPullListView.onPullUpRefreshComplete();
                break;
        }
        mState = STATE_NONE;
    }

    /**
     * 暴露给外部的刷新方法
     */
    public void refresh() {
        sendRequestData();
    }
}
