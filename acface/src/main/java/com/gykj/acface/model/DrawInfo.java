package com.gykj.acface.model;

import android.graphics.Rect;

public class DrawInfo {
    private Rect rect;
    private int sex;
    private int age;
    private int liveness;

    public DrawInfo(Rect rect, int sex, int age, int liveness) {
        this.rect = rect;
        this.sex = sex;
        this.age = age;
        this.liveness = liveness;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLiveness() {
        return liveness;
    }

    public void setLiveness(int liveness) {
        this.liveness = liveness;
    }
}
