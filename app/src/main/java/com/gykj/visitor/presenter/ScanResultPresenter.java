package com.gykj.visitor.presenter;

import com.gykj.visitor.R;
import com.gykj.visitor.adapter.ChildAdapter;
import com.gykj.visitor.entity.ChildEntity;
import com.gykj.visitor.entity.PollEntity;
import com.gykj.visitor.model.data.VisitorApi;
import com.gykj.visitor.ui.activity.LoginActivity;
import com.gykj.visitor.views.IScanResultView;
import com.wrs.gykjewm.baselibrary.base.BasePresenter;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.NullEntity;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.ActivityStackManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/219:45
 * version: 1.0
 */
public class ScanResultPresenter extends BasePresenter<IScanResultView> {

    VisitorApi api;
    ChildAdapter adapter;

    private List<ChildEntity> childList = new ArrayList<>();


    @Override
    public void attachView(IScanResultView view) {
        super.attachView(view);
        api = VisitorApi.getApi();
        initAdapter();
    }

    private void initAdapter() {
        if(null == adapter){
            adapter = new ChildAdapter(mBaseView.getContext(),childList);
        }else {
            adapter.setNewData(childList);
        }
    }


    public void parentPolling(long parentId, String parentName, String visitObjectiv, final String imgUrl){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.parentPolling(parentId, parentName, visitObjectiv)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<PollEntity>>() {
                    @Override
                    public void accept(BaseResult<PollEntity> nullEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if (nullEntityBaseResult.isSuccessed()) {
                            uplodeImg(nullEntityBaseResult.getData().getId(),imgUrl);
                        } else if (nullEntityBaseResult.getCode() == 100) {
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING, nullEntityBaseResult.getMsg());
                        } else {
                            ToastUtils.showToast(ToastType.WARNING, nullEntityBaseResult.getMsg());
                        }
                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }

    public void uplodeImg(long id, String imgUrl){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.uplodeImg(id, imgUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<NullEntity>>() {
                    @Override
                    public void accept(BaseResult<NullEntity> nullEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if (nullEntityBaseResult.isSuccessed()) {
                            ToastUtils.showToast(ToastType.SUCCESS, nullEntityBaseResult.getMsg());
                            mBaseView.finishActivity();
                        } else if (nullEntityBaseResult.getCode() == 100) {
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING, nullEntityBaseResult.getMsg());
                        } else {
                            ToastUtils.showToast(ToastType.WARNING, nullEntityBaseResult.getMsg());
                        }
                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }

    public ChildAdapter getAdapter() {
        return adapter;
    }

}
