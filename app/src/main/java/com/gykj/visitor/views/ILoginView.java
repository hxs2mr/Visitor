package com.gykj.visitor.views;

import com.gykj.visitor.entity.LoginEntity;
import com.wrs.gykjewm.baselibrary.base.IBaseView;

import java.util.List;

/**
 * desc   : 登录接口 V 层
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/209:21
 * version: 1.0
 */
public interface ILoginView extends IBaseView {

    void showDeviceDialog(List<LoginEntity.DeviceListBean> deviceList);

    void intoMainActivity();

    void login();
}
