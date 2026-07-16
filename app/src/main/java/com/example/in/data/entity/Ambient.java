package com.example.in.data.entity;

public class Ambient {
    private int resId;
    private String title;
    private String resUrl;

    public Ambient(int resId, String title, String resUrl) {
        this.resId = resId;
        this.title = title;
        this.resUrl = resUrl;
    }

    public int getResId() {
        return resId;
    }

    public String getTitle() {
        return title;
    }

    public String getResUrl() {
        return resUrl;
    }
}
