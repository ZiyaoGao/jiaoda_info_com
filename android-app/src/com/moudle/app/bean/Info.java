package com.moudle.app.bean;

/**
 *  消息
 * Created by lichao on 2017/5/5.
 */

public class Info extends Entity {
    private String userID;//接收用户ID
    private String sendUserID;//发送者ID
    private int type;//未读信息 0 已读信息 1 已删除信息 2 完全删除的信息 3
    private int isSignType;//未标记 0 不重要  1 一般 2 重要 3 非常重要 4
    private boolean isCollection;//是否收藏
    private String author;
    private String infoType;//消息类型
    private String content;

    public String getSendUserID() {
        return sendUserID;
    }

    public void setSendUserID(String sendUserID) {
        this.sendUserID = sendUserID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getIsSignType() {
        return isSignType;
    }

    public void setIsSignType(int isSignType) {
        this.isSignType = isSignType;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
