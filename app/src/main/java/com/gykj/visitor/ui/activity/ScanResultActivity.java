package com.gykj.visitor.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.gykj.visitor.R;

import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.presenter.ScanResultPresenter;
import com.gykj.visitor.views.IScanResultView;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.base.BaseApplication;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;
import com.wrs.gykjewm.baselibrary.utils.oss.PictureUpload;
import com.wrs.gykjewm.baselibrary.utils.oss.UploadFileListener;


import java.security.Signature;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * desc   : 扫码结果界面
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/219:17
 * version: 1.0
 */
public class ScanResultActivity extends BaseActivity implements IScanResultView {

    @BindView(R.id.result_name_tv)
    TextView mResultNameTv;

    @BindView(R.id.result_phone_tv)
    TextView mResultPhoneTv;

    @BindView(R.id.result_recyclerview)
    RecyclerView mResultRecyclerView;

    @BindView(R.id.rsult_objective_et)
    EditText mResultObjectiveEt;

    @BindView(R.id.result_avater_iv)
    CircleImageView mResultAvaterIv;

    private ParentEntity mParentEntity;

    ScanResultPresenter presenter;


    @Override
    public int initContentView() {
        return R.layout.activity_scan_result;
    }

    @Override
    public void initData() {
        mParentEntity = (ParentEntity) getIntent().getExtras().getSerializable(Constant.PARENT_INFO);
        presenter = new ScanResultPresenter();
        presenter.attachView(this);
        initRecyclerViewUI();
    }

    @Override
    public void initUi() {
        mResultNameTv.setText(getString(R.string.result_parent_name)+mParentEntity.getParentName());
        mResultPhoneTv.setText(getString(R.string.result_phone)+mParentEntity.getParentPhone());
        presenter.getAdapter().setNewData(mParentEntity.getChildList());
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.icon_default_user)
                .error(R.mipmap.icon_default_user)
                .signature(new ObjectKey(UUID.randomUUID().toString()))  // 重点在这行
                .centerCrop();
        Glide.with(this)
                .load(BaseApplication.getInstance().getFACE_DB_PATH()+"user.png")
                .apply(options)
                .into(mResultAvaterIv);
    }


    @OnClick({R.id.result_cancle_iv,R.id.result_sure_iv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.result_cancle_iv:
                finish();
                break;
            case R.id.result_sure_iv:
                commitData();
                break;
        }
    }

    @Override
    public void initRecyclerViewUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL,false);
        mResultRecyclerView.setLayoutManager(layoutManager);
        mResultRecyclerView.setAdapter(presenter.getAdapter());
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void commitData() {
        final String objective = mResultObjectiveEt.getText().toString().trim();
        showProgressDialog(getString(R.string.loading));
        PictureUpload.getInstance().addTask(this, Constant.OSS_PATH, BaseApplication.getInstance().getFACE_DB_PATH() + "user.png", new UploadFileListener() {
            @Override
            public void onUploadSuccess(final String aliSuccessFilePath) {
                Log.d("lanzhu",aliSuccessFilePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        presenter.parentPolling(mParentEntity.getParentId(),mParentEntity.getParentName(),TextUtils.isEmpty(objective)?"":objective,aliSuccessFilePath);
                    }
                });
            }

            @Override
            public void onUploadFailure(String localFailureFilePath) {
                Log.d("lanzhu_f",localFailureFilePath);
                ToastUtils.showToast(ToastType.WARNING,"头像上传失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                    }
                });
            }
        });
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
