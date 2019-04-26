package com.wrs.gykjewm.baselibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.wrs.gykjewm.baselibrary.base.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/12/315:47
 * version: 1.0
 */
public class ImageUtils {


    /**
     * 重NV21 里面获取截取图片信息
     *
     * @param nv21
     * @param width
     * @param height
     * @param rect
     * @return
     */
    public static Bitmap getBitmapFromNv21(byte[] nv21, int width, int height, Rect rect) {
        //显示采集到的特征图片
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();

        yuv.compressToJpeg(rect, 80, ops);
        final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);

        try {
            ops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 保存获取bitmap到文件中
     * @param bitmap
     * @param bitName
     * @throws IOException
     */
    public static void saveBitmap(Bitmap bitmap,String bitName)  {
        File file = new File(BaseApplication.getInstance().getFACE_DB_PATH() +bitName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

