package com.gykj.visitor.entity;

import java.util.List;

/**
 * desc   : 登录返回请求实体类
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/209:02
 * version: 1.0
 */
public class LoginEntity {


    /**
     * msg : 登录成功
     * code : 0
     * data : {"expire":120,"schoolId":62,"userId":1,"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTQyNjgwNjA4LCJleHAiOjE1NDI2ODA3Mjh9.CghnPEYsRhM0rDUa-Au4CTT3ZH9GGs9XM3l6DWnmI0MoaisC_JEICaYImdJ1eFxh-ZswfBv9Iieko3kzH0I9mg"}
     * deviceList : [{"id":1,"deviceName":"学校大门","deviceSerial":"8759847854258745698","schoolId":62,"createTime":"2018-11-06 06:41:45"}]
     */

    private String msg;
    private int code;
    private DataBean data;
    private List<DeviceListBean> deviceList;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public List<DeviceListBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceListBean> deviceList) {
        this.deviceList = deviceList;
    }

    public static class DataBean {
        /**
         * expire : 120
         * schoolId : 62
         * userId : 1
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTQyNjgwNjA4LCJleHAiOjE1NDI2ODA3Mjh9.CghnPEYsRhM0rDUa-Au4CTT3ZH9GGs9XM3l6DWnmI0MoaisC_JEICaYImdJ1eFxh-ZswfBv9Iieko3kzH0I9mg
         */

        private int expire;
        private int schoolId;
        private int userId;
        private String token;
        private String schoolName;

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }
    }

    public static class DeviceListBean {
        /**
         * id : 1
         * deviceName : 学校大门
         * deviceSerial : 8759847854258745698
         * schoolId : 62
         * createTime : 2018-11-06 06:41:45
         */

        private int id;
        private String deviceName;
        private String deviceSerial;
        private int schoolId;
        private String createTime;
        private boolean isClick;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceSerial() {
            return deviceSerial;
        }

        public void setDeviceSerial(String deviceSerial) {
            this.deviceSerial = deviceSerial;
        }

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isClick() {
            return isClick;
        }

        public void setClick(boolean click) {
            isClick = click;
        }
    }
}
