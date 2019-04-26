package com.gykj.visitor.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gykj.visitor.R;
import com.gykj.visitor.adapter.SpinnerAdapter;
import com.gykj.visitor.entity.IDCardEntity;
import com.gykj.visitor.entity.TeacherEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.presenter.NoAppointPresenter;
import com.gykj.visitor.utils.PhoneUtils;
import com.gykj.visitor.views.INoAppointView;
import com.gykj.visitor.widget.PopSpinner;
import com.lanzhu.baidutts.manager.BaiduTtsManager;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc   : 未预约界面
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/209:06
 * version: 1.0
 */
public class NoAppointActivity extends BaseActivity implements INoAppointView,PopSpinner.OnItemClickListener{

    @BindView(R.id.appiont_name_tv) TextView mAppointNameTv;
    @BindView(R.id.appiont_sex_tv) TextView mAppointSexTv;
    @BindView(R.id.appiont_nation_tv) TextView mAppointNationTv;
    @BindView(R.id.appiont_birthday_tv) TextView mAppointBirthdayTv;
    @BindView(R.id.appiont_id_tv) TextView mAppointIdTv;
    @BindView(R.id.appiont_address_tv) TextView mAppointAddressTv;
    @BindView(R.id.appiont_phone_et) EditText mAppointPhoneEt;

    @BindView(R.id.appiont_teacher_phone_et) EditText mAppointTeacherPhoneEt;
    @BindView(R.id.appiont_teacher_name_et) EditText mAppointTeacherNameEt;
    @BindView(R.id.appiont_objective_et) EditText mAppointObjectiveEt;

    @BindView(R.id.appiont_delete_name_iv) ImageView mAppointDeleteNameIv;
    @BindView(R.id.appiont_delete_phone_iv) ImageView mAppointDeletePhoneIv;


    private IDCardEntity mCardEntity;
    private VisitorEntity mVisitorEntity;
    private PopSpinner spinner;
    private SpinnerAdapter adapter;

    private int SEARCH_TYPE = 0;
    private int teacher_id = -1;

    NoAppointPresenter presenter;

    private boolean is_search = true;

    @Override
    public int initContentView() {
        return R.layout.activity_no_appoint;
    }

    @Override
    public void initData() {
        mVisitorEntity  = (VisitorEntity) getIntent().getExtras().getSerializable(Constant.VISITOR);
        mCardEntity = (IDCardEntity) getIntent().getExtras().getSerializable(Constant.ID_CARD_INFO);
        presenter = new NoAppointPresenter();
        presenter.attachView(this);
    }

    @Override
    public void initUi() {
        initPopSpinner();
        showVisitorInfo();
        mAppointPhoneEt.setVisibility(View.VISIBLE);
        initTeacherNameListener();
        initTeacherPhoneListener();
        initSpinnerListener();
    }

    @Override
    public void showVisitorInfo() {
        mAppointNameTv.setText(getString(R.string.appiont_name)+mCardEntity.getName());
        mAppointSexTv.setText(getString(R.string.appiont_sex)+mCardEntity.getSex());
        mAppointNationTv.setText(getString(R.string.appiont_nation)+mCardEntity.getNation());
        mAppointBirthdayTv.setText(getString(R.string.appiont_birthday)+mCardEntity.getBirth());
        mAppointIdTv.setText(getString(R.string.appiont_id)+mCardEntity.getId());
        mAppointAddressTv.setText(mCardEntity.getAddress());

    }


    @Override
    public void commitVisitData() {
        String phone = mAppointPhoneEt.getText().toString().trim();
        String objective = mAppointObjectiveEt.getText().toString().trim();
        String visitorName = mAppointTeacherNameEt.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            mAppointPhoneEt.setError(getString(R.string.appoint_input_phone));
            return;
        }
        if(!PhoneUtils.isChinaPhone(phone)){
            mAppointPhoneEt.setError(getString(R.string.appoint_incorrect_phone));
            return;
        }
        if(TextUtils.isEmpty(visitorName)){
            mAppointTeacherNameEt.setError(getString(R.string.appoint_input_teacher_name));
            return;
        }
        if(TextUtils.isEmpty(objective)){
            objective = "";
        }
        if(teacher_id <= 0){
            ToastUtils.showToast(ToastType.WARNING,getString(R.string.appoint_select_teacher));
            return;
        }
        presenter.appointPolling(teacher_id,mVisitorEntity.getVisitId(),objective,phone,visitorName);
    }

    @Override
    public void initPopSpinner() {
        spinner = new PopSpinner(this);
        adapter = new SpinnerAdapter(this,presenter.getTeacherList());
        spinner.setAdapter(adapter);
    }

    @Override
    public void showNameSpinner(List<TeacherEntity> list) {
        adapter.setDatas(list);
        spinner.showAsDropDown(mAppointTeacherNameEt);
    }

    @Override
    public void showPhoneSpinner(List<TeacherEntity> list) {
        adapter.setDatas(list);
        spinner.showAsDropDown(mAppointTeacherPhoneEt);
    }

    @Override
    public void initTeacherNameListener() {
        mAppointTeacherNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SEARCH_TYPE = 1;
                if(s.length() >= 1){
                    mAppointDeleteNameIv.setVisibility(View.VISIBLE);
                    if(is_search){
                        presenter.teacherSearch(s.toString(),SEARCH_TYPE);
                    }
                }else {
                    mAppointDeleteNameIv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void initTeacherPhoneListener() {
        mAppointTeacherPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SEARCH_TYPE = 2;
                if(s.length() >= 1){
                    mAppointDeletePhoneIv.setVisibility(View.VISIBLE);
                }else {
                    mAppointDeletePhoneIv.setVisibility(View.INVISIBLE);
                }
                if(s.length()>=3 && is_search){
                    presenter.teacherSearch(s.toString(),SEARCH_TYPE);
                }
            }
        });
    }

    @Override
    public void initSpinnerListener() {
        spinner.setOnItemClickListener(this);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public long getVisitorID() {
        return mVisitorEntity.getVisitId();
    }

    @OnClick({R.id.appiont_cancle_iv,R.id.appiont_sure_iv,R.id.appiont_delete_name_iv,R.id.appiont_delete_phone_iv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.appiont_sure_iv:
                commitVisitData();
                break;
            case R.id.appiont_cancle_iv:
                finish();
                break;
            case R.id.appiont_delete_name_iv:
                mAppointTeacherNameEt.setText("");
                break;
            case R.id.appiont_delete_phone_iv:
                mAppointTeacherPhoneEt.setText("");
                break;

        }
    }


    @Override
    public void onClick(int i) {
        is_search = false;
        TeacherEntity entity = presenter.getTeacherList().get(i);
        teacher_id = entity.getId();
        switch (SEARCH_TYPE){
            case 1:
                mAppointTeacherNameEt.setText(entity.getTeacherName());
                if(null == entity.getPhone() || TextUtils.isEmpty(entity.getPhone())){
                    mAppointTeacherPhoneEt.setText("");
                }else {
                    mAppointTeacherPhoneEt.setText(entity.getPhone());
                }
                is_search = true;
                break;
            case 2:
                mAppointTeacherNameEt.setText(entity.getTeacherName());
                if(null == entity.getPhone() || TextUtils.isEmpty(entity.getPhone())){
                    mAppointTeacherPhoneEt.setText("");
                }else {
                    mAppointTeacherPhoneEt.setText(entity.getPhone());
                }
                is_search = true;
                break;
        }
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
