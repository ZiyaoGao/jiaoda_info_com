package com.moudle.app.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.bean.URLs;

/**
 * API客户端接口：用于访问网络数据
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

    public static final String UTF_8 = "UTF-8";
    private final static int TIMEOUT_CONNECTION = 20000;
    private final static int TIMEOUT_SOCKET = 20000;
    private final static int RETRY_TIME = 3;

    //昵称
    public static int TYPE_NICKNAME = 0;
    //真实姓名
    public static int TYPE_REALNAME = 1;
    //性别
    public static int TYPE_GENDER = 2;

    private static String appCookie;
    private static String appUserAgent;


    public static void cleanCookie() {
        appCookie = "";
    }

    private static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }

    private static String getUserAgent(AppContext appContext) {
        if (appUserAgent == null || appUserAgent == "") {
            StringBuilder ua = new StringBuilder("moudle");
            ua.append('/' + appContext.getPackageInfo().versionName + '_' + appContext.getPackageInfo().versionCode);//App版本
            ua.append("/Android");//手机系统平台
            ua.append("/" + android.os.Build.VERSION.RELEASE);//手机系统版本
            ua.append("/" + android.os.Build.MODEL); //手机型号
            ua.append("/" + appContext.getAppId());//客户端唯一标识
            appUserAgent = ua.toString();
        }
        return appUserAgent;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        final HttpParams httpParams = new BasicHttpParams();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集
        httpClient.getParams().setContentCharset(UTF_8);
        //nagle算法默认是打开的，会引起delay的问题；所以要手工关掉。
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        return httpClient;
    }

    private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpGet.setRequestHeader("Host", URLs.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        httpGet.setRequestHeader("Cookie", cookie);
        httpGet.setRequestHeader("User-Agent", userAgent);
        httpGet.setRequestHeader("mobile-agent", userAgent);
        return httpGet;
    }

    private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
        PostMethod httpPost = new PostMethod(url);
        // 设置 请求超时时间
        httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpPost.setRequestHeader("Host", URLs.HOST);
        httpPost.setRequestHeader("Connection", "Keep-Alive");
        httpPost.setRequestHeader("Cookie", cookie);
        httpPost.setRequestHeader("User-Agent", userAgent);
        httpPost.setRequestHeader("mobile-agent", userAgent);
        return httpPost;
    }

    private static String _MakeURL(String p_url, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(p_url);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
            //不做URLEncoder处理
            //url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
        }

        return url.toString().replace("?&", "?");
    }

    /**
     * 拼接url参数 XXX/1-2-3
     *
     * @param p_url
     * @param params
     * @return
     */
    private static String makeURL(String p_url, List<String> params) {
        StringBuilder url = new StringBuilder(p_url);
        final int psize = params.size();
        for (int i = 0; i < psize; i++) {
            url.append(params.get(i));
            if (i != psize - 1) {
                url.append("-");
            }
        }
        return url.toString();
    }

    /**
     * get请求URL
     *
     * @param url
     * @throws AppException
     */
    private static InputStream http_get(AppContext appContext, String url) throws AppException {
        //System.out.println("get_url==> "+url);
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        GetMethod httpGet = null;
        HttpClient httpClient = null;

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, cookie, userAgent);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                InputStream inputStream = httpGet.getResponseBodyAsStream();
                responseBody = getStringFromStream(inputStream);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");

        return new ByteArrayInputStream(responseBody.getBytes());
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @throws AppException
     */
    public static InputStream _post(String url, List<NameValuePair> params) throws AppException {
        InputStream is = null;
        HttpResponse httpResponse = null;
        HttpPost httpPost = new HttpPost(url);
        int time = 0;
        do {
            try {
                if (null != params) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, UTF_8));
                }
                httpResponse = new DefaultHttpClient().execute(httpPost);
                final int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                is = httpResponse.getEntity().getContent();
                //Log.e("服务器响应测试", EntityUtils.toString(httpResponse.getEntity()));
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (time < RETRY_TIME);
        return is;
    }


    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @throws AppException
     */
    private static InputStream my_post(AppContext appContext, String url, List<NameValuePair> params) throws AppException {
        InputStream is = null;
        HttpResponse httpResponse = null;
        HttpPost httpPost = new HttpPost(url);
        int time = 0;
        do {
            try {
                try {
                    if (null != params) {
                        httpPost.setEntity(new UrlEncodedFormEntity(params, "gb2312"));
                    }
                    httpResponse = new DefaultHttpClient()
                            .execute(httpPost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                is = httpResponse.getEntity().getContent();

                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            }
        } while (time < RETRY_TIME);
        return is;
    }

    /**
     * 公用post方法
     *
     * @param url
     * @throws AppException
     */
    public static String image_post(String url, List<NameValuePair> params) throws AppException {
        InputStream is = null;
        String result = "";
        int time = 0;
        do {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(
                        CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                //设置图片读取超时时间
                //httpclient.getParams().setParameter("http.socket.timeout", new Integer(30000));
                HttpPost httppost = new HttpPost(url);
                MultipartEntity mpEntity = new MultipartEntity();
                for (int index = 0; index < params.size(); index++) {
                    if (params.get(index).getName().equals("filePath")) {
                        String filePath = new String(params.get(index).getValue());
                        String mimeType = "image/jpeg";
                        String extension = filePath.substring(filePath.lastIndexOf(".") + 1,
                                filePath.length());
                        File file = new File(filePath);
                        if (null != extension && !"".equals(extension)) {
                            if (extension.equals("jpg")) {
                                mimeType = "image/jpeg";
                            } else if (extension.equals("png")) {
                                mimeType = "image/png";
                            } else if (extension.equals("gif")) {
                                mimeType = "image/gif";
                            }
                        }
                        ContentBody cbFile = new FileBody(file, mimeType);
                        mpEntity.addPart("imgFile", cbFile);
                    } else {
                        mpEntity.addPart(params.get(index).getName(), new StringBody(params.get(index).getValue()));
                    }
                }
                httppost.setEntity(mpEntity);
                HttpResponse response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                httpclient.getConnectionManager().shutdown();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            }
        } while (time < RETRY_TIME);

        return result;
    }

    /**
     * post请求URL
     *
     * @param url
     * @throws AppException
     * @throws java.io.IOException
     * @throws
     */
    private static String http_post(AppContext appContext, String url, List<NameValuePair> params) throws AppException, IOException {
        return getStringFromStream(my_post(appContext, url, params));
    }

    /**
     * 获取网络图片
     *
     * @param url
     * @return
     */
    public static Bitmap getNetBitmap(String url) throws AppException {
        //System.out.println("image_url==> "+url);
        HttpClient httpClient = null;
        GetMethod httpGet = null;
        Bitmap bitmap = null;
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, null, null);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                InputStream inStream = httpGet.getResponseBodyAsStream();
                bitmap = BitmapFactory.decodeStream(inStream);
                inStream.close();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return bitmap;
    }


    /**
     * 将输入流转为String
     *
     * @param is
     * @return
     * @throws AppException
     */
    public static String getStringFromStream(InputStream is) throws AppException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
        } catch (IOException e) {
            throw AppException.io(e);
        }
        return builder.toString();
    }
    /**
     * InputStream 转换成JSONObject
     *
     * @param is
     * @return
     * @throws AppException
     */
    public static JSONObject toJSONObj(InputStream is) throws AppException {
        if (is == null)
            return new JSONObject();
        JSONObject jobj = null;
        try {
            String context = ApiClient.getStringFromStream(is);
            if (context.length() != 0) {
                jobj = new JSONObject(context);
            }
            return jobj;
        } catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }
}
