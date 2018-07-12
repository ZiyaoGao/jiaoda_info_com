package com.moudle.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseFragMent;
import com.moudle.app.bean.Info;
import com.moudle.app.bean.InfoType;
import com.moudle.app.bean.Person;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.moudle.app.widget.TitleModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 新建信息
 * Created by Administrator on 2016/5/7.
 */
public class AddInfoFragment extends BaseFragMent {
    private Person person;
    private TextView et_name;
//    private EditText et_info_type;
    private EditText et_content;
    private Spinner info_spinner2;
    private Button btn_send;
    private TextView tv_add;
    private List<BmobObject> list = new ArrayList<>();
    private List<String> lists = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            person = (Person) args.getSerializable("Person");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_add_info, null);
        initView(v);
        initData();
        return v;
    }

    public void initView(View v) {
        et_name = (TextView) v.findViewById(R.id.et_name);
        info_spinner2 = (Spinner) v.findViewById(R.id.info_spinner2);
        et_content = (EditText) v.findViewById(R.id.et_content);
        btn_send = (Button) v.findViewById(R.id.btn_send);
        tv_add = (TextView) v.findViewById(R.id.tv_add);
        btn_send.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        if (person != null) {
            et_name.setText(person.getName());
            tv_add.setVisibility(View.GONE);
            Info info = new Info();
            info.setUserID(person.getObjectId());
            info.setSendUserID(AppContext.user.uuid);
            list.add(info);
        }
//        info_spinner2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
        final BmobQuery<InfoType> bmobQuery = new BmobQuery<InfoType>();
        bmobQuery.order("createdAt");
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        bmobQuery.findObjects(getActivity(), new FindListener<InfoType>() {
            @Override
            public void onSuccess(List<InfoType> object) {
                for (InfoType info : object) {
                    lists.add(info.getName());
                }
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lists);
                //第三步：为适配器设置下拉列表下拉时的菜单样式。
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //第四步：将适配器添加到下拉列表上
                info_spinner2.setAdapter(adapter);
            }

            @Override
            public void onError(int code, String msg) {
                if (code == 9009) {

                } else {
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (StringUtils.isEmpty(
                        et_name.getText().toString().trim() +
                        et_content.getText().toString().trim())) {
                    UIHelper.ToastMessage(getActivity(), "将输入框填写完整");
                    return;
                } else {
//第一种方式:v3.5.0之前的版本
                    for (BmobObject bmobObject : list) {
                        ((Info) bmobObject).setInfoType((String) info_spinner2.getSelectedItem());
                        ((Info) bmobObject).setContent(et_content.getText().toString());
                        ((Info) bmobObject).setAuthor(AppContext.user.name);
                    }
                    new BmobObject().insertBatch(getActivity(), list, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            UIHelper.ToastMessage(getActivity(), "发送成功");
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            UIHelper.ToastMessage(getActivity(), "发送失败");
                        }
                    });
                }
                break;
            case R.id.tv_add:
                UIHelper.showSimpleBack(AddInfoFragment.this, SimpleBackPage.SELECT_CONSTACT, null, 0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            HashMap<Integer, Person> map = (HashMap<Integer, Person>) data.getExtras().getSerializable("select");
            if (map != null) {
                String nameList = et_name.getText().toString();
                et_name.setText("");
                Iterator iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    Object key = (Object) iterator.next();
                    Info info = new Info();
                    info.setUserID(map.get(key).getObjectId());
                    info.setSendUserID(AppContext.user.uuid);
                    for (BmobObject bmobObject : list) {
                        if (((Info) bmobObject).getUserID().equals(map.get(key).getObjectId()))
                            list.remove(bmobObject);
                    }
                    list.add(info);
                    if (iterator.hasNext()) {
                        nameList = nameList + map.get(key).getName() + "\n";
                    } else {
                        nameList = nameList + map.get(key).getName();
                    }
                }
                et_name.setText(nameList);
            }
        }
    }
}
