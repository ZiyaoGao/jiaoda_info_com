package com.moudle.app.common;/**
 * Created by x on 16-3-11.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.moudle.app.AppConfig;
import com.moudle.app.AppContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description 上传图片辅助类
 * @Author Li Chao
 * @Date 16-3-11 17:15
 */
public class UpImageHelp {
    public static final int REQUEST_CODE_CAMERA = 0x110;
    public static final int REQUEST_CODE_ALBUM = 0x111;
    public static final int REQUEST_CODE_CROP = 0x112;
    public static final String CAMERA_PHOTO_NAME = "tmpcamera.png";
    public static final String CROP_PHOTO_NAME = "tmpcropped.png";
    public static final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");


    /**
     * 返回拍摄照片路径
     *
     * @param object
     * @param filePath
     * @return
     */
    public static String openCamera(Object object, String filePath, int requestCode) {
        if (!isExitsSdcard()) {
            UIHelper.ToastMessage(AppContext.getInstance(), "SD卡不存在，请插卡", 0);
            return null;
        }
        if (!FileUtils.checkFilePathExists(AppConfig.DEFAULT_SAVE_IMAGE_PATH)) {
            FileUtils.createPath(AppConfig.DEFAULT_SAVE_IMAGE_PATH);
        } else {

        }
        if (StringUtils.isEmpty(filePath)) {
            filePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH + dateFormater.format(new Date()) + CAMERA_PHOTO_NAME;
        }
        Uri uri = Uri.fromFile(new File(filePath));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, requestCode);
        }
        return filePath;
    }

    /**
     * 打开系统相册
     *
     * @param object
     */
    public static void openAlbum(Object object, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                "image/*");
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
