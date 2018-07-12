package com.moudle.app.bean;

import com.moudle.app.R;
import com.moudle.app.fragment.AddInfoFragment;
import com.moudle.app.fragment.InfoDetailFragment;
import com.moudle.app.fragment.LoginFragment;
import com.moudle.app.fragment.ModifyMyInfoFragment;
import com.moudle.app.fragment.MyInfoSignInFragment;
import com.moudle.app.fragment.QueryCollectionInfoFragment;
import com.moudle.app.fragment.QueryContactsFragment;
import com.moudle.app.fragment.QueryInfoFragment;
import com.moudle.app.fragment.QuerySignInInfoFragment;
import com.moudle.app.fragment.RegFragment;
import com.moudle.app.fragment.SelectContactsFragment;
import com.moudle.app.fragment.SettingsFragment;
import com.moudle.app.fragment.SettingsNotificationFragment;
import com.moudle.app.fragment.FeedBackFragment;

/**
 * @Description 标题暂不加,（活动、新闻）
 * @Author Li Chao
 * @Date 2016/1/4 10:18
 */
public enum SimpleBackPage {
    LOGIN(2, LoginFragment.class, R.string.login_button),
    REG(3, RegFragment.class, R.string.app_reg),
    FEEDBACK(4, FeedBackFragment.class, R.string.mine_feedback),
    SETTING(5, SettingsFragment.class, R.string.main_menu_setting),
    SETTING_NOTIFICATION(6, SettingsNotificationFragment.class, R.string.app_push),
    INFODETAIL(9, InfoDetailFragment.class, R.string.info_detail),
    SINGAL_COLLEGE_LIST(10, QueryContactsFragment.class, R.string.costant_person),
    ADD_INFO(11, AddInfoFragment.class, R.string.add_new_message),
    SELECT_CONSTACT(12,SelectContactsFragment.class,R.string.select_constact),
    QUERY_INFO(13,QueryInfoFragment.class,R.string.info_info),
    QUERY_COLLECTION_INFO(14,QueryCollectionInfoFragment.class,R.string.my_collection_info),
    QUERY_SIGN_IN_INFO(15,QuerySignInInfoFragment.class,R.string.my_sign_in_info),
    QUERY_SIGN_IN_INFO_MENU(16,MyInfoSignInFragment.class, R.string.my_sign_in_info),
    MODIFY_MY_INFO(17, ModifyMyInfoFragment.class,R.string.my_info);
    private Class<?> clz;
    private int value;
    private int title;

    private SimpleBackPage(int value, Class<?> clz, int title) {
        this.value = value;
        this.clz = clz;
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Class<?> getClz() {
        return clz;
    }


    public void setClz(Class<?> clz) {
        this.clz = clz;
    }


    public int getValue() {
        return value;
    }


    public void setValue(int value) {
        this.value = value;
    }


    public static SimpleBackPage getPageByValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValue() == val)
                return p;
        }
        return null;
    }
}
