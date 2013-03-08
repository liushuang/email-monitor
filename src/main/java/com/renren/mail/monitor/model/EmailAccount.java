package com.renren.mail.monitor.model;

public class EmailAccount {

    // 用户名
    private String userName;

    // 密码
    private String password;

    // 连接host
    private String host;

    public EmailAccount(String userName, String password, String host) {
        this.userName = userName;
        this.password = password;
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
