package com.moudle.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moudle.app.bean.Constant;
import com.moudle.app.bean.GlobalVariable;
import com.moudle.app.bean.Person;
import com.moudle.app.cache.CacheManager;
import com.moudle.app.api.ApiClient;
import com.moudle.app.bean.Operation;
import com.moudle.app.bean.User;
import com.moudle.app.common.DataCleanManager;
import com.moudle.app.common.MethodsCompat;
import com.moudle.app.common.MiddleClient;
import com.moudle.app.common.StringUtils;
import com.moudle.app.common.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;


/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 */
public class AppContext extends Application {

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static final int PAGE_SIZE = 10;// 默认分页大小
    private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

    private Boolean login = null; // 登录状态
    //private String loginUid = null; // 登录用户的id
    public static User user;//使用静态变量，考虑内存的回收问题，导致一些空指针异常
    private String saveImagePath;// 保存图片路径
    private static AppContext instance;
    public final String defaultBenginTime = "8:00";
    public final String defaultEndTime = "22:00";

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 注册App异常崩溃处理器
        //Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        init();
    }


    public boolean isLogin() {
        return this.login;
        //return true;//TODO 用于展示，所以都改为永远在线
    }


    /**
     * 初始化用户登录信息
     */
    public void initLoginInfo() {
        // User loginUser = getLoginInfo();
        if (getProperty(AppConfig.USER_STATUS) != null)
            login = getProperty(AppConfig.USER_STATUS).equals("login");
        if (login != null && login) {
            user = (User) CacheManager.readObject(this, GlobalVariable.USER);
        } else {
            Logout();
        }

    }

    /**
     * 用户登录验证
     *
     * @param account
     * @param pwd
     * @param ismd5
     * @return
     * @throws AppException
     */
    public User loginVerify(String account, String pwd, boolean ismd5) throws AppException {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd))
            return null;
        User u = MiddleClient.getLoginedInfo(account, pwd, ismd5);
        if (u != null) {
            setUser(u);
        }
        return u;
    }


    public void setUser(User u) {
//        setProperty(AppConfig.LOGIN_USERNAME, u.name);
//        setProperty(AppConfig.LOGIN_PASSWORD, u.password);
//        setProperty(AppConfig.LOGIN_GENDER, u.gender + "");
//        setProperty(AppConfig.LOGIN_REALNAME, u.name);
        // setProperty(AppConfig.LOGIN_PHONE, u.phone);
        if (containsProperty(AppConfig.USER_PHOTO)) {
            //removeProperty(AppConfig.USER_PHOTO);
            u.photo = getProperty(AppConfig.USER_PHOTO);
        }

        // this.loginUid = u.loginId;
        this.user = u;
        this.login = true;
        CacheManager.saveObject(this,user,GlobalVariable.USER);
    }


    /**
     * 保存一些用户特殊信息 例如照片
     */
    public void setUserSpecial() {
        setProperty(AppConfig.USER_PHOTO, user.photo);
    }


    /**
     * 初始化
     */
    private void init() {
        // 设置保存图片的路径
        saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
        if (StringUtils.isEmpty(saveImagePath)) {
            setProperty(AppConfig.SAVE_IMAGE_PATH, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
            saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
        }
        //initLoginInfo();
        if (!isNetworkConnected()) {
            UIHelper.ToastMessage(this, getString(R.string.network_not_connected));
        }
        //设置BmobConfig
        BmobConfig config = new BmobConfig.Builder()
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setBlockSize(500 * 1024)
                .build();
        Bmob.getInstance().initConfig(config);
        //Bmob初始化
        Bmob.initialize(this, Constant.BMOB_APPID);
        // 创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        // 全局初始化此配置
        ImageLoader.getInstance().init(configuration);
        initLoginInfo();
    }


    /**
     * 检测当前系统声音是否为正常模式
     *
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }


    /**
     * 应用程序是否发出提示音
     *
     * @return
     */
    public boolean isAppSound() {
        return isAudioNormal() && isVoice();
    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }


    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }


    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }


    /**
     * 用户注销
     */
    public void Logout() {
        ApiClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        //this.loginUid = null;
        this.removeProperty(AppConfig.LOGIN_PASSWORD);
    }


    /**
     * @return
     * @throws AppException
     */
    public String getServiceProtocol() throws AppException {
        String protocol = "";
        if (isNetworkConnected()) {
            try {
                protocol = MiddleClient.getProtocolDetail();
            } catch (AppException e) {
                throw e;
            }
        }
        return protocol;
    }


    /**
     * 是否加载显示文章图片
     *
     * @return
     */
    public boolean isLoadImage() {
        String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
        // 默认是加载的
        if (StringUtils.isEmpty(perf_loadimage))
            return true;
        else
            return StringUtils.toBool(perf_loadimage);
    }


    /**
     * 设置是否加载文章图片
     *
     * @param b
     */
    public void setConfigLoadimage(boolean b) {
        setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
    }


    /**
     * 是否发出提示音
     *
     * @return
     */
    public boolean isVoice() {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        // 默认是开启提示声音
        if (StringUtils.isEmpty(perf_voice))
            return true;
        else
            return StringUtils.toBool(perf_voice);
    }

    /**
     * 是否震动提示
     *
     * @return
     */
    public boolean isVibration() {
        String perf_voice = getProperty(AppConfig.CONF_VIBRATION);
        // 默认是开启提示震动
        if (StringUtils.isEmpty(perf_voice))
            return true;
        else
            return StringUtils.toBool(perf_voice);
    }

    /**
     * 设置是否发出提示音
     *
     * @param b
     */
    public void setConfigVoice(boolean b) {
        setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
    }

    /**
     * 设置是否发出震动
     *
     * @param b
     */
    public void setConfigVibration(boolean b) {
        setProperty(AppConfig.CONF_VIBRATION, String.valueOf(b));
    }

    /**
     * 是否启动检查更新
     *
     * @return
     */
    public boolean isCheckUp() {
        String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
        // 默认是开启
        if (StringUtils.isEmpty(perf_checkup))
            return true;
        else
            return StringUtils.toBool(perf_checkup);
    }


    /**
     * 设置启动检查更新
     *
     * @param b
     */
    public void setConfigCheckUp(boolean b) {
        setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
    }


    /**
     * 是否左右滑动
     *
     * @return
     */
    public boolean isScroll() {
        String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
        // 默认是关闭左右滑动
        if (StringUtils.isEmpty(perf_scroll))
            return false;
        else
            return StringUtils.toBool(perf_scroll);
    }


    /**
     * 设置是否左右滑动
     *
     * @param b
     */
    public void setConfigScroll(boolean b) {
        setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
    }


    /**
     * 设置是否推送
     *
     * @param b
     */
    public void setConfigisRecommends(boolean b) {
        setProperty(AppConfig.CONF_RECOMMENDS, String.valueOf(b));
    }


    public boolean isRecommends() {
        String perf_recommends = getProperty(AppConfig.CONF_RECOMMENDS);
        // 默认是http
        if (StringUtils.isEmpty(perf_recommends))
            return true;
        else
            return StringUtils.toBool(perf_recommends);
    }


    public boolean hasRecommendTime() {
        String perf_recommends_time = getProperty(AppConfig.CONF_RECOMMENDS_TIME);
        // 默认是http
        if (StringUtils.isEmpty(perf_recommends_time)) {
            return false;
        } else {
            if (perf_recommends_time.contains("null")) {
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 获取推送时间
     */
    public String getConfigisRecommendsTime() {
        if (hasRecommendTime()) {
            return getProperty(AppConfig.CONF_RECOMMENDS_TIME);
        } else {
            return defaultBenginTime + "~" + defaultEndTime;
        }
    }


    /**
     * 设置推送时间
     */
    public void setConfigisRecommendsTime(String begintime, String endtime) {
        if (StringUtils.isEmpty(begintime) || StringUtils.isEmpty(endtime))
            return;
        setProperty(AppConfig.CONF_RECOMMENDS_TIME, begintime + "~" + endtime);
    }


    /**
     * 获取推送的开始时间
     */
    public String getRecommendsBeginTime() {
        if (hasRecommendTime()) {
            return getProperty(AppConfig.CONF_RECOMMENDS_TIME).split("~")[0];
        } else {
            return defaultBenginTime;
        }
    }


    /**
     * 获取推送的结束时间
     */
    public String getRecommendsEndTime() {
        if (hasRecommendTime()) {
            return getProperty(AppConfig.CONF_RECOMMENDS_TIME).split("~")[1];
        } else {
            return defaultEndTime;
        }
    }


    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }


    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }


    /**
     * 判断缓存是否失效
     *
     * @param cachefile
     * @return
     */
    public boolean isCacheDataFailure(String cachefile) {
        boolean failure = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }


    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat.getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws java.io.IOException
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }


    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }


    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }


    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }


    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }


    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }


    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }


    /**
     * 获取内存中保存图片的路径
     *
     * @return
     */
    public String getSaveImagePath() {
        return saveImagePath;
    }


    /**
     * 设置内存中保存图片的路径
     *
     * @return
     */
    public void setSaveImagePath(String saveImagePath) {
        this.saveImagePath = saveImagePath;
    }


    /**
     * 注册
     *
     * @param account
     * @param password
     * @param code
     * @return
     * @throws AppException
     * @throws IOException
     */
    public User regVerify(String account, String password, String code) throws AppException {
        if (StringUtils.isEmpty(account))
            return null;
        User user = MiddleClient.userReg(account, password, code);
        if (user.isStatus()) {
            setUser(user);
        }
        return user;
    }


    /**
     * 重设密码(用于密码忘记)
     *
     * @param account
     * @param password
     * @param code
     * @return
     * @throws AppException
     * @throws IOException
     */
    public User resetPwd(String account, String password, String code) throws AppException {
        if (StringUtils.isEmpty(account))
            return null;
        User user = MiddleClient.resetPassword(account, password, code);
        if (user.isStatus()) {
            setUser(user);
        }
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
    public User changePassword(String oldpwd, String newpwd) throws AppException {
        User newPwdUser = MiddleClient.modityPassword(user.loginId, oldpwd, newpwd);
        if (newPwdUser.isStatus()) {
            setUser(newPwdUser);
        }
        return newPwdUser;
    }


    /**
     * 签到
     *
     * @return
     * @throws AppException
     * @throws IOException
     */
    public Operation signIn() throws AppException {
        if (!login)
            return null;
        Operation entity = MiddleClient.getSignIn(user.uuid);
        return entity;
    }


    /**
     * 获取验证码
     *
     * @param account
     * @return
     * @throws AppException
     */
    public Operation getCode(String account) throws AppException {
        if (StringUtils.isEmpty(account))
            return null;
        JSONObject jsonObject = MiddleClient.getCodeLogin(account);
        Operation entity = new Operation(jsonObject);
        return entity;
    }
}
