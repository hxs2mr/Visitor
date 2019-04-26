package com.wrs.gykjewm.baselibrary.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/**
 *  *
 * created: 11/6/18 下午2:15
 * email:  1113799552@qq.com
 * version: v1.0
 */
public interface IBaseView {
    void showLoading(int visibility);
    void showLoadingError(int errorType);
    Context getContext();

    void showProgressDialog(String msg);

    void closeProgressDialog();

    void onRefresh(boolean bRefresh);

    void showActivity(Activity activity, Class<?> cls, Bundle bundle);
    void showActivity(Activity activity, Class<?> cls);
    void showActivity(Activity activity, Intent intent);
    void skipActivity(Activity activity, Class<?> cls, Bundle bundle);
    void skipActivity(Activity activity, Class<?> cls);
    void skipActivity(Activity activity, Intent intent);
    void showActivityForResult(Intent intent, int requestCode);

}
