package com.nenu.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_ndashare")
public class TblNdashare {
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * NDA编号
     */
    @Column(name = "NDAID")
    private String ndaid;

    /**
     * 人员顺序 暂时保留，如果出现一对多的情况可以保证顺序
     */
    @Column(name = "OrderNumber")
    private Integer ordernumber;

    /**
     * 组织名称
     */
    @Column(name = "OrgName")
    private String orgname;

    /**
     * 用户名 tbl_UserInfo中的用户名
     */
    @Column(name = "UserName")
    private String username;

    /**
     * 公钥
     */
    @Column(name = "PubKey")
    private String pubkey;

    /**
     * 私钥
     */
    @Column(name = "PrivateKey")
    private String privatekey;

    /**
     * 创建人
     */
    @Column(name = "CreateUserName")
    private String createusername;

    /**
     * 创建时间
     */
    @Column(name = "CreateTime")
    private Date createtime;

    /**
     * 操作IP
     */
    @Column(name = "OperateIP")
    private String operateip;

    /**
     * 有效 为以后留证据，取消分享后部真正删除本记录，而是做标记
     */
    @Column(name = "Valid")
    private String valid;

    /**
     * 0 待确认 1 同意 2 被拒 3 待对方确认
     */
    @Column(name = "ShareStatus")
    private String sharestatus;

    /**
     * 0 待确认 1 同意 2 已拒绝 3 待对方确认
     */
    @Column(name = "ReceiverStatus")
    private String receiverstatus;

    /**
     * 是否携带文件   1 是  2 不是
     */
    @Column(name = "HaveFile")
    private String havefile;

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
     * 获取NDA编号
     *
     * @return NDAID - NDA编号
     */
    public String getNdaid() {
        return ndaid;
    }

    /**
     * 设置NDA编号
     *
     * @param ndaid NDA编号
     */
    public void setNdaid(String ndaid) {
        this.ndaid = ndaid;
    }

    /**
     * 获取人员顺序 暂时保留，如果出现一对多的情况可以保证顺序
     *
     * @return OrderNumber - 人员顺序 暂时保留，如果出现一对多的情况可以保证顺序
     */
    public Integer getOrdernumber() {
        return ordernumber;
    }

    /**
     * 设置人员顺序 暂时保留，如果出现一对多的情况可以保证顺序
     *
     * @param ordernumber 人员顺序 暂时保留，如果出现一对多的情况可以保证顺序
     */
    public void setOrdernumber(Integer ordernumber) {
        this.ordernumber = ordernumber;
    }

    /**
     * 获取组织名称
     *
     * @return OrgName - 组织名称
     */
    public String getOrgname() {
        return orgname;
    }

    /**
     * 设置组织名称
     *
     * @param orgname 组织名称
     */
    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    /**
     * 获取用户名 tbl_UserInfo中的用户名
     *
     * @return UserName - 用户名 tbl_UserInfo中的用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名 tbl_UserInfo中的用户名
     *
     * @param username 用户名 tbl_UserInfo中的用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取公钥
     *
     * @return PubKey - 公钥
     */
    public String getPubkey() {
        return pubkey;
    }

    /**
     * 设置公钥
     *
     * @param pubkey 公钥
     */
    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    /**
     * 获取私钥
     *
     * @return PrivateKey - 私钥
     */
    public String getPrivatekey() {
        return privatekey;
    }

    /**
     * 设置私钥
     *
     * @param privatekey 私钥
     */
    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    /**
     * 获取创建人
     *
     * @return CreateUserName - 创建人
     */
    public String getCreateusername() {
        return createusername;
    }

    /**
     * 设置创建人
     *
     * @param createusername 创建人
     */
    public void setCreateusername(String createusername) {
        this.createusername = createusername;
    }

    /**
     * 获取创建时间
     *
     * @return CreateTime - 创建时间
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 设置创建时间
     *
     * @param createtime 创建时间
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * 获取操作IP
     *
     * @return OperateIP - 操作IP
     */
    public String getOperateip() {
        return operateip;
    }

    /**
     * 设置操作IP
     *
     * @param operateip 操作IP
     */
    public void setOperateip(String operateip) {
        this.operateip = operateip;
    }

    /**
     * 获取有效 为以后留证据，取消分享后部真正删除本记录，而是做标记
     *
     * @return Valid - 有效 为以后留证据，取消分享后部真正删除本记录，而是做标记
     */
    public String getValid() {
        return valid;
    }

    /**
     * 设置有效 为以后留证据，取消分享后部真正删除本记录，而是做标记
     *
     * @param valid 有效 为以后留证据，取消分享后部真正删除本记录，而是做标记
     */
    public void setValid(String valid) {
        this.valid = valid;
    }

    /**
     * 获取0 待确认 1 同意 2 被拒 3 待对方确认
     *
     * @return ShareStatus - 0 待确认 1 同意 2 被拒 3 待对方确认
     */
    public String getSharestatus() {
        return sharestatus;
    }

    /**
     * 设置0 待确认 1 同意 2 被拒 3 待对方确认
     *
     * @param sharestatus 0 待确认 1 同意 2 被拒 3 待对方确认
     */
    public void setSharestatus(String sharestatus) {
        this.sharestatus = sharestatus;
    }

    /**
     * 获取0 待确认 1 同意 2 已拒绝 3 待对方确认
     *
     * @return ReceiverStatus - 0 待确认 1 同意 2 已拒绝 3 待对方确认
     */
    public String getReceiverstatus() {
        return receiverstatus;
    }

    /**
     * 设置0 待确认 1 同意 2 已拒绝 3 待对方确认
     *
     * @param receiverstatus 0 待确认 1 同意 2 已拒绝 3 待对方确认
     */
    public void setReceiverstatus(String receiverstatus) {
        this.receiverstatus = receiverstatus;
    }

    /**
     * 获取是否携带文件   1 是  2 不是
     *
     * @return HaveFile - 是否携带文件   1 是  2 不是
     */
    public String getHavefile() {
        return havefile;
    }

    /**
     * 设置是否携带文件   1 是  2 不是
     *
     * @param havefile 是否携带文件   1 是  2 不是
     */
    public void setHavefile(String havefile) {
        this.havefile = havefile;
    }
}