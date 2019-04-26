package com.gykj.visitor.entity;

import java.io.Serializable;

/**
 * desc   :用户身份证实体
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/209:02
 * version: 1.0
 */
public class IDCardEntity implements Serializable {


    private static final long serialVersionUID = -9116472250249300311L;


    private String name;
    private String sex;
    private String nation;
    private String birth;
    private String address;
    private String id;
    private byte[] photo;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

}
