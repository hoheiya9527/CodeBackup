package com.github.tvbox.osc.bean;

import androidx.annotation.NonNull;


import java.util.UUID;

public class CastVideo {

    private final String name;
    private String url;

    public CastVideo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return UUID.randomUUID().toString();
    }

    public String getUri() {
        return url;
    }

    public String getName() {
        return name;
    }
}