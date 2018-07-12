package com.moudle.app.adapter;/**
 * Created by x on 16-4-22.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moudle.app.R;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.bean.College;
import com.moudle.app.bean.Info;

/**
 * @Description 我的联系人 适配
 * @Author Li Chao
 * @Date 16-4-22 14:06
 */
public class CollegeAdapter extends BaseListAdapter<College> {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 自定义视图
        ViewHolder viewHolder = null;
        College college = mDatas.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_college_info, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(college.getName());
        return convertView;
    }

    public class ViewHolder { // 自定义控件集合
        private TextView tv_name;
    }
}
