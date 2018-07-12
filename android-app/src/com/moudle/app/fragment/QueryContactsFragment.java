package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;
import com.moudle.app.adapter.ContactsAdapter;
import com.moudle.app.adapter.InfoAdapter;
import com.moudle.app.base.BaseHaveHeaderListFragment;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.base.BaseListFragment;
import com.moudle.app.bean.College;
import com.moudle.app.bean.Info;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.DialogHelp;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.EmptyLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 查询某个学院的联系人列表
 * Created by Administrator on 2016/5/7.
 */
public class QueryContactsFragment extends BaseHaveHeaderListFragment<Person> implements ContactsAdapter.ContactCallback {
    private College college;//需要读取的信息类型
    private AlertDialog singleAlerDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(BUNDLE_NEWS_TYPE, 0);
            college = (College) args.getSerializable("College");
        }
    }


    @Override
    protected BaseListAdapter<Person> getListAdapter() {
        return new ContactsAdapter();
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
        ((ContactsAdapter) mAdapter).contactCallback = this;
        // bmobQuery.setLimit(20);
//        //先判断是否有缓存
//        boolean isCache = bmobQuery.hasCachedResult(getActivity(), Person.class);
//        if (isCache) {
//            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
//        } else {
//            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
//        }
        mAdapter.clear();
        final BmobQuery<Person> bmobQuery = new BmobQuery<Person>();
        bmobQuery.order("createdAt");
        bmobQuery.addWhereEqualTo("dataStatus", 0);
        bmobQuery.addWhereEqualTo("collegeId", college.getObjectId());
        bmobQuery.addWhereNotEqualTo("objectId", AppContext.user.getUuid());

        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        bmobQuery.findObjects(getActivity(), new FindListener<Person>() {
            @Override
            public void onSuccess(List<Person> object) {
                List<Person> result = new ArrayList<Person>();
                for (Person person : object) {
                    result.add(person);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0)
            return;
        Person person = mAdapter.getData().get(position-1);
        Bundle args = new Bundle();
        args.putSerializable("Person", person);
        UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ADD_INFO, args);
    }

    @Override
    public void contactOperation(int position) {
        Person person = mAdapter.getData().get(position);
        singleAlerDialog = DialogHelp.getSingleChoiceDialog(getActivity(), "请选择",
                new String[]{"授权", "删除"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                person.setRole(1);
                                person.update(getActivity(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        UIHelper.ToastMessage(getActivity(), "授权成功");
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
                                person.setDataStatus(1);
                                person.update(getActivity(), new UpdateListener() {
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
                                break;

                        }
                        singleAlerDialog.dismiss();
                    }
                }).create();
        singleAlerDialog.show();
    }

    @Override
    protected View initHeaderView() {
        return mInflater.inflate(R.layout.list_contacts_info, null);
    }
}
