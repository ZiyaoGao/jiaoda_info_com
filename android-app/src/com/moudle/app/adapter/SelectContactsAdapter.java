package com.moudle.app.adapter;/**
 * Created by x on 16-4-22.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.bean.Person;

import java.util.HashMap;

/**
 * @Description 联系人 适配
 * @Author Li Chao
 * @Date 16-4-22 14:06
 */
public class SelectContactsAdapter extends BaseListAdapter<Person> {
    public interface ContactCallback {
        void selectContact(int position, boolean isChecked);
    }

    public ContactCallback contactCallback;
    private HashMap<Integer, Boolean> select = new HashMap();
    private boolean isReset;

    public HashMap<Integer, Boolean> getSelect() {
        return select;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 自定义视图
        ViewHolder viewHolder = null;
        Person person = mDatas.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_select_contacts_info, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.iv_operation = (CheckBox) convertView.findViewById(R.id.iv_operation);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(person.getName());
        viewHolder.iv_operation.setTag(position);
        if (select.containsKey(position)) {
            viewHolder.iv_operation.setChecked(select.get(position));
        } else {
            viewHolder.iv_operation.setChecked(false);
        }
        ViewHolder finalViewHolder = viewHolder;
        viewHolder.iv_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (person.getCollegeId().equals(AppContext.user.collegeId)) {
                    select.put((Integer) v.getTag(), finalViewHolder.iv_operation.isChecked());
                } else {
                    // 重置，确保最多只有一项被选中
                    for (Integer key : select.keySet()) {
                        select.put(key, false);
                    }
                    select.put((Integer) v.getTag(), finalViewHolder.iv_operation.isChecked());
                    SelectContactsAdapter.this.notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    public class ViewHolder { // 自定义控件集合
        private TextView tv_name;
        private CheckBox iv_operation;
    }
}
