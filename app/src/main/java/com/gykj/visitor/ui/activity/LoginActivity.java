package com.gykj.visitor.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gykj.visitor.R;
import com.gykj.visitor.entity.LoginEntity;
import com.gykj.visitor.presenter.LoginPresenter;
import com.gykj.visitor.ui.dialog.DeviceDialog;
import com.gykj.visitor.views.ILoginView;
import com.gykj.visitor.views.OnSelectListener;
import com.tencent.bugly.beta.Beta;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.VisitorManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc   : 访客登录界面
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/208:56
 * version: 1.0
 */
public class LoginActivity extends BaseActivity implements ILoginView,OnSelectListener {

    @BindView(R.id.login_account_et)
    EditText mLoginAccountEt;

    @BindView(R.id.login_pass_et)
    EditText mLoginPassEt;

    LoginPresenter presenter;

    DeviceDialog deviceDialog;

    @Override
    public int initContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        presenter = new LoginPresenter();
        presenter.attachView(this);
        Beta.checkUpgrade();//主动拉去 Bugly上的APP更新
    }
    @Override
    public void initUi() {
        deviceDialog = new DeviceDialog(this);
        deviceDialog.setOnSelectListener(this);
        if(null != VisitorManager.getManager().getAccount()){
            mLoginAccountEt.setText(VisitorManager.getManager().getAccount().getAccount());
            mLoginPassEt.setText(VisitorManager.getManager().getAccount().getPass());
        }
    }

    @OnClick({R.id.login_login_tv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.login_login_tv:
                login();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(null != VisitorManager.getManager().getAccount()){
            mLoginAccountEt.setText(VisitorManager.getManager().getAccount().getAccount());
            mLoginPassEt.setText(VisitorManager.getManager().getAccount().getPass());
        }
    }

    @Override
    public void showDeviceDialog(List<LoginEntity.DeviceListBean> deviceList) {
        deviceDialog.setDeviceList(deviceList);
        deviceDialog.show();
    }


    //跳转访客系统主页
    @Override
    public void intoMainActivity() {    
        showActivity(this,MainActivity.class);
    }

    @Override
    public void login() {
        String account = mLoginAccountEt.getText().toString().trim();
        String pass = mLoginPassEt.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            ToastUtils.showToast(ToastType.WARNING,getString(R.string.please_input_account));
            return;
        }
        if(TextUtils.isEmpty(pass)){
            ToastUtils.showToast(ToastType.WARNING,getString(R.string.please_input_pass));
            return;
        }
        presenter.login(account,pass);
    }

    @Override
    public void select(LoginEntity.DeviceListBean device) {
        //根据已选择的门设备Id 获取改设备的token
        VisitorManager.getManager().saveDeviceId(String.valueOf(device.getId()));
        VisitorManager.getManager().saveDeviceName(device.getDeviceName());
        presenter.getToken(String.valueOf(device.getId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter.isAttached()){
            presenter.cancel();
            presenter.detachView();
        }
    }

}
