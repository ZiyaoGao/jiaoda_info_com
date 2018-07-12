package com.moudle.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.adapter.InfoAdapter;
import com.moudle.app.base.BaseHaveHeaderListFragment;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.bean.Info;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.EmptyLayout;
import com.moudle.app.widget.TitleModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 标记的信息
 *
 * Created by Administrator on 2016/5/7.
 */
public class QuerySignInInfoFragment extends BaseHaveHeaderListFragment<Info> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt("signType", 0);
        }
    }


    public final static int MODIFYINFO = 0x01;

    @Override
    protected View initHeaderView() {
        return mInflater.inflate(R.layout.list_info, null);
    }

    @Override
    protected BaseListAdapter<Info> getListAdapter() {
        return new InfoAdapter();
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
        mAdapter.clear();
        final BmobQuery<Info> bmobQuery = new BmobQuery<Info>();
        bmobQuery.order("createdAt");
        bmobQuery.addWhereEqualTo("userID", AppContext.user.getUuid());
        bmobQuery.addWhereNotEqualTo("type", 3);
        bmobQuery.addWhereEqualTo("isSignType", mCatalog);
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        bmobQuery.findObjects(getActivity(), new FindListener<Info>() {
            @Override
            public void onSuccess(List<Info> object) {
                List<Info> result = new ArrayList<Info>();
                for (Info info : object) {
                    result.add(info);
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
        Info info = mAdapter.getData().get(position - 1);
        Bundle args = new Bundle();
        args.putSerializable("Info", info);
        UIHelper.showSimpleBack(this, SimpleBackPage.INFODETAIL, args, MODIFYINFO);
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

    /**
     * 初始化头部
     *
     * @param titleModel
     */
    protected void initTitle(TitleModel titleModel) {

    }
}
