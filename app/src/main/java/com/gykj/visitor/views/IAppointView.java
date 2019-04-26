package com.gykj.visitor.views;

import com.wrs.gykjewm.baselibrary.base.IBaseView;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2016:43
 * version: 1.0
 */
public interface IAppointView extends IBaseView {

    void showVisitorInfo();

    void finishActivity();

    void commitData();
}
