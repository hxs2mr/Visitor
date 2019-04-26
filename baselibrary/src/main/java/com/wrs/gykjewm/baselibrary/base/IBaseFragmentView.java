package com.wrs.gykjewm.baselibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * description:
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午12:46
 * email:  1113799552@qq.com
 * version: v1.0
 */

public interface IBaseFragmentView {

    Activity getmActivity();

    void showProgressDialog(String msg);
    void closeProgressDialog();

    void showActivity(Activity activity, Class<?> cls, Bundle bundle);
    void showActivity(Activity activity, Class<?> cls);
    void showActivity(Activity activity, Intent intent);
    void skipActivity(Activity activity, Class<?> cls, Bundle bundle);
    void skipActivity(Activity activity, Class<?> cls);
    void skipActivity(Activity activity, Intent intent);

}
