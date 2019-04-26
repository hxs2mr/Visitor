package com.gykj.visitor.entity;


import java.io.Serializable;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2010:08
 * version: 1.0
 */
public class ChildEntity implements Serializable{


    private static final long serialVersionUID = 2396642870302792716L;
    /**
     * studentId : 5417
     * gradeName : 冠宇科技
     * studntName : 阿龙弟弟
     * className : 技术部
     */

    private int studentId;
    private String gradeName;
    private String studntName;
    private String className;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getStudntName() {
        return studntName;
    }

    public void setStudntName(String studntName) {
        this.studntName = studntName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
