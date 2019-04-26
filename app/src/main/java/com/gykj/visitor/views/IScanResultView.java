package com.gykj.visitor.views;

import com.gykj.visitor.entity.ChildEntity;
import com.wrs.gykjewm.baselibrary.base.IBaseView;

import java.util.List;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/219:38
 * version: 1.0
 */
public interface IScanResultView extends IBaseView {

    void initRecyclerViewUI();

    void finishActivity();

    void commitData();

}
