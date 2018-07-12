package com.moudle.app.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moudle.app.AppContext;
import com.moudle.app.AppManager;
import com.moudle.app.R;
import com.moudle.app.ui.LoginActivity;
import com.moudle.app.ui.PwdModifyActivity;
import com.moudle.app.ui.SimpleBackActivity;
import com.moudle.app.ui.UserInfoModifyActivity;
import com.moudle.app.ui.WebActivity;
import com.moudle.app.ui.WhiteActivity;
import com.moudle.app.bean.SimpleBackPage;
import com.moudle.app.bean.User;
import com.moudle.app.ui.HelpActivity;

import java.util.regex.Pattern;


/**
 * 应用程序UI工具包：封装UI相关的一些操作
 *
 * @author konbluesky
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

    /**
     * 表情图片匹配
     */
    private static Pattern facePattern = Pattern.compile("\\[{1}([0-9]\\d*)\\]{1}");


    /**
     * 调用系统中的浏览器打开url
     *
     * @param context
     * @param url
     */
    public static void showUrlRedirect(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage(context, "无法浏览此网页", 500);
        }
    }

    /**
     * 打开带Head的内置浏览器
     *
     * @param context
     * @param url
     */
    public static void openModifyBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage(context, "无法浏览此网页", 500);
        }
    }


    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                showUrlRedirect(view.getContext(), url);
                return true;
            }
        };
    }


    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }


    public static void ToastMessage(Context cont, int msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }


    public static void ToastMessage(Context cont, String msg, int time) {
        Toast.makeText(cont, msg, time).show();
    }




    /**
     * 显示加载中的Dialog
     */
    public static void showLoadingDialog(AlertDialog dlg, Context context, int resourceId) {
        Window window = dlg.getWindow();
        window.setContentView(R.layout.loading);
        TextView textView = (TextView) dlg.findViewById(R.id.loading_text);
        textView.setText(context.getText(resourceId));
    }


    /**
     * 修改密码
     *
     * @param context
     */
    public static void showPwdModifyActivity(Context context) {
        Intent intent = new Intent(context, PwdModifyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转至BackActivity
     *
     * @param context
     * @param page
     * @param args
     */
    public static void showSimpleBack(Context context, SimpleBackPage page, Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    /**
     * @param context
     */
    public static void showSimpleBack(Context context, SimpleBackPage page) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    /**
     * @param activity
     * @param page
     * @param args
     * @param requestCode
     */
    public static void showSimpleBack(Activity activity, SimpleBackPage page, Bundle args, int requestCode) {
        Intent intent = new Intent(activity, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param page
     * @param args
     * @param requestCode
     */
    public static void showSimpleBack(Fragment fragment, SimpleBackPage page, Bundle args, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final AppContext ac = (AppContext) activity.getApplication();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    ToastMessage(ac, "缓存清除成功");
                } else {
                    ToastMessage(ac, "缓存清除失败");
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    ac.clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }


    /**
     * 发送App异常崩溃报告
     *
     * @param cont
     * @param crashReport
     */
    public static void sendAppCrashReport(final Context cont, final String crashReport) {
        Log.d("UIhelper", crashReport);
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_error);
        builder.setMessage(R.string.app_error_message);
        builder.setPositiveButton(R.string.submit_report, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 发送异常报告
                Intent i = new Intent(Intent.ACTION_SEND);
                // i.setType("text/plain"); //模拟器
                i.setType("message/rfc822"); // 真机
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"304635843@qq.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "JDM_Android客户端 - 错误报告");
                i.putExtra(Intent.EXTRA_TEXT, crashReport);
                cont.startActivity(Intent.createChooser(i, "发送错误报告"));
                // 退出
                AppManager.getAppManager().AppExit(cont);
            }
        });
        builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 退出
                AppManager.getAppManager().AppExit(cont);
            }
        });
        builder.show();
    }


    /**
     * 退出程序
     *
     * @param cont
     */
    public static void Exit(final Context cont) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_menu_surelogout);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 退出
                AppManager.getAppManager().AppExit(cont);
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    /**
     * 显示登录页面
     *
     * @param context
     */
    public static void showLoginDialog(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }


    /**
     * 敬请期待
     *
     * @param context
     */
    public static void showWhite(Context context) {
        Intent intent = new Intent(context, WhiteActivity.class);
        context.startActivity(intent);
    }


    /**
     * 帮助Activity
     *
     * @param context
     */
    public static void showHelp(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }



    /**
     * 用户信息修改
     *
     * @param context
     */
    public static void showUserModify(Context context, int modifyFlag) {
        Intent intent = new Intent(context, UserInfoModifyActivity.class);
        intent.putExtra(User.USERMODIFY, modifyFlag);
        context.startActivity(intent);
    }

    /**
     * 显示通用浏览器
     * 目前都为百度首页
     *
     * @param context   头部标题
     * @param headTitle
     */
    public static void showCommonWeb(Context context, String headTitle) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("headTitle", headTitle);
        context.startActivity(intent);
    }

    /**
     * 显示沉于底部的DiaLog并返回View供业务逻辑操作 耦合性太高，以后还得进行解耦合
     */
    public static View showBottomDialog(Context context, int layId, Dialog dig) {
        LayoutInflater flater = LayoutInflater.from(context);
        LinearLayout linearLayout = (LinearLayout) flater.inflate(layId, null);
        final int cFullFillWidth = 10000;
        linearLayout.setMinimumWidth(cFullFillWidth);
        // dig = new Dialog(context, R.style.MMTheme_DataSheet);
        Window w = dig.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dig.onWindowAttributesChanged(lp);
        dig.setCanceledOnTouchOutside(true);
        dig.setContentView(linearLayout);
        dig.show();
        return linearLayout;
    }



    /**
     * 显示意见反馈
     *
     * @param context
     */
    public static void showFeedBack(Context context) {
        showSimpleBack(context, SimpleBackPage.FEEDBACK);
    }

    /**
     * 显示设置
     *
     * @param context
     */
    public static void showSetting(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTING);
    }

    /**
     * 显示通知设置
     *
     * @param context
     */
    public static void showSettingNotice(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTING_NOTIFICATION);
    }

}
