package com.gykj.visitor.views;

import com.gykj.visitor.entity.TeacherEntity;
import com.wrs.gykjewm.baselibrary.base.IBaseView;

import java.util.List;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2013:29
 * version: 1.0
 */
public interface INoAppointView extends IBaseView {

    void showVisitorInfo();

    void commitVisitData();

    void initPopSpinner();

    void showNameSpinner(List<TeacherEntity> list);

    void showPhoneSpinner(List<TeacherEntity> list);

    void initTeacherNameListener();

    void initTeacherPhoneListener();

    void initSpinnerListener();

    void finishActivity();

    long getVisitorID();
}
