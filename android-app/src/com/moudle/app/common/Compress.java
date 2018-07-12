package com.moudle.app.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.moudle.app.AppConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yushi on 16-3-24.
 */
public class Compress {

    public final static String DEFAULT_SAVE_IMAGE_PATH = AppConfig.DEFAULT_SAVE_IMAGE_PATH;


    /**
     * @param oldPath
     * @param bitmapMaxWidth 480
     * @return
     * @throws Exception
     */
    public static String CompressImage(String oldPath, int bitmapMaxWidth) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(oldPath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int reqHeight = 0;
        int reqWidth = bitmapMaxWidth;
        reqHeight = (reqWidth * height) / width;
        // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
        options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth, reqHeight);
//                System.out.println("calculateInSampleSize(options, 480, 800);==="
//                                + calculateInSampleSize(options, 480, 800));
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
        //Log.e("asdasdas", "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);
        Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap, bitmapMaxWidth, reqHeight, false));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        return saveImg(bbb, MD5.get32MD5(timeStamp));
    }

    /**
     * @param b Bitmap
     * @return 图片存储的位置
     * @throws
     */
    public static String saveImg(Bitmap b, String name) throws Exception {

        File mediaFile = new File(DEFAULT_SAVE_IMAGE_PATH + name + ".jpg");
        if (mediaFile.exists()) {
            mediaFile.delete();

        }
        if (!new File(DEFAULT_SAVE_IMAGE_PATH).exists()) {
            new File(DEFAULT_SAVE_IMAGE_PATH).mkdirs();
        }
        mediaFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(mediaFile);
        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        b.recycle();
        b = null;
        System.gc();
        return mediaFile.getPath();
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}

