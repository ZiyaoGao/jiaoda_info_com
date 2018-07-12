package com.moudle.app.bean;


import android.util.Log;

import org.json.JSONObject;

/**
 * 实体类
 */
public abstract class Entity extends Base {
    protected Integer id;//对应自己数据库的id
    protected String uuid;
    private final static String MSG_ERROR = "网络回传数据解析异常";

    public Entity() {
    }

    public Entity(JSONObject json) {
        if (json == null || !initBase(json)) {
            message = MSG_ERROR;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    protected String cacheKey;

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    //用来存放服务器端回传信息
    public boolean status = false;
    public String message = "";

    public boolean initBase(JSONObject jsonObj) {
        boolean result = false;
        try {
            if (jsonObj != null) {
                result = true;
                status = jsonObj.optBoolean("status");
                message = jsonObj.optString("msg");
            }
        } catch (Exception e) {
            Log.e(MSG_ERROR, e.getMessage());
        }
        return result;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
