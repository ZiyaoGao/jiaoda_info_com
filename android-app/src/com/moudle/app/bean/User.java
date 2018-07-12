package com.moudle.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.moudle.app.AppException;


/**
 * 用户数据模型
 */
public class User extends Entity implements Serializable {
    public User() {
    }

    public User(JSONObject json) {
        super(json);
    }

    public String createtime;
    public String liveflag;
    public String remark;
    public String uuid;
    public String loginId;
    public String password;
    public String phone;


    public String bigicon;//用户头像网络路径
    public String gender;
    public String age;
    public String number;
    public String grade;
    public boolean isSignIn;
    public int unReadCount;

    public String rule;
    public String bmobUuid;
    public String name;
    public String photo;//用户头像本地路径
    public String collegeId;
    public int role;
    public String account;
    public String pwd;
    public String postition;
    public int level;
    public final static int GENDER = 0;//性别
    public final static int NICKNAME = 1;//别名 昵称
    public final static String USERMODIFY = "user_info_modify";

    public static User parse(JSONObject jsonObj) throws AppException {
        if (jsonObj == null)
            return null;
        User user = new User(jsonObj);
        try {
            if (user.status) {
                JSONObject json = jsonObj.getJSONObject("dataObj");
                user.uuid = json.optString("uuid");
                if (json.optString("loginid").equals("null")) {
                    user.loginId = json.optString("phone");
                } else {
                    user.loginId = json.optString("loginid");
                }
                user.createtime = json.optString("createtime");
                user.liveflag = json.optString("liveflag");
                user.remark = json.optString("remark");
                user.name = json.optString("name");
                user.password = json.optString("pwd");
                user.phone = json.optString("phone");
                user.gender = json.optString("gender");
                user.bigicon = json.optString("bigicon");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw AppException.parse(e);
        }
        return user;
    }

    public User(String name, String age, String number, String grade) {
        this.name = name;
        this.age = age;
        this.number = number;
        this.grade = grade;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getLiveflag() {
        return liveflag;
    }

    public void setLiveflag(String liveflag) {
        this.liveflag = liveflag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBigicon() {
        return bigicon;
    }

    public void setBigicon(String bigicon) {
        this.bigicon = bigicon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    public void setIsSignIn(boolean isSignIn) {
        this.isSignIn = isSignIn;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public static int getGENDER() {
        return GENDER;
    }

    public static int getNICKNAME() {
        return NICKNAME;
    }

    public static String getUSERMODIFY() {
        return USERMODIFY;
    }
}
