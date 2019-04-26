package com.gykj.visitor.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;


import com.gykj.visitor.R;
import com.gykj.visitor.adapter.DeviceAdapter;
import com.gykj.visitor.entity.LoginEntity;
import com.gykj.visitor.views.OnSelectListener;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * description:
 * <p>   选择对应的门设备
 * author: josh.lu
 * created: 20/8/18 上午10:57
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class DeviceDialog extends Dialog implements BaseQuickAdapter.OnItemChildClickListener{

    @BindView(R.id.login_recyclerveiw)
    RecyclerView mLoginRecyclerView;

    private List<LoginEntity.DeviceListBean> mDeviceList = new ArrayList<>();
    private DeviceAdapter adapter;
    private Context mContext;

    private OnSelectListener mOnSelectListener;

    public DeviceDialog(@NonNull Context context) {
        this(context, R.style.alert_dialog);
        this.mContext = context;
    }

    public DeviceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setParams();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL,false);
        mLoginRecyclerView.setLayoutManager(layoutManager);
    }


    private void setParams() {
        setContentView(R.layout.dialog_device_layout);
        ButterKnife.bind(this);
        Window window = getWindow();
        setCanceledOnTouchOutside(false);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    public void setOnSelectListener(OnSelectListener listener){
        this.mOnSelectListener = listener;
    }

    public void setDeviceList(List<LoginEntity.DeviceListBean> deviceList){
        this.mDeviceList = deviceList;
        if(null == adapter){
            adapter = new DeviceAdapter(mDeviceList);
            mLoginRecyclerView.setAdapter(adapter);
        }else {
            adapter.setNewData(mDeviceList);
        }
        adapter.setOnItemChildClickListener(this);
    }


    @OnClick({R.id.login_close_iv,R.id.login_certain_tv,R.id.login_cancle_tv})
    public void OnClisk(View view){
        switch (view.getId()){
            case R.id.login_close_iv:
                dismiss();
                break;
            case R.id.login_certain_tv:
                LoginEntity.DeviceListBean deviceListBean = getDeviceListBean();
                if(null == deviceListBean){
                    ToastUtils.showToast(ToastType.WARNING,mContext.getString(R.string.please_select_device));
                    return;
                }
                mOnSelectListener.select(getDeviceListBean());
                dismiss();
                break;
            case R.id.login_cancle_tv:
                dismiss();
                break;
        }
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()){
            case R.id.tab_layout:
                for(int i = 0;i<mDeviceList.size();i++){
                    if(position == i){
                        mDeviceList.get(i).setClick(true);
                    }else {
                        mDeviceList.get(i).setClick(false);
                    }
                }
                adapter.setNewData(mDeviceList);
                break;
        }
    }


    private LoginEntity.DeviceListBean getDeviceListBean(){
        LoginEntity.DeviceListBean deviceListBean = null;
        for(int i = 0;i<mDeviceList.size();i++){
            if(mDeviceList.get(i).isClick()){
                deviceListBean = mDeviceList.get(i);
                break;
            }
        }
        return deviceListBean;
    }
}
