package com.moudle.app.common;


import com.moudle.app.AppException;
import com.moudle.app.api.ApiClient;
import com.moudle.app.bean.GlobalVariable;
import com.moudle.app.bean.Operation;
import com.moudle.app.bean.URLs;
import com.moudle.app.bean.Update;
import com.moudle.app.bean.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 调用Client的中间层 Created by Administrator on 2015/11/19.
 */
public class MiddleClient {
    /**
     * 注册
     *
     * @param account
     * @param password
     * @param code
     * @return User
     * @throws AppException
     */
    public static User userReg(String account, String password, String code) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, account));
        params.add(new BasicNameValuePair("pwd", password));
        params.add(new BasicNameValuePair("vcode", code));
        InputStream is = ApiClient._post(URLs.REGISTER_URL, params);
        User user = User.parse(ApiClient.toJSONObj(is));
        return user;
    }


    /**
     * userLogin登录方法
     *
     * @param account
     * @param pwd
     * @param ismd5
     * @return User
     * @throws AppException
     */
    public static User getLoginedInfo(String account, String pwd, boolean ismd5) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        /*
         * 增加ismd5区别字段，因为一次注册成功后返回的是pwd是加密后的字段，所以再次访问时
         * 要通知服务端不要进行再次加密验证，以防二次加密对数据造成破坏
         */
        if (ismd5) {
            params.add(new BasicNameValuePair("ismd5", String.valueOf(ismd5)));
        }
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, account));
        params.add(new BasicNameValuePair("pwd", pwd));
        InputStream is = ApiClient._post(URLs.USER_LOGIN_URL, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        User user = User.parse(jsonObject);
        return user;
    }



    /**
     * 签到
     *
     * @param userUUID
     * @return
     * @throws AppException
     */
    public static Operation getSignIn(String userUUID) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(GlobalVariable.USERUUID, userUUID));
        InputStream is = ApiClient._post(URLs.SIGN_IN, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        Operation entity = new Operation(jsonObject);
        return entity;
    }


    /**
     * 重置密码 用于忘记密码
     */
    public static User resetPassword(String account, String password, String code) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, account));
        params.add(new BasicNameValuePair("pwd", password));
        params.add(new BasicNameValuePair("vcode", code));
        InputStream is = ApiClient._post(URLs.RESTET_PASSWORD_URL, params);
        JSONObject jsonObj = ApiClient.toJSONObj(is);
        User user = User.parse(jsonObj);
        return user;
    }


    /**
     * 修改密码
     *
     * @param oldpwd
     * @param newpwd
     * @return
     * @throws AppException
     */
    public static User modityPassword(String loginId, String oldpwd, String newpwd) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, loginId));
        params.add(new BasicNameValuePair("oldpwd", oldpwd));
        params.add(new BasicNameValuePair("newpwd", newpwd));
        InputStream is = ApiClient._post(URLs.CHANGE_PASSWORD_URL, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        User user = User.parse(jsonObject);
        return user;
    }


    /**
     * 获取验证码
     *
     * @param account
     * @return
     * @throws AppException
     */
    public static JSONObject getCodeLogin(String account) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, account));
        InputStream is = ApiClient._post(URLs.VERIFICATION_CODE, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        return jsonObject;
    }


    /**
     * 发送反馈内容
     *
     * @param userUUID
     * @param text
     * @return
     * @throws AppException
     */
    public static Operation sendFeedBack(String userUUID, String text) throws AppException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(GlobalVariable.USERUUID, userUUID));
        params.add(new BasicNameValuePair("text", text));
        InputStream is = ApiClient._post(URLs.FEEDBACK_URL, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        Operation entity = new Operation(jsonObject);
        return entity;
    }

    /**
     * 上传用户头像
     *
     * @return
     */
    public static Operation upUserHeadPortrait(String loginId, String filePath) throws AppException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, loginId));
        params.add(new BasicNameValuePair(GlobalVariable.FILEPATH, filePath));
        String result = ApiClient.image_post(URLs.USERIMG, params);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            AppException.run(e);
        }
        Operation entity = new Operation(jsonObject);
        return entity;
    }

    /**
     * 修改用户昵称
     *
     * @return
     */
    public static Operation modifyUserNickName(String loginId, String name) throws AppException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, loginId));
        params.add(new BasicNameValuePair("name", name));
        InputStream is = ApiClient._post(URLs.USERNICKNAME, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        Operation entity = new Operation(jsonObject);
        return entity;
    }

    /**
     * 修改用户性别
     *
     * @return
     */
    public static Operation modifyUserGender(String loginId, String gender) throws AppException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(GlobalVariable.LOGINID, loginId));
        params.add(new BasicNameValuePair("gender", gender));
        InputStream is = ApiClient._post(URLs.USERGENDER, params);
        JSONObject jsonObject = ApiClient.toJSONObj(is);
        Operation entity = new Operation(jsonObject);
        return entity;
    }

    /**
     * 获得服务详情
     *
     * @return
     * @throws AppException
     */
    public static String getProtocolDetail() throws AppException {
        String newUrl = URLs.SERVICE_PROTOCAL;
        try {
            InputStream is = ApiClient._post(newUrl, null);
            JSONObject jsonObj = ApiClient.toJSONObj(is);
            if (jsonObj == null)
                return "";
            String protocol = "";
            try {
                if (jsonObj.getBoolean("status")) {
                    JSONObject json = jsonObj.getJSONObject("dataObj");
                    protocol = json.getString("content");
                }

            } catch (Exception e) {
                if (e instanceof IOException) {
                    e.printStackTrace();
                    throw AppException.io(e);
                }
                if (e instanceof AppException) {
                    e.printStackTrace();
                    throw AppException.parse(e);
                }
            }
            return protocol;
        } catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }


    /**
     * 检查版本更新
     *
     * @return
     */
    public static Update checkVersion() throws AppException {
        try {
            return Update.parse(ApiClient._post(URLs.UPDATE_VERSION, null));
        } catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }
}
