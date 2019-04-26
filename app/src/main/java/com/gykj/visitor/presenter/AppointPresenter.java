package com.gykj.visitor.presenter;

import com.gykj.visitor.R;
import com.gykj.visitor.model.data.VisitorApi;
import com.gykj.visitor.ui.activity.LoginActivity;
import com.gykj.visitor.views.IAppointView;
import com.wrs.gykjewm.baselibrary.base.BasePresenter;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.NullEntity;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.ActivityStackManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   : 预约界面 P 层
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/12/189:40
 * version: 1.0
 */
public class AppointPresenter extends BasePresenter<IAppointView> {

    VisitorApi api;

    @Override
    public void attachView(IAppointView view) {
        super.attachView(view);
        api = VisitorApi.getApi();
    }


    public void confirmEntry(long id){
        mBaseView.showProgressDialog(mBaseView.getContext().getString(R.string.loading));
        Disposable subscribe = api.confirmEntry(id)
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
                },getErrorConsumer());
        addDisposable(subscribe);
    }
}
