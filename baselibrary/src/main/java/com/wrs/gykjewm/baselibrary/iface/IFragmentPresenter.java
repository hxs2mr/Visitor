package com.wrs.gykjewm.baselibrary.iface;


import com.wrs.gykjewm.baselibrary.base.IBaseFragmentView;

/**
 * description:
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午12:46
 * email:  1113799552@qq.com
 * version: v1.0
 */

public interface IFragmentPresenter<V extends IBaseFragmentView> {

    void attachView(V view);

    void detachView();


}
