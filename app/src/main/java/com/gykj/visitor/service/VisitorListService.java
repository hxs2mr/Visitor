package com.gykj.visitor.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gykj.visitor.entity.VisitorCountEntity;
import com.gykj.visitor.model.data.VisitorApi;
import com.tencent.bugly.beta.Beta;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.VisitorManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   : 定时更新访客人数服务
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/512:50
 * version: 1.0
 */
public class VisitorListService extends Service {

    protected CompositeDisposable compositeDisposable;
    private static final int MESSAGE_VISITOR = 101;

    private Timer mTimer;
    private TimerTask  mTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessageDelayed(MESSAGE_VISITOR,5000*60);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_VISITOR:
                    noLeavingCount();
                    Log.d("lanzhu","主动检测更新");
                    Beta.checkUpgrade();
                    break;
            }
        }
    };


    VisitorApi api;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        api = VisitorApi.getApi();
        mTimer = new Timer();
        //5分钟定时向服务器请求一次
        mTimer.schedule(mTask,5000*60);
    }

    public void noLeavingCount(){
        Disposable subscribe = api.noLeavingCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<VisitorCountEntity>>() {
                    @Override
                    public void accept(BaseResult<VisitorCountEntity> visitorCountEntityBaseResult) throws Exception {
                        if(visitorCountEntityBaseResult.isSuccessed()){
                            Intent intent = new Intent();
                            intent.setAction(Constant.ACTION_GET_VISITOR_LIST);
                            intent.putExtra(Constant.VISITOR_NUMBER,String.valueOf(visitorCountEntityBaseResult.getData().getCount()));
                            sendBroadcast(intent);
                        }
                    }
                }, getErrorConsumer());
        addDisposable(subscribe);
    }

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
            }
        };
        return errorConsumer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mTimer){
            mTimer.cancel();
        }
        if(null != compositeDisposable){
            compositeDisposable.clear();
        }
    }
}
