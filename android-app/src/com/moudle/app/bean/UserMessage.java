package com.moudle.app.bean;

import com.moudle.app.AppException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description 用户信息
 * @Author Li Chao
 * @Date 2015/12/16 15:43
 */
public class UserMessage extends Entity {
    private String result;
    private String liveflag;
    private String createtime;
    private String msgtitle;
    private String isreadflag;
    private String msgtext;
    private String remark;
    private String msgtype;
    private String platform;
    public boolean isLastMsg;//是否是最后一条消息

    public UserMessage(JSONObject json) {
        super(json);
    }

    public static UserMessage parse(JSONObject jsonObj) throws AppException {
        if (jsonObj == null)
            return new UserMessage(jsonObj);
        UserMessage userMessage = new UserMessage(jsonObj);
        try {
            userMessage.setResult(jsonObj.getString("result"));
            userMessage.setLiveflag(jsonObj.getString("liveflag"));
            userMessage.setCreatetime(jsonObj.getString("createtime"));
            userMessage.setMsgtitle(jsonObj.getString("msgtitle"));
            userMessage.setIsreadflag(jsonObj.getString("isreadflag"));
            userMessage.setMsgtext(jsonObj.getString("msgtext"));
            userMessage.setRemark(jsonObj.getString("remark"));
            userMessage.setUuid(jsonObj.getString("uuid"));
            userMessage.setMsgtype(jsonObj.getString("msgtype"));
            userMessage.setPlatform(jsonObj.getString("platform"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw AppException.parse(e);
        }
        return userMessage;
    }


    public static List<UserMessage> parseList(JSONObject jsonObj) throws AppException {
        if (jsonObj == null) {
            return new ArrayList<UserMessage>();
        }
        List<UserMessage> userMessages = null;
        try {
            if (new Operation(jsonObj).status && jsonObj.getJSONArray("dataList") != null) {
                userMessages = new ArrayList<UserMessage>();
                JSONArray jsonlist = jsonObj.getJSONArray("dataList");
                for (int i = 0; i < jsonlist.length(); i++) {
                    JSONObject jsonObject = jsonlist.getJSONObject(i);
                    UserMessage userMessage = UserMessage.parse(jsonObject);
                    userMessages.add(userMessage);
                }
                return userMessages;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw AppException.parse(e);
        }
        return userMessages;
    }


    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }


    public String getLiveflag() {
        return liveflag;
    }


    public void setLiveflag(String liveflag) {
        this.liveflag = liveflag;
    }


    public String getCreatetime() {
        return createtime;
    }


    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }


    public String getMsgtitle() {
        return msgtitle;
    }


    public void setMsgtitle(String msgtitle) {
        this.msgtitle = msgtitle;
    }


    public String getIsreadflag() {
        return isreadflag;
    }


    public void setIsreadflag(String isreadflag) {
        this.isreadflag = isreadflag;
    }


    public String getMsgtext() {
        return msgtext;
    }


    public void setMsgtext(String msgtext) {
        this.msgtext = msgtext;
    }


    public String getRemark() {
        return remark;
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getMsgtype() {
        return msgtype;
    }


    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }


    public String getPlatform() {
        return platform;
    }


    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
