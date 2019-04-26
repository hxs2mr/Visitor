package com.wrs.gykjewm.baselibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wrs.gykjewm.baselibrary.R;


import butterknife.ButterKnife;

/**
 * description:fragment 基类
 * <p>
 * author: josh.lu
 * created: 13/6/18 上午1:42
 * email:  1113799552@qq.com
 * version: v1.0
 */
public abstract class BaseFragment extends Fragment implements IBaseFragmentView{

    public Activity mActivity;
    private View mContainerView;
    private MaterialDialog dialog;
    private MaterialDialog.Builder builder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mContainerView){
            mContainerView = inflater.inflate(initView(),container,false);
            ButterKnife.bind(this,mContainerView);
        }
        return mContainerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
    }

    public abstract int initView();

    protected void initData(){}

    protected void initListener() {
    }



    public Activity getmActivity() {
        return mActivity;
    }

    @Override
    public void showProgressDialog(String msg) {
        if(null == builder){
            builder = new MaterialDialog.Builder(mActivity)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .canceledOnTouchOutside(false);
        }
        dialog = builder.content(msg).show();

    }

    @Override
    public void closeProgressDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void showActivity(Activity activity, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        intent.putExtras(bundle);
        startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void showActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void showActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void skipActivity(Activity activity, Class<?> cls, Bundle bundle) {
        showActivity(activity, cls, bundle);
        activity.finish();
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void skipActivity(Activity activity, Class<?> cls) {
        showActivity(activity, cls);
        activity.finish();
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void skipActivity(Activity activity, Intent intent) {
        showActivity(activity, intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

}
