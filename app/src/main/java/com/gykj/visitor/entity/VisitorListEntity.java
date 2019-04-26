package com.gykj.visitor.entity;

import java.io.Serializable;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/512:40
 * version: 1.0
 */
public class VisitorListEntity implements Serializable {

    private static final long serialVersionUID = 769604980672931490L;


    /**
     * intoSchoolTime : 2018-11-05 07:56:32
     * phone : 18585870152
     * name : 岳雯龙
     * id : 1
     */

    private String intoSchoolTime;
    private String phone;
    private String name;
    private int id;

    public String getIntoSchoolTime() {
        return intoSchoolTime;
    }

    public void setIntoSchoolTime(String intoSchoolTime) {
        this.intoSchoolTime = intoSchoolTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
