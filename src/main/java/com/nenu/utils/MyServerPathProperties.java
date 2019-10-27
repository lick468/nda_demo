package com.nenu.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my.serverfilepath")
public class MyServerPathProperties {
    private String orguppath;
    private String passpath;
    private String downpath;

    public String getOrgUpPath() {
        return orguppath;
    }

    public String getPassPath() {
        return passpath;
    }

    public String getDownPath() {
        return downpath;
    }

    public void setPassPath(String passpath) {
        this.passpath = passpath;
    }

    public void setDownPath(String downpath) {
        this.downpath = downpath;
    }

    public void setOrgUpPath(String orguppath) {
        this.orguppath = orguppath;
    }
}