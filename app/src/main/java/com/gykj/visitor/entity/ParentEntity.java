package com.gykj.visitor.entity;

import java.io.Serializable;
import java.util.List;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2010:07
 * version: 1.0
 */
public class ParentEntity implements Serializable{


    private static final long serialVersionUID = 708465132024933157L;

    /**
     * parentName : 阿龙妈妈
     * childList : [{"studentId":5417,"gradeName":"冠宇科技","studntName":"阿龙弟弟","className":"技术部"},{"studentId":6581,"gradeName":"冠宇科技","studntName":"阿龙哥哥","className":"测试"}]
     * parentId : 6574
     * parentPhone : 18720918000
     */

    private String parentName;
    private int count;
    private long parentId;
    private String parentPhone;
    private List<ChildEntity> childList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public List<ChildEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<ChildEntity> childList) {
        this.childList = childList;
    }


}
