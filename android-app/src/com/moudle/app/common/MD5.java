package com.moudle.app.common;

import java.security.MessageDigest;

/**
 * 对外提供getMD5(String)方法
 */
public class MD5 {

    /**
     * 将字符串转成MD5值
     *
     * @return
     */
    public static String get32MD5(String str) {
        if (str == null) str = "";

        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte byEncrypt[] = md.digest(str.getBytes());

            for (int i = 0; i < byEncrypt.length; i++) {
                sb.append(Integer.toHexString(byEncrypt[i] & 0xFFFF));
            }
        } catch (Exception ex) {
        }
        return sb.toString();
    }

//    public final static String get32MD5(String s) {
//        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
//        try {
//            byte[] btInput = s.getBytes();
//            // 获得MD5摘要算法的 MessageDigest 对象
//            MessageDigest mdInst = MessageDigest.getInstance("MD5");
//            // 使用指定的字节更新摘要
//            mdInst.update(btInput);
//            // 获得密文
//            byte[] md = mdInst.digest();
//            // 把密文转换成十六进制的字符串形式
//            int j = md.length;
//            char str[] = new char[j * 2];
//            int k = 0;
//            for (int i = 0; i < j; i++) {
//                byte byte0 = md[i];
//                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                str[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(str);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


}