package com.gykj.visitor.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.visitor.R;
import com.gykj.visitor.entity.LoginEntity;

import java.util.List;

/**
 * description: 设备适配器
 * <p>
 * author: josh.lu
 * created: 20/8/18 上午11:12
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class DeviceAdapter extends BaseQuickAdapter<LoginEntity.DeviceListBean,BaseViewHolder> {



    public DeviceAdapter(@Nullable List<LoginEntity.DeviceListBean> data) {
        super(R.layout.layout_device_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LoginEntity.DeviceListBean item) {
        helper.setText(R.id.tab_title_tv,item.getDeviceName());
        if(item.isClick()){
            helper.setImageResource(R.id.tab_check_cb,R.mipmap.icon_circle_selected);
        }else {
            helper.setImageResource(R.id.tab_check_cb,R.mipmap.icon_circle_normal);
        }
        helper.addOnClickListener(R.id.tab_layout);

    }

}
