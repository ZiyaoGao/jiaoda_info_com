package com.moudle.app.bean;/**
 * Created by x on 16-4-14.
 */

import org.json.JSONObject;

/**
 * @Description
 * @Author Li Chao
 * @Date 16-4-14 11:05
 */
public class PinyinEntity extends Entity {
    private String sortLetters;  //显示数据拼音的首字母

    public PinyinEntity(JSONObject json) {
        super(json);
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
