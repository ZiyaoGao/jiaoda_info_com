package com.moudle.app.adapter;/**
 * Created by x on 16-4-22.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moudle.app.AppContext;
import com.moudle.app.R;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.bean.College;
import com.moudle.app.bean.Person;

/**
 * @Description 联系人 适配
 * @Author Li Chao
 * @Date 16-4-22 14:06
 */
public class ContactsAdapter extends BaseListAdapter<Person> {
    public interface ContactCallback {
        void contactOperation(int position);
    }

    public ContactCallback contactCallback;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 自定义视图
        ViewHolder viewHolder = null;
        Person person = mDatas.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_contacts_info, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.iv_operation = (ImageView) convertView.findViewById(R.id.iv_operation);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (AppContext.user.role== 0) {
            viewHolder.iv_operation.setVisibility(View.GONE);
        } else {
            viewHolder.iv_operation.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_name.setText(person.getName());
        viewHolder.iv_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactCallback != null)
                    contactCallback.contactOperation(position);
            }
        });
        return convertView;
    }

    public class ViewHolder { // 自定义控件集合
        private TextView tv_name;
        private ImageView iv_operation;
    }
}
