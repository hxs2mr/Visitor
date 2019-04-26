package com.gykj.visitor.presenter;

import com.gykj.visitor.R;
import com.gykj.visitor.adapter.VisitorAdapter;
import com.gykj.visitor.entity.VisitorListEntity;
import com.gykj.visitor.model.data.VisitorApi;
import com.gykj.visitor.ui.activity.LoginActivity;
import com.gykj.visitor.views.IVisitorListView;
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
 * date   : 2018/11/513:36
 * version: 1.0
 */
public class VisitorListPresenter extends BasePresenter<IVisitorListView> {



    VisitorAdapter adapter;
    VisitorApi api;

    private List<VisitorListEntity> mVisitorList = new ArrayList<>();

    @Override
    public void attachView(IVisitorListView view) {
        super.attachView(view);
        api = VisitorApi.getApi();
        initAdapter();
    }



    public void changeStatus( long id){
        mBaseView.showProgressDialog(mContext.getResources().getString(R.string.loading));
        Disposable subscribe = api.changeStatus(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<NullEntity>>() {
                    @Override
                    public void accept(BaseResult<NullEntity> nullEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if(nullEntityBaseResult.isSuccessed()){
                            ToastUtils.showToast(ToastType.SUCCESS,nullEntityBaseResult.getMsg());
                            mBaseView.initFirstVisitorData();
                        }else if(nullEntityBaseResult.getCode() == 100){
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,nullEntityBaseResult.getMsg());
                        }else {
                            ToastUtils.showToast(ToastType.WARNING,nullEntityBaseResult.getMsg());
                        }
                    }
                });
        addDisposable(subscribe);
    }


    public void noLeavingList( final int offset){
        mBaseView.showProgressDialog(mContext.getResources().getString(R.string.loading));
        Disposable subscribe = api.noLeavingList(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<List<VisitorListEntity>>>() {
                    @Override
                    public void accept(BaseResult<List<VisitorListEntity>> listBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        mBaseView.finishRefreshing();
                        if(listBaseResult.isSuccessed()){
                            if(listBaseResult.getData().size()<10){
                                mBaseView.finishLoadMore(true);
                            }
                            if(offset == 1){
                                mVisitorList = listBaseResult.getData();
                            }else if(listBaseResult.getCode() == 100){
                                ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                                ToastUtils.showToast(ToastType.WARNING,listBaseResult.getMsg());
                            }else {
                                mVisitorList.addAll(listBaseResult.getData());
                            }
                            adapter.setNewData(mVisitorList);
                        }
                    }
                });
        addDisposable(subscribe);
    }


    private void initAdapter(){
        if(null == adapter){
            adapter = new VisitorAdapter(mVisitorList);
        }
    }

    public VisitorAdapter getAdapter() {
        return adapter;
    }


    public List<VisitorListEntity> getmVisitorList() {
        return mVisitorList;
    }



}
