package com.gykj.visitor.entity;

import java.io.Serializable;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2010:48
 * version: 1.0
 */
public class TeacherEntity implements Serializable{


    private static final long serialVersionUID = -4325320209033011898L;
    /**
     * teacherName : 张丰境
     * id : 5507
     */

    private String teacherName;
    private String phone;
    private int id;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
