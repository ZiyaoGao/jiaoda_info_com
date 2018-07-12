package com.moudle.app.bean;

import org.json.JSONObject;

/**
 * 用来处理增加、更新操作
 * 服务器传回的最原始JsonObject
 */
public class Operation extends Entity {

    public Operation(JSONObject json) {
        super(json);
    }
}
