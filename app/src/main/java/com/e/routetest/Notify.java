package com.e.routetest;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notify {
    public Notify(int type, String title, String body) {
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @PrimaryKey(autoGenerate = true)
    int num=0;
    int type;
    String title;
    String body;
}
