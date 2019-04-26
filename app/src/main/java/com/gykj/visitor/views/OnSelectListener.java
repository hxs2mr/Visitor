package com.gykj.visitor.views;

import com.gykj.visitor.entity.LoginEntity;

/**
 * desc   : 设备选择回调接口
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/2010:27
 * version: 1.0
 */
public interface OnSelectListener {

    void select(LoginEntity.DeviceListBean device);
}
