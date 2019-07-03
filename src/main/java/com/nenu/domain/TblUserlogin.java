package com.nenu.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_userlogin")
public class TblUserlogin {
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    @Column(name = "UserName")
    private String username;

    /**
     * 登录时间
     */
    @Column(name = "LoginTime")
    private Date logintime;

    /**
     * 登录IP
     */
    @Column(name = "LoginIP")
    private String loginip;

    /**
     * 登出时间
     */
    @Column(name = "LogoutTime")
    private Date logouttime;

    /**
     * 登出动作 登出动作包括点击退出，超时突出，关闭网页退出等
     */
    @Column(name = "LogoutAction")
    private String logoutaction;

    /**
     * 获取ID
     *
     * @return ID - ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return UserName - 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取登录时间
     *
     * @return LoginTime - 登录时间
     */
    public Date getLogintime() {
        return logintime;
    }

    /**
     * 设置登录时间
     *
     * @param logintime 登录时间
     */
    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }

    /**
     * 获取登录IP
     *
     * @return LoginIP - 登录IP
     */
    public String getLoginip() {
        return loginip;
    }

    /**
     * 设置登录IP
     *
     * @param loginip 登录IP
     */
    public void setLoginip(String loginip) {
        this.loginip = loginip;
    }

    /**
     * 获取登出时间
     *
     * @return LogoutTime - 登出时间
     */
    public Date getLogouttime() {
        return logouttime;
    }

    /**
     * 设置登出时间
     *
     * @param logouttime 登出时间
     */
    public void setLogouttime(Date logouttime) {
        this.logouttime = logouttime;
    }

    /**
     * 获取登出动作 登出动作包括点击退出，超时突出，关闭网页退出等
     *
     * @return LogoutAction - 登出动作 登出动作包括点击退出，超时突出，关闭网页退出等
     */
    public String getLogoutaction() {
        return logoutaction;
    }

    /**
     * 设置登出动作 登出动作包括点击退出，超时突出，关闭网页退出等
     *
     * @param logoutaction 登出动作 登出动作包括点击退出，超时突出，关闭网页退出等
     */
    public void setLogoutaction(String logoutaction) {
        this.logoutaction = logoutaction;
    }
}