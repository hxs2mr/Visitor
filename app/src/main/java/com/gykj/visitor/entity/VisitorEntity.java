package com.gykj.visitor.entity;

import java.io.Serializable;

/**
 * desc   : 老师实体
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2010:17
 * version: 1.0
 */
public class VisitorEntity implements Serializable{


    private static final long serialVersionUID = -2048939458375244689L;
    /**
     * teacherName : 王晓莉
     * visitPhone : 13627260363
     * objectiv :
     * visitDate : 2018-09-20
     * visitId : 5556
     */
    private long visitId;
    private String teacherName;
    private String visitPhone;
    private String objectiv;
    private String visitDate;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getVisitPhone() {
        return visitPhone;
    }

    public void setVisitPhone(String visitPhone) {
        this.visitPhone = visitPhone;
    }

    public String getObjectiv() {
        return objectiv;
    }

    public void setObjectiv(String objectiv) {
        this.objectiv = objectiv;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public long getVisitId() {
        return visitId;
    }

    public void setVisitId(long visitId) {
        this.visitId = visitId;
    }
}
