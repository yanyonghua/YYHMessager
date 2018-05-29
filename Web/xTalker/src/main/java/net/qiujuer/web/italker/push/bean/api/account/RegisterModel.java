package net.qiujuer.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;

/**
 * 注册模块
 */
public class RegisterModel {
    @Expose
    private String accout;
    @Expose
    private String password;
    @Expose
    private String name;

    public String getAccout() {
        return accout;
    }

    public void setAccout(String accout) {
        this.accout = accout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
