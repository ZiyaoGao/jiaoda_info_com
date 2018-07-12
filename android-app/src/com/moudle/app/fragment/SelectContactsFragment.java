package com.moudle.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.adapter.ContactsAdapter;
import com.moudle.app.adapter.SelectContactsAdapter;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.base.BaseListFragment;
import com.moudle.app.bean.College;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.DialogHelp;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.EmptyLayout;
import com.moudle.app.widget.TitleModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 选择联系人列表
 * Created by Administrator on 2016/5/7.
 */
public class SelectContactsFragment extends BaseListFragment<Person> implements SelectContactsAdapter.ContactCallback {
    private College college;//需要读取的信息类
    private AlertDialog singleAlerDialog;
    private HashMap<Integer, Person> select = new HashMap<>();

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
    protected SelectContactsAdapter getListAdapter() {
        return new SelectContactsAdapter();
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
        ((SelectContactsAdapter) mAdapter).contactCallback = this;
        final BmobQuery<Person> bmobQuery = new BmobQuery<Person>();
        bmobQuery.order("createdAt");
        bmobQuery.addWhereNotEqualTo("objectId", AppContext.user.uuid);
        bmobQuery.addWhereGreaterThanOrEqualTo("level", AppContext.user.level);
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
//        Person person = mAdapter.getData().get(position - 1);
//        Bundle args = new Bundle();
//        args.putSerializable("Person", person);
//        UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ADD_INFO, args);
    }

    @Override
    public void selectContact(int position, boolean isChecked) {
  /*    if (AppContext.user.collegeId.equals(mAdapter.getItem(position).getCollegeId())) {
            if (isChecked) {
                select.put(position, mAdapter.getItem(position));
            } else {
                select.remove(position);
            }
       } else {
            if (select.size() > 0) {
                select.clear();
            }
            select.put(position, mAdapter.getItem(position));
        }
*/
    }

    /**
     * 初始化头部
     *
     * @param titleModel
     */
    protected void initTitle(TitleModel titleModel) {
        TextView textView = titleModel.creatRightView(TextView.class);
        textView.setText("完成");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                Bundle args = new Bundle();
                HashMap<Integer, Boolean> adapterSelect=((SelectContactsAdapter)mAdapter).getSelect();
                for (Integer key : adapterSelect.keySet()) {
                    if(adapterSelect.get(key)){
                        select.put(key,mAdapter.getItem(key));
                    }
                }
                args.putSerializable("select", select);
                data.putExtras(args);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });
        titleModel.submit();

    }
}
