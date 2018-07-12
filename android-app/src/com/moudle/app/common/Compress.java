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
        // ���ڴ��д���bitmap����������������Ŵ�С������
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
     * @return ͼƬ�洢��λ��
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
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
            options -= 10;// ÿ�ζ�����10
            baos.reset();// ����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
        return bitmap;
    }

}

