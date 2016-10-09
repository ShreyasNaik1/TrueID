package com.trueid.trueid.model;

import java.io.Serializable;

/**
 * Created by junxiang92 on 8/10/16.
 */
public class Account implements Serializable {
    private long id;
    private String icon, name, domain, username, password;

    public Account() {
    }

    public Account(String icon, String name, String domain, String username, String password) {
        this.icon = icon;
        this.name = name;
        this.domain = domain;
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
