package com.gykj.visitor.entity;

import java.io.Serializable;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/514:58
 * version: 1.0
 */
public class VisitorCountEntity implements Serializable {


    /**
     * count : 0
     */

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
