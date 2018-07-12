package com.moudle.app.common;

import android.content.ClipboardManager;
import android.content.Context;

import com.moudle.app.AppException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串操作工具包
 *
 * @author konbluesky
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    private final static Pattern emailer =
            Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    public final static String UTF8 = "utf-8";
    public final static String GBK = "gbk";
    // private final static SimpleDateFormat dateFormater = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private final static SimpleDateFormat dateFormater2 = new
    // SimpleDateFormat("yyyy-MM-dd");

    public final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd");
        }
    };

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDateYmd(String sdate) {
        try {
            return dateFormater2.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static String toDateMd(String sdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        try {
            Date date = sdf.parse(sdate);
            return dateFormater3.get().format(date);
        } catch (ParseException e) {
            AppException.run(e);
        }
        return null;
    }

    /**
     * 判断是否同一天
     *
     * @param stf 用户的上一次签到时间
     * @return
     */
    public static boolean isSameDay(String stf) {
        if (isEmpty(stf))
            return false;
        Date day1 = toDate(stf);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = sdf.format(day1);
        String ds2 = dateFormater2.get().format(new Date());
        if (ds1.equals(ds2)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获得当前的时间()
     *
     * @param dateFormater
     * @return
     */
    public static String getCurrentDateTime(ThreadLocal<SimpleDateFormat> dateFormater) {
        return dateFormater.get().format(new Date());
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }


    /**
     * 计算两个日期之间相差的天数
     *
     * @param date 较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date smdate = sdf.parse(sdf.format(new Date()));
        Date bdate = sdf.parse(sdf.format(toDate(date)));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate yyyy-MM-dd
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDateYmd(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }


    /**
     * 判断给定字符串时间是否为今日
     *
     * @return boolean
     */
    public static boolean isToday(Date time) {
        boolean b = false;
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }


    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || input.equals("null"))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }


    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }


    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }


    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }


    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }


    public static String toEncode(String val, String charset) {
        if (isEmpty(val))
            return "";
        String s = "";
        try {
            s = URLEncoder.encode(val, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }


    public static String toDecode(String val, String charset) {
        if (isEmpty(val))
            return "";
        String s = "";
        try {
            s = URLEncoder.encode(val, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }


    /**
     * 获取指定HTML标签的指定属性的值
     *
     * @param source  要匹配的源文本
     * @param element 标签名称
     * @param attr    标签的属性名称
     * @return 属性值列表
     */
    public static String match(String source, String element, String attr) {
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?\\s.*?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            reg = m.group(1);
        }
        return reg;
    }


    // 获得当前时间的毫秒表示

    public static long getNow() {
        GregorianCalendar now = new GregorianCalendar();
        return now.getTimeInMillis();
    }


    // 获得n天后的日期
    public static String getNDays(int days) {
        // 86400000为一天的毫秒数
        return getDate(getNow() + 86400000 * days);
    }


    // 根据输入的毫秒数，获得日期字符串
    public static String getDate(long millis) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        return DateFormat.getDateInstance().format(calendar.getTime());

    }


    /**
     * 判断是否是正确的手机号码
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNum(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0,6-8])|(18[0,2,5-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();

    }


    /**
     * @param content
     * @param context
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }


    /**
     * 获取当月第几天
     *
     * @param str
     * @return
     */
    public static int getDay(String str) {
        String str2 = str.substring(str.length() - 2, str.length());
        int day = Integer.parseInt(str2);
        return day;
    }

    /**
     * 检测String是否全是中文
     * @param name
     * @return
     */
    public static boolean checkNameChese(String name)
    {
        boolean res=true;
        char [] cTemp = name.toCharArray();
        for(int i=0;i<name.length();i++)
        {
            if(!isChinese(cTemp[i]))
            {
                res=false;
                break;
            }
        }
        return res;
    }
    /**
     * 判定输入汉字
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 1.必须只能是 大写字母、小写字母和数字构成的密码
     2.大写字母、小写字母、数字都至少出现一次
     * @param password
     * @return
     */
    public static boolean checkPassword(String password){
        if(password.matches("\\w+")){
            Pattern p1= Pattern.compile("[a-z]+");
            Pattern p2= Pattern.compile("[A-Z]+");
            Pattern p3= Pattern.compile("[0-9]+");
            Matcher m=p1.matcher(password);
            if(!m.find())
                return false;
            else{
                m.reset().usePattern(p2);
                if(!m.find())
                    return false;
                else{
                    m.reset().usePattern(p3);
                    if(!m.find())
                        return false;
                    else{
                        return true;
                    }
                }
            }
        }else{
            return false;
        }

    }
}
