package com.wrs.gykjewm.baselibrary.base;

import android.content.Context;
import android.content.res.Resources;


import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.iface.IPresenter;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class BasePresenter<T extends IBaseView> implements IPresenter<T> {

    protected Context mContext;
    protected Resources mRes;
    protected CompositeDisposable compositeDisposable;
    protected T mBaseView;

    @Override
    public void attachView(T view) {
        this.mBaseView = view;
        this.mContext = view.getContext();
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

    @Override
    public void cancel() {
        if (this.compositeDisposable != null) {
            this.compositeDisposable.clear();
        }
    }

    @Override
    public void addDisposable(Disposable disposable) {
        if (this.compositeDisposable == null) {
            this.compositeDisposable = new CompositeDisposable();
        }
        this.compositeDisposable.add(disposable);
    }


    protected Consumer getErrorConsumer(){
        Consumer errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtils.showToast(ToastType.EEEOR, "获取数据异常"+throwable.getMessage());
                mBaseView.closeProgressDialog();
            }
        };
        return errorConsumer;
    }



}
