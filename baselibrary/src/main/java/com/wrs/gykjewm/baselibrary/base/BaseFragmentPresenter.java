package com.wrs.gykjewm.baselibrary.base;

import android.content.Context;
import android.content.res.Resources;

import com.wrs.gykjewm.baselibrary.iface.IFragmentPresenter;


/**
 * description:
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午12:46
 * email:  1113799552@qq.com
 * version: v1.0
 */

public class BaseFragmentPresenter<V extends IBaseFragmentView> implements IFragmentPresenter<V> {
    protected Context mContext;
    protected Resources mRes;
    protected V mBaseView;
    private String mFunCode;

    @Override
    public void attachView(V view) {
        this.mBaseView = view;
        this.mContext = view.getmActivity();
        this.mRes = mContext.getResources();
    }

    @Override
    public void detachView() {
        if(null != mBaseView)
            mBaseView = null;
    }


    public boolean isAttached(){
        return mBaseView != null ? true : false;
    }



}
