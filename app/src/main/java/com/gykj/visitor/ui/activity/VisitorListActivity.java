package com.gykj.visitor.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.visitor.R;
import com.gykj.visitor.presenter.VisitorListPresenter;
import com.gykj.visitor.views.IVisitorListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.manager.VisitorManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc   : 访客列表界面
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/59:49
 * version: 1.0
 */
public class VisitorListActivity extends BaseActivity implements IVisitorListView,OnLoadMoreListener,OnRefreshListener,BaseQuickAdapter.OnItemChildClickListener{

    @BindView(R.id.visitor_refresh_layout)
    SmartRefreshLayout mVisitorRefreshLayout;

    @BindView(R.id.visitor_recyclerView)
    RecyclerView mVisitorRecyclerView;


    VisitorListPresenter presenter;

    private int mOffset = 1;


    @Override
    public int initContentView() {
        return R.layout.activity_visitor_list;
    }

    @Override
    public void initData() {
        presenter = new VisitorListPresenter();
        presenter.attachView(this);
    }

    @Override
    public void initUi() {
        mVisitorRefreshLayout.setOnRefreshListener(this);
        mVisitorRefreshLayout.setOnLoadMoreListener(this);
        presenter.getAdapter().setOnItemChildClickListener(this);
        initRecyclerViewUI();
        presenter.noLeavingList(mOffset);
    }


    @OnClick({R.id.visitor_back_iv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.visitor_back_iv:
                finish();
                break;
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mOffset += 1;
        presenter.noLeavingList(mOffset);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        initFirstVisitorData();
    }

    @Override
    public void finishRefreshing() {
        mVisitorRefreshLayout.finishRefresh();
    }

    @Override
    public void finishLoadMore(boolean noMoreData) {
        mVisitorRefreshLayout.finishLoadMore(0,true,noMoreData);
    }

    @Override
    public void initRecyclerViewUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL,false);
        mVisitorRecyclerView.setLayoutManager(layoutManager);
        mVisitorRecyclerView.setAdapter(presenter.getAdapter());
    }

    @Override
    public void initFirstVisitorData() {
        mOffset = 1;
        presenter.noLeavingList(mOffset);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter.isAttached()){
            presenter.cancel();
            presenter.detachView();
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()){
            case R.id.item_visitor_operate_tv:
                presenter.changeStatus(presenter.getmVisitorList().get(position).getId());
                break;
        }
    }
}
