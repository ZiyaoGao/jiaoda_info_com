package com.moudle.app.common;


import java.util.Comparator;
import com.moudle.app.bean.PinyinEntity;

/**
 * @author xiaanming
 * @Description 根据拼音来排列ListView里面的数据类
 */
public class PinyinComparator implements Comparator<PinyinEntity> {
    public int compare(PinyinEntity o1, PinyinEntity o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
