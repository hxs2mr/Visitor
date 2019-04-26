package com.wrs.gykjewm.baselibrary.base;

import android.app.Application;
import android.os.Environment;

import com.arcsoft.face.FaceEngine;
import com.gykj.acface.common.Constants;
import com.lanzhu.autolayout.config.AutoLayoutConifg;
import com.lanzhu.baidutts.manager.BaiduTtsManager;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.wrs.gykjewm.baselibrary.utils.ThreadManager;


import java.io.File;



/**
 * description:
 * <p>
 * author: josh.lu
 * created: 11/6/18 上午11:00
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class BaseApplication extends Application {

    private static BaseApplication instance = null;


    private final String FACE_DB_PATH = Environment.getExternalStorageDirectory() + File.separator+"faceDb/";


    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AutoLayoutConifg.getInstance().init(instance);
        //CrashReport.initCrashReport(getApplicationContext(), "319d02343d", false);
        Beta.autoCheckUpgrade = false;
        Bugly.init(getApplicationContext(), "08859aac9f", false);
        if (LeakCanary.isInAnalyzerProcess(instance)) {
            return;
        }
        //LeakCanary.install(instance);
        //初始化人脸引擎
        initFaceEngine();
        //realm初始化
        File file = new File(FACE_DB_PATH);
        if(!file.exists()){
            file.mkdir();
        }
        //初始化语音
        BaiduTtsManager.getManager().initBaiduTts(this);
    }

    private void initFaceEngine() {
        ThreadManager.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                FaceEngine faceEngine = new FaceEngine();
                final int activeCode = faceEngine.active(instance, Constants.APP_ID, Constants.SDK_KEY);

            }
        });
    }

    public String getFACE_DB_PATH() {
        return FACE_DB_PATH;
    }

}
