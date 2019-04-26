package com.gykj.visitor.presenter;

import android.util.Log;

import com.guoguang.jni.JniCall;
import com.gykj.visitor.R;
import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.entity.PollEntity;
import com.gykj.visitor.entity.VisitorCountEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.model.data.VisitorApi;
import com.gykj.visitor.ui.activity.LoginActivity;
import com.gykj.visitor.views.IMainView;
import com.lanzhu.baidutts.manager.BaiduTtsManager;
import com.orhanobut.logger.Logger;
import com.wrs.gykjewm.baselibrary.base.BasePresenter;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.NullEntity;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.ActivityStackManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;
import com.wrs.gykjewm.baselibrary.widget.LoadDialogView;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   : 主界面 P 层
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/208:39
 * version: 1.0
 */
public class MainPresenter extends BasePresenter<IMainView> {

    VisitorApi api;

    @Override
    public void attachView(IMainView view) {
        super.attachView(view);
        api = VisitorApi.getApi();
    }
    public void checkIsAppoint(String address, String birthDate, String effectiveDate, String idCard,
                               String nation, String signOrgina, final String visitName, int visitSex, final byte[] photo, final int ret){


        try {
            mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        }catch (Exception e)
            {

            }

        Disposable subscribe = api.checkIsAppoint(address, birthDate, effectiveDate, idCard, nation, signOrgina, visitName, visitSex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<VisitorEntity>>() {
                    @Override
                    public void accept(BaseResult<VisitorEntity> teacherEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        mBaseView.stopRecognize(false);
                        if(teacherEntityBaseResult.getCode() == 2){//已经预约
                            ToastUtils.showToast(ToastType.SUCCESS,teacherEntityBaseResult.getMsg());


                            /**设备一的方法**/
                         //   byte[] buf = new byte[WLTService.imgLength];
                           // if (1 == WLTService.wlt2Bmp(photo, buf)) {    //检查人脸和身份证上面的是否匹配  1:为匹配
                           //     mBaseView.extractFeature(buf,teacherEntityBaseResult.getData(),teacherEntityBaseResult.getCode());
                           // }
                            /**设备二的方法**/
                            if(	1==ret)
                            {
                                mBaseView.extractFeature(photo,teacherEntityBaseResult.getData(),teacherEntityBaseResult.getCode());
                            }


                        }else if(teacherEntityBaseResult.getCode() == 1){//未预约      人脸识别

                            ToastUtils.showToast(ToastType.SUCCESS,teacherEntityBaseResult.getMsg());


                            /**设备一的方法**/
                         /*   byte[] buf = new byte[WLTService.imgLength];
                            if (1 == WLTService.wlt2Bmp(photo, buf)) {   //判断身份证图片是否符合大小   然后调用人脸识别
                                Log.i("HXS","起始执行");
                                mBaseView.extractFeature(buf,teacherEntityBaseResult.getData(),teacherEntityBaseResult.getCode());
                            }*/

                            /**设备二  内嵌的方法**/


                            if(1 == ret)
                            {
                                mBaseView.extractFeature(photo,teacherEntityBaseResult.getData(),teacherEntityBaseResult.getCode());
                            }

                        }else if(teacherEntityBaseResult.getCode() == 3){//离校确认登记

                            ToastUtils.showToast(ToastType.SUCCESS,visitName+"家长已离校");
                            BaiduTtsManager.getManager().speak(visitName+"，家长已离校");
                            mBaseView.setIDCardReaderRecognize(false);


                            /**设备一**/
                           //  mBaseView.startRecognizeIDCard();
                            noLeavingCount();
                        } else if(teacherEntityBaseResult.getCode() == 100){//token过期

                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,teacherEntityBaseResult.getMsg());
                        }else {
                            ToastUtils.showToast(ToastType.WARNING,teacherEntityBaseResult.getMsg());

                            /**设备一**/
                           // mBaseView.startRecognizeIDCard();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        try {

                            /**设备一**/
                             //   mBaseView.startRecognizeIDCard();
                           // ToastUtils.showToast(ToastType.EEEOR, "获取数据异常"+throwable.getMessage());
                            mBaseView.closeProgressDialog();
                            mBaseView.stopRecognize(false);
                        }catch (Exception e)
                        {
                            System.out.println("获取数据异常"+e.getMessage());
                        }
                    }
                });
        addDisposable(subscribe);
    }

    public void getParentInfo(final long parentId){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.getParentInfo(parentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<ParentEntity>>() {
                    @Override
                    public void accept(BaseResult<ParentEntity> parentEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if(parentEntityBaseResult.isSuccessed()){
                            mBaseView.intoScanResultActivity(parentEntityBaseResult.getData());
                        }else if(parentEntityBaseResult.getCode() == 100){
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,parentEntityBaseResult.getMsg());
                        }else if(parentEntityBaseResult.getCode() == 1){
                            ToastUtils.showToast(ToastType.WARNING,parentEntityBaseResult.getMsg());
                            BaiduTtsManager.getManager().speak(parentEntityBaseResult.getMsg());
                        }
                        else if(parentEntityBaseResult.getCode() == 3){
                            parentPolling(parentEntityBaseResult.getData().getParentName(),parentId,"");
                        }else {
                            ToastUtils.showToast(ToastType.WARNING,parentEntityBaseResult.getMsg());
                        }
                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }


    public void parentPolling(final String parentName, long parentId, String visitObjectiv){
        mBaseView.showProgressDialog(mBaseView.getContext().getResources().getString(R.string.loading));
        Disposable subscribe = api.parentPolling(parentId,parentName, visitObjectiv)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<PollEntity>>() {
                    @Override
                    public void accept(BaseResult<PollEntity> nullEntityBaseResult) throws Exception {
                        mBaseView.closeProgressDialog();
                        if (nullEntityBaseResult.isSuccessed()) {
                            noLeavingCount();
                            ToastUtils.showToast(ToastType.SUCCESS, parentName + "家长已离校");
                            BaiduTtsManager.getManager().speak(parentName+"，家长已离校");
                        } else if(nullEntityBaseResult.getCode() == 100){
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,nullEntityBaseResult.getMsg());
                        }else {
                            ToastUtils.showToast(ToastType.WARNING, nullEntityBaseResult.getMsg());
                        }
                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }

    public void noLeavingCount(){
        Disposable subscribe = api.noLeavingCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread ())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<VisitorCountEntity>>() {
                    @Override
                    public void accept(BaseResult<VisitorCountEntity> visitorCountEntityBaseResult) throws Exception {
                        if(visitorCountEntityBaseResult.isSuccessed()){
                            mBaseView.showVisitorCount(visitorCountEntityBaseResult.getData().getCount());
                        }else if(visitorCountEntityBaseResult.getCode() == 100){
                            ActivityStackManager.getManager().clearActivityToLogin(LoginActivity.class);
                            ToastUtils.showToast(ToastType.WARNING,visitorCountEntityBaseResult.getMsg());
                        }
                    }
                },  new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showToast(ToastType.EEEOR, "获取数据异常"+throwable.getMessage());
                    }
                });
        addDisposable(subscribe);
    }
}
