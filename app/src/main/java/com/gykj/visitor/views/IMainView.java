package com.gykj.visitor.views;

import android.graphics.Bitmap;

import com.guoguang.jni.idcard.IDCardMsg;
import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.wrs.gykjewm.baselibrary.base.IBaseView;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/208:38
 * version: 1.0
 */
public interface IMainView extends IBaseView {

    void startIDCardReader();

    void RequestDevicePermission();

    void startRecognizeIDCard();

    void checkIsAppoint(IDCardInfo info);

    void checkIsAppoint(IDCardMsg info);

    void stopRecognize(boolean recognize);

    void intoScanResultActivity(ParentEntity parentEntity);

    void initVisitorService();

    void showVisitorCount(int count);

    void initFaceEngine();

    void initEngine();

    boolean checkPermissions(String[] neededPermissions);

    void extractFeature( byte[] bitmap,VisitorEntity entity,int code);

    void setIDCardReaderRecognize(boolean recognize);

}
