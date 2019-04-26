package com.wrs.gykjewm.baselibrary.utils;

import android.content.Context;
import android.widget.Toast;


import com.wrs.gykjewm.baselibrary.base.BaseApplication;
import com.wrs.gykjewm.baselibrary.domain.ToastType;

import es.dmoral.toasty.Toasty;

/**
 * description:吐司工具类
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午1:47
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class ToastUtils {
    /**
     * short duration
     * the default value
     */
    public static final int TIME_SHORT = Toast.LENGTH_SHORT;
    /**
     * long duration
     */
    public static final int TIME_LONG = Toast.LENGTH_LONG;

    private static Toast sToast;
    private static Context sContext = BaseApplication.getInstance();

    /**
     * show toast
     *
     * @param resId content value's resource id
     */
    public static void showToast(ToastType type, int resId) {
        showToast(type,sContext.getResources().getString(resId));
    }

    /**
     * show toast
     *
     * @param text content value's string
     */
    public static void showToast(ToastType type,String text) {
        showToast(type,text, TIME_SHORT);
    }

    /**
     * show toast
     *
     * @param resId    content value's resource id
     * @param duration how long the toast should be show
     */
    public static void showToast(ToastType type,int resId, int duration) {
        showToast(type,sContext.getResources().getString(resId), duration);
    }

    /**
     * show toast
     *
     * @param text     content value's string
     * @param duration how long the toast should be show
     */
    public static void showToast(ToastType type ,String text, int duration) {
        switch (type){
            case SUCCESS:
                sToast = Toasty.success(sContext,text);
                break;
            case WARNING:
                sToast = Toasty.warning(sContext,text);
                break;
            case EEEOR:
                sToast = Toasty.error(sContext,text);
                break;
        }
        sToast.show();
    }


}
