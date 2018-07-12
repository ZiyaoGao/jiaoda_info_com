package com.moudle.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.adapter.CollegeAdapter;
import com.moudle.app.adapter.InfoAdapter;
import com.moudle.app.base.BaseHaveHeaderListFragment;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.base.BaseListFragment;
import com.moudle.app.bean.College;
import com.moudle.app.bean.Info;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.EmptyLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 查询学院信息列表(我的联系人)
 * Created by Administrator on 2016/5/7.
 */
public class QuerySchollFragment extends BaseHaveHeaderListFragment<College> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(BUNDLE_NEWS_TYPE, 0);
        }
    }

    @Override
    public void initData() {
//        IntentFilter filter = new IntentFilter(Constant.JDMDATA_ACTION_USER_CHANGE);
//        filter.addAction(Constant.JDMDATA_ACTION_USER_ADD_CHANGE);
//        getActivity().registerReceiver(mReceiver, filter);
    }

    public final static int MODIFYINFO = 0x01;


    @Override
    protected BaseListAdapter<College> getListAdapter() {
        return new CollegeAdapter();
    }

    @Override
    protected Serializable getSerializable(String... params) throws AppException {
        return null;
    }

    /**
     * 取最新数据
     */
    @Override
    protected void sendRequestData() {
        // bmobQuery.setLimit(20);
//        //先判断是否有缓存
//        boolean isCache = bmobQuery.hasCachedResult(getActivity(), Person.class);
//        if (isCache) {
//            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
//        } else {
//            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
//        }
        mAdapter.clear();
        final BmobQuery<College> bmobQuery = new BmobQuery<College>();
        bmobQuery.order("createdAt");
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        bmobQuery.findObjects(getActivity(), new FindListener<College>() {
            @Override
            public void onSuccess(List<College> object) {
                List<College> result = new ArrayList<College>();
                for (College college : object) {
                    result.add(college);
                }
                if (object != null) {
                    if (mOperation) {
                        new SaveCacheTask(getActivity(), (Serializable) result, getCacheKey()).execute();
                    }
                    executeOnLoadFinish();
                    executeOnLoadDataSuccess(result);
                } else {
                    executeOnLoadDataError("");
                }
            }

            @Override
            public void onError(int code, String msg) {
                //  UIHelper.ToastMessage(getActivity(), "查询失败：" + msg);
                if (code == 9009) {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
                } else {
                    executeOnLoadDataError(msg);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0)
            return;
        College college = mAdapter.getData().get(position - 1);
        Bundle args = new Bundle();
        args.putSerializable("College", college);
        UIHelper.showSimpleBack(this, SimpleBackPage.SINGAL_COLLEGE_LIST, args, MODIFYINFO);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == MODIFYINFO) {
            //((FragmentMainActivity) getActivity()).setIsSlideMenu(true);//设置侧滑按钮可用
            sendRequestData();
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
        return false;
    }

    @Override
    protected View initHeaderView() {
        return mInflater.inflate(R.layout.list_college_info, null);
    }
}
