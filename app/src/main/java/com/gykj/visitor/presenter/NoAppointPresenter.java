package com.gykj.visitor.presenter;

import com.gykj.visitor.R;
import com.gykj.visitor.entity.TeacherEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.model.data.VisitorApi;
import com.gykj.visitor.ui.activity.LoginActivity;
import com.gykj.visitor.views.INoAppointView;
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
 * date   : 2018/9/2015:05
 * version: 1.0
 */
public class NoAppointPresenter extends BasePresenter<INoAppointView> {

    VisitorApi api;

    private List<TeacherEntity> teacherList = new ArrayList<>();

    @Override
    public void attachView(INoAppointView view) {
        super.attachView(view);
        api = VisitorApi.getApi();
    }

    public void teacherSearch(String patten, final int type){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.teacherSearch(patten)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<List<TeacherEntity>>>() {
                    @Override
                    public void accept(BaseResult<List<TeacherEntity>> listBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if(listBaseResult.isSuccessed()){
                            ToastUtils.showToast(ToastType.SUCCESS,listBaseResult.getMsg());
                            teacherList = listBaseResult.getData();
                            switch (type){
                                case 1:
                                    mBaseView.showNameSpinner(teacherList);
                                    break;
                                case 2:
                                    mBaseView.showPhoneSpinner(teacherList);
                                    break;
                            }
                        }else if(listBaseResult.getCode() == 100){
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,listBaseResult.getMsg());
                        }else {
                            ToastUtils.showToast(ToastType.WARNING,listBaseResult.getMsg());
                        }
                    }
                },getErrorConsumer());
        addDisposable(subscribe);
    }

    public void appointPolling(long teacherId,long visitId,String visitObjectiv,String visitPhone,String visitorName){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.appointPolling(teacherId, visitId, visitObjectiv, visitPhone, visitorName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<NullEntity>>() {
                    @Override
                    public void accept(BaseResult<NullEntity> nullEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if(nullEntityBaseResult.isSuccessed()){
                            ToastUtils.showToast(ToastType.SUCCESS,nullEntityBaseResult.getMsg());
                            mBaseView.finishActivity();
                        }else if(nullEntityBaseResult.getCode() == 100){
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,nullEntityBaseResult.getMsg());
                        }else {
                            ToastUtils.showToast(ToastType.WARNING,nullEntityBaseResult.getMsg());
                        }

                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }

    public List<TeacherEntity> getTeacherList() {
        return teacherList;
    }
}
