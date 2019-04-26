package com.gykj.visitor.presenter;

import com.gykj.visitor.R;
import com.gykj.visitor.entity.LoginEntity;
import com.gykj.visitor.entity.TokenEntity;
import com.gykj.visitor.model.data.VisitorApi;
import com.gykj.visitor.views.ILoginView;
import com.wrs.gykjewm.baselibrary.base.BasePresenter;
import com.wrs.gykjewm.baselibrary.domain.Account;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.VisitorManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   : 登录 P 层
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/209:21
 * version: 1.0
 */
public class LoginPresenter extends BasePresenter<ILoginView> {

    VisitorApi api;

    @Override
    public void attachView(ILoginView view) {
        super.attachView(view);
        api = VisitorApi.getApi();
    }

    public void login(final String account, final String pass){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.login(account, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginEntity>() {
                    @Override
                    public void accept(LoginEntity loginEntity) throws Exception {
                        mBaseView.closeProgressDialog();
                        if(loginEntity.getCode() == 0){
                            Account a = new Account();
                            a.setAccount(account);
                            a.setPass(pass);
                            VisitorManager.getManager().saveAccount(a);
                            VisitorManager.getManager().saveToken(loginEntity.getData().getToken());
                            VisitorManager.getManager().saveSchoolName(String.valueOf(loginEntity.getData().getSchoolName()));
                            VisitorManager.getManager().saveSchoolId(String.valueOf(loginEntity.getData().getSchoolId()));
                            if(null !=  VisitorManager.getManager().getDeviceId()){
                                getToken( VisitorManager.getManager().getDeviceId());
                            }else {
                                mBaseView.showDeviceDialog(loginEntity.getDeviceList());
                            }

                        }else {
                            ToastUtils.showToast(ToastType.WARNING,loginEntity.getMsg());
                        }
                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }


    //登陆成功之后获取token
    public void getToken(final String deviceId){
        mBaseView.showProgressDialog(mContext.getString(R.string.loading));
        Disposable subscribe = api.getToken(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<TokenEntity>>() {
                    @Override
                    public void accept(BaseResult<TokenEntity> tokenEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if(null != tokenEntityBaseResult && tokenEntityBaseResult.isSuccessed()){
                            VisitorManager.getManager().saveToken(tokenEntityBaseResult.getData().getToken());
                            VisitorManager.getManager().saveDeviceId(deviceId);
                            mBaseView.intoMainActivity();
                        }else {
                            ToastUtils.showToast(ToastType.WARNING, tokenEntityBaseResult.getMsg());
                        }

                    }
                }, getErrorConsumer());
        addDisposable(subscribe);

    }
}
