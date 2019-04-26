package com.wrs.gykjewm.baselibrary.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;


import com.wrs.gykjewm.baselibrary.base.BaseActivity;

import java.util.Stack;

/**
 * description: activity任务栈管理
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午12:56
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class ActivityStackManager {

    private static ActivityStackManager instance = null;
    private static Stack<Activity> activityStack;// 栈

    /**
     * 私有构造
     */
    private ActivityStackManager() {
        activityStack = new Stack<Activity>();
    }

    /**
     * 单例实例
     *
     * @return
     */
    public static synchronized ActivityStackManager getManager() {
        if (instance == null) {
            instance = new ActivityStackManager();
        }
        return instance;
    }

    /**
     * 压栈
     * @param activity
     */
    public void push(Activity activity) {
        activityStack.push(activity);
    }

    /**
     * 出栈
     *
     * @return
     */
    public Activity pop() {
        if (activityStack.isEmpty())
            return null;
        return activityStack.pop();
    }

    /**
     * 栈顶
     *
     * @return
     */
    public Activity peek() {
        if (activityStack.isEmpty())
            return null;
        return activityStack.peek();
    }

    /**
     * 用于异地登录或者退出时清除activity
     */
    public void clearActivityToLogin(Class<?> loginActivity) {
        while (!activityStack.isEmpty()) {
            Activity activity = activityStack.pop();
            if (activity.getClass().getSimpleName().equals(loginActivity.getSimpleName())) {
                continue;
            } else {
                remove(activity);
                activity.finish();
            }
        }
    }

    /**
     * 移除
     *
     * @param activity
     */
    public void remove(Activity activity) {
        if (activityStack.size() > 0 && activity == activityStack.peek())
            activityStack.pop();
        else
            activityStack.remove(activity);
    }

    /**
     * 是否存在栈
     *
     * @param activity
     * @return
     */
    public boolean contains(Activity activity) {
        return activityStack.contains(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        while (!activityStack.isEmpty()) {
            activityStack.pop().finish();
        }
    }

    /**
     * 退出应用程序
     *
     * @param context
     */
    public void exitApp(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            //清除通知栏
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
        }
    }
}