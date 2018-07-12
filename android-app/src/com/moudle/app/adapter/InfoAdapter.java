package com.moudle.app.adapter;/**
 * Created by x on 16-4-22.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moudle.app.R;
import com.moudle.app.base.BaseListAdapter;
import com.moudle.app.bean.IdCard;
import com.moudle.app.bean.Info;

/**
 * @Description
 * @Author Li Chao
 * @Date 16-4-22 14:06
 */
public class InfoAdapter extends BaseListAdapter<Info> {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 自定义视图
        ViewHolder viewHolder = null;
        Info info = mDatas.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_info, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_info_type = (TextView) convertView.findViewById(R.id.tv_info_type);
            viewHolder.tv_info_date = (TextView) convertView.findViewById(R.id.tv_info_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(info.getAuthor());
        viewHolder.tv_info_type.setText(info.getInfoType());
        viewHolder.tv_info_date.setText(info.getCreatedAt());
        return convertView;
    }

    public class ViewHolder { // 自定义控件集合
        private TextView tv_name;
        private TextView tv_info_type;
        private TextView tv_info_date;
    }
}
