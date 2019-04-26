package com.wrs.gykjewm.baselibrary.domain;

/**
 * description:
 * <p>
 * author: josh.lu
 * created: 17/7/18 下午2:58
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class BaseResult<T> {

    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccessed(){
        return code == 0?true:false;
    }

}
