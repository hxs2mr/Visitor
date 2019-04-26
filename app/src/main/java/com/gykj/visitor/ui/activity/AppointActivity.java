package com.gykj.visitor.ui.activity;

import android.view.View;

import android.widget.TextView;

import com.gykj.visitor.R;
import com.gykj.visitor.entity.IDCardEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.presenter.AppointPresenter;
import com.gykj.visitor.views.IAppointView;
import com.lanzhu.baidutts.manager.BaiduTtsManager;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.common.Constant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc   : 已经预约界面
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2010:04
 * version: 1.0
 */
public class AppointActivity extends BaseActivity implements IAppointView{

    @BindView(R.id.appiont_name_tv) TextView mAppointNameTv;
    @BindView(R.id.appiont_sex_tv) TextView mAppointSexTv;
    @BindView(R.id.appiont_nation_tv) TextView mAppointNationTv;
    @BindView(R.id.appiont_birthday_tv) TextView mAppointBirthdayTv;
    @BindView(R.id.appiont_id_tv) TextView mAppointIdTv;
    @BindView(R.id.appiont_address_tv) TextView mAppointAddressTv;
    @BindView(R.id.appiont_phone_tv) TextView mAppointPhoneTv;

    @BindView(R.id.appiont_teacher_name_tv) TextView mAppointTeacherNameTv;
    @BindView(R.id.appiont_date_tv) TextView mAppointDateTv;
    @BindView(R.id.appiont_objective_tv) TextView mAppointObjectiveTv;

    private IDCardEntity mCardEntity;
    private VisitorEntity mVisitorEntity;

    AppointPresenter presenter;

    @Override
    public int initContentView() {
        return R.layout.activity_appoint;
    }

    @Override
    public void initData() {
        mVisitorEntity  = (VisitorEntity) getIntent().getExtras().getSerializable(Constant.VISITOR);
        mCardEntity = (IDCardEntity) getIntent().getExtras().getSerializable(Constant.ID_CARD_INFO);
        presenter = new AppointPresenter();
        presenter.attachView(this);
    }

    @Override
    public void initUi() {
        showVisitorInfo();
    }


    @OnClick({R.id.appiont_close_iv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.appiont_close_iv:
                commitData();
                break;
        }
    }
    @Override
    public void showVisitorInfo() {
        mAppointNameTv.setText(getString(R.string.appiont_name)+mCardEntity.getName());
        mAppointSexTv.setText(getString(R.string.appiont_sex)+mCardEntity.getSex());
        mAppointNationTv.setText(getString(R.string.appiont_nation)+mCardEntity.getNation());
        mAppointBirthdayTv.setText(getString(R.string.appiont_birthday)+mCardEntity.getBirth());
        mAppointIdTv.setText(getString(R.string.appiont_id)+mCardEntity.getId());
        mAppointAddressTv.setText(mCardEntity.getAddress());
        mAppointPhoneTv.setText(getString(R.string.appiont_phone)+mVisitorEntity.getVisitPhone());
        mAppointTeacherNameTv.setText(getString(R.string.appiont_teacher_name)+mVisitorEntity.getTeacherName());
        mAppointDateTv.setText(getString(R.string.appiont_date)+mVisitorEntity.getVisitDate());
        if(null == mVisitorEntity.getObjectiv()){
            mAppointObjectiveTv.setText("");
        }else {
            mAppointObjectiveTv.setText(getString(R.string.appiont_objective)+mVisitorEntity.getObjectiv());
        }

    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void commitData() {
        presenter.confirmEntry(mVisitorEntity.getVisitId());
    }

    @Override
    protected void onDestroy() {
        if(presenter.isAttached()){
            presenter.cancel();
            presenter.detachView();
        }
        super.onDestroy();
    }
}
