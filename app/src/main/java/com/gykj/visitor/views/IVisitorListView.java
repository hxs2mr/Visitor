package com.gykj.visitor.views;

import com.wrs.gykjewm.baselibrary.base.IBaseView;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/513:36
 * version: 1.0
 */
public interface IVisitorListView extends IBaseView {

    void finishRefreshing();

    void finishLoadMore(boolean noMoreData);

    void initRecyclerViewUI();

    void initFirstVisitorData();
}
