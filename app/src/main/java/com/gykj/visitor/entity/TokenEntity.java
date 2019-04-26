package com.gykj.visitor.entity;

/**
 * desc   : 有效Token返回实体类
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/209:34
 * version: 1.0
 */
public class TokenEntity {


    /**
     * expire : 720000
     * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJ1c2VySWRcIjoxLFwiZGV2aWNlSWRcIjoxfSIsImlhdCI6MTU0MjY3NzY0OCwiZXhwIjoxNTQzMzk3NjQ4fQ.aZkSbxYVnwQReaOoDYOxsIh93WwnIwH1oNOn8Eh31uwakyqcA_oRtNnqk4zXhWY7Qoooah92QWpjNdaqAq6G8Q
     */

    private int expire;
    private String token;

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
