package com.moudle.app.bean;

import java.io.Serializable;


/**
 * 接口URL实体类
 * 由于某些接口原因不能用,用原来的流量管家接口
 */

public class URLs implements Serializable {

    public final static String HTTP = "http://";
    public final static String JDM_URL_API = "192.168.0.205:8080/moudle/api";
    public final static String JDM_VISIT_ADDRESS = HTTP + JDM_URL_API;
    /**
     * 活动列表
     */
    public final static String COMPETITION_LIST = JDM_VISIT_ADDRESS + "/match/list";

    /**
     * 用户注册
     **/
    public final static String REGISTER_URL = JDM_VISIT_ADDRESS + "/user/register";

    /**
     * 忘记密码
     **/
    public final static String RESTET_PASSWORD_URL = JDM_VISIT_ADDRESS + "/user/resetpwd";

    /**
     * 修改密码
     **/
    public final static String CHANGE_PASSWORD_URL = JDM_VISIT_ADDRESS + "/user/changepwd";

    /**
     * 首页图片轮播URL
     **/
    public final static String BANNER_URL = JDM_VISIT_ADDRESS + "/main/banner/list";

    /**
     * 意见反馈URL
     **/
    public final static String FEEDBACK_URL = JDM_VISIT_ADDRESS + "/system/feedback";

    /**
     * 获取验证码
     **/
    public final static String GET_VERIFY_CODE_URL = JDM_VISIT_ADDRESS + "/verify/sendCode?phone=";

    /**
     * 用户登录
     **/
    public final static String USER_LOGIN_URL = JDM_VISIT_ADDRESS + "/user/login";

    /**
     * 用户信息更新
     **/
    public final static String USER_UPDATE_URL = JDM_VISIT_ADDRESS + "/user/updateProfile";

    public final static String HOST = "http://www.db898.com";

    /**
     * 升级地址
     */
    public final static String UPDATE_VERSION = JDM_VISIT_ADDRESS + "/system/update";

    /***
     * 服务协议
     */
    public final static String SERVICE_PROTOCAL = JDM_VISIT_ADDRESS + "/service/getServiceProtocol";

    /**
     * 获取验证码
     */
    public final static String VERIFICATION_CODE = JDM_VISIT_ADDRESS + "/user/vcode";

    /**
     * 获取签到记录
     */
    public final static String SIGNINRECORD = JDM_VISIT_ADDRESS + "/user/attendance/month";

    /**
     * 签到
     */
    public final static String SIGN_IN = JDM_VISIT_ADDRESS + "/user/attendance";

    /**
     * 视频
     */
    public final static String VIDEO = JDM_VISIT_ADDRESS + "/video/list";
    /**
     * 用户消息
     */
    public final static String USERMESSAGE = HTTP + "192.168.0.205:8080/moudleServer/api" + "/sysmsg/list";
    /**
     * 用户消息阅读
     */
    public final static String USERMSGREAD = HTTP + "192.168.0.205:8080/moudleServer/api" + "/sysmsg/setRead";
    /**
     * 用户消息全部阅读
     */
    public final static String USERMSGALLREAD = HTTP + "192.168.0.205:8080/moudleServer/api" + "/sysmsg/setAllRead";
    /**
     * 用户头像
     */
    public final static String USERIMG = JDM_VISIT_ADDRESS + "/user/upimg";

    /**
     * 用户修改昵称
     */
    public final static String USERNICKNAME = JDM_VISIT_ADDRESS + "/user/changename";
    /**
     * 用户修改性别
     */
    public final static String USERGENDER = JDM_VISIT_ADDRESS + "/user/changegender";

    /**
     * 用户未读信息查询
     */
    public final static String USERUNREADMSG = JDM_VISIT_ADDRESS + "/sysmsg/unreadCount";
    /**
     * 新闻
     */
    public final static String NEWS = HTTP + JDM_URL_API + "/news/list";
    /**
     * 新闻详情
     */
    public final static String NEWSDETAIL = HTTP + JDM_URL_API + "/news/newsDetail?uuid=";
    /**
     * 新闻赞美
     */
    public final static String NEWSPRAISE = HTTP + JDM_URL_API + "/news/addPraiseCount?uuid=";
}
