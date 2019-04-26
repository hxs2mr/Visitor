package com.gykj.visitor.views;

import com.wrs.gykjewm.baselibrary.base.IBaseView;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/2312:22
 * version: 1.0
 */
public interface IFaceAuthView extends IBaseView {

    void initEngine();

    void initCamera();

    boolean checkPermissions(String[] neededPermissions);

    void startRotation();

    void intoActivity();

    void intoScanActivity();

}
