package com.nenu.domain;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tbl_userinfo")
public class TblUserinfo {
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
     * 密码 加密的密码
     */
    @Column(name = "Password")
    private String password;

    /**
     * 所属组织
     */
    @Column(name = "OrgName")
    private String orgname;

    /**
     * 姓名
     */
    @Column(name = "Name")
    private String name;

    /**
     * 性别
     */
    @Column(name = "Gender")
    private String gender;

    /**
     * 出生日期
     */
    @Column(name = "Birthday")
    private Date birthday;

    /**
     * 身份证号
     */
    @Column(name = "CertID")
    private String certid;

    /**
     * 家庭所在省
     */
    @Column(name = "HomeProvince")
    private String homeprovince;

    /**
     * 家庭所在市
     */
    @Column(name = "HomeCity")
    private String homecity;

    /**
     * 家庭所在区
     */
    @Column(name = "HomeDistrict")
    private String homedistrict;

    /**
     * 家庭地址
     */
    @Column(name = "HomeAddress")
    private String homeaddress;

    /**
     * 家庭邮编
     */
    @Column(name = "HomePostCode")
    private String homepostcode;

    /**
     * 通讯地址省
     */
    @Column(name = "MailProvince")
    private String mailprovince;

    /**
     * 通讯地址市
     */
    @Column(name = "MailCity")
    private String mailcity;

    /**
     * 通讯地址区
     */
    @Column(name = "MailDistrict")
    private String maildistrict;

    /**
     * 详细通讯地址
     */
    @Column(name = "MailAddress")
    private String mailaddress;

    /**
     * 通讯地址邮编
     */
    @Column(name = "MailPostCode")
    private String mailpostcode;

    /**
     * 固定电话
     */
    @Column(name = "PhoneNo")
    private String phoneno;

    /**
     * 移动电话
     */
    @Column(name = "MobilePhone")
    private String mobilephone;

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
     * 创建IP
     */
    @Column(name = "CreateIP")
    private String createip;

    /**
     * 更新人
     */
    @Column(name = "UpdateUserName")
    private String updateusername;

    /**
     * 更新时间
     */
    @Column(name = "UpdateTime")
    private Date updatetime;

    /**
     * 更新IP
     */
    @Column(name = "UpdateIP")
    private String updateip;

    /**
     * 邮箱
     */
    @Column(name = "Email")
    private String email;

    /**
     * 备用邮箱
     */
    @Column(name = "BackupEmail")
    private String backupemail;

    /**
     * QQ
     */
    @Column(name = "QQ")
    private String qq;

    /**
     * 微信
     */
    @Column(name = "WeChat")
    private String wechat;

    /**
     * Instagram
     */
    @Column(name = "Instagram")
    private String instagram;

    /**
     * 有效标志 用户信息并不真正删除，只是加删除标志
     */
    @Column(name = "Valid")
    private String valid;

    /**
     * 找回密码提示问题
     */
    @Column(name = "Question4Pass")
    private String question4pass;

    /**
     * 问题答案
     */
    @Column(name = "Answer")
    private String answer;

    /**
     * CA信息 用户CA的相关信息
     */
    @Column(name = "CAInfo")
    private String cainfo;

    /**
     * 签名
     */
    @Column(name = "Signature")
    private byte[] signature;

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
     * 获取密码 加密的密码
     *
     * @return Password - 密码 加密的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码 加密的密码
     *
     * @param password 密码 加密的密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取所属组织
     *
     * @return OrgName - 所属组织
     */
    public String getOrgname() {
        return orgname;
    }

    /**
     * 设置所属组织
     *
     * @param orgname 所属组织
     */
    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    /**
     * 获取姓名
     *
     * @return Name - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取性别
     *
     * @return Gender - 性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置性别
     *
     * @param gender 性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取出生日期
     *
     * @return Birthday - 出生日期
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置出生日期
     *
     * @param birthday 出生日期
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取身份证号
     *
     * @return CertID - 身份证号
     */
    public String getCertid() {
        return certid;
    }

    /**
     * 设置身份证号
     *
     * @param certid 身份证号
     */
    public void setCertid(String certid) {
        this.certid = certid;
    }

    /**
     * 获取家庭所在省
     *
     * @return HomeProvince - 家庭所在省
     */
    public String getHomeprovince() {
        return homeprovince;
    }

    /**
     * 设置家庭所在省
     *
     * @param homeprovince 家庭所在省
     */
    public void setHomeprovince(String homeprovince) {
        this.homeprovince = homeprovince;
    }

    /**
     * 获取家庭所在市
     *
     * @return HomeCity - 家庭所在市
     */
    public String getHomecity() {
        return homecity;
    }

    /**
     * 设置家庭所在市
     *
     * @param homecity 家庭所在市
     */
    public void setHomecity(String homecity) {
        this.homecity = homecity;
    }

    /**
     * 获取家庭所在区
     *
     * @return HomeDistrict - 家庭所在区
     */
    public String getHomedistrict() {
        return homedistrict;
    }

    /**
     * 设置家庭所在区
     *
     * @param homedistrict 家庭所在区
     */
    public void setHomedistrict(String homedistrict) {
        this.homedistrict = homedistrict;
    }

    /**
     * 获取家庭地址
     *
     * @return HomeAddress - 家庭地址
     */
    public String getHomeaddress() {
        return homeaddress;
    }

    /**
     * 设置家庭地址
     *
     * @param homeaddress 家庭地址
     */
    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress;
    }

    /**
     * 获取家庭邮编
     *
     * @return HomePostCode - 家庭邮编
     */
    public String getHomepostcode() {
        return homepostcode;
    }

    /**
     * 设置家庭邮编
     *
     * @param homepostcode 家庭邮编
     */
    public void setHomepostcode(String homepostcode) {
        this.homepostcode = homepostcode;
    }

    /**
     * 获取通讯地址省
     *
     * @return MailProvince - 通讯地址省
     */
    public String getMailprovince() {
        return mailprovince;
    }

    /**
     * 设置通讯地址省
     *
     * @param mailprovince 通讯地址省
     */
    public void setMailprovince(String mailprovince) {
        this.mailprovince = mailprovince;
    }

    /**
     * 获取通讯地址市
     *
     * @return MailCity - 通讯地址市
     */
    public String getMailcity() {
        return mailcity;
    }

    /**
     * 设置通讯地址市
     *
     * @param mailcity 通讯地址市
     */
    public void setMailcity(String mailcity) {
        this.mailcity = mailcity;
    }

    /**
     * 获取通讯地址区
     *
     * @return MailDistrict - 通讯地址区
     */
    public String getMaildistrict() {
        return maildistrict;
    }

    /**
     * 设置通讯地址区
     *
     * @param maildistrict 通讯地址区
     */
    public void setMaildistrict(String maildistrict) {
        this.maildistrict = maildistrict;
    }

    /**
     * 获取详细通讯地址
     *
     * @return MailAddress - 详细通讯地址
     */
    public String getMailaddress() {
        return mailaddress;
    }

    /**
     * 设置详细通讯地址
     *
     * @param mailaddress 详细通讯地址
     */
    public void setMailaddress(String mailaddress) {
        this.mailaddress = mailaddress;
    }

    /**
     * 获取通讯地址邮编
     *
     * @return MailPostCode - 通讯地址邮编
     */
    public String getMailpostcode() {
        return mailpostcode;
    }

    /**
     * 设置通讯地址邮编
     *
     * @param mailpostcode 通讯地址邮编
     */
    public void setMailpostcode(String mailpostcode) {
        this.mailpostcode = mailpostcode;
    }

    /**
     * 获取固定电话
     *
     * @return PhoneNo - 固定电话
     */
    public String getPhoneno() {
        return phoneno;
    }

    /**
     * 设置固定电话
     *
     * @param phoneno 固定电话
     */
    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    /**
     * 获取移动电话
     *
     * @return MobilePhone - 移动电话
     */
    public String getMobilephone() {
        return mobilephone;
    }

    /**
     * 设置移动电话
     *
     * @param mobilephone 移动电话
     */
    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
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
     * 获取创建IP
     *
     * @return CreateIP - 创建IP
     */
    public String getCreateip() {
        return createip;
    }

    /**
     * 设置创建IP
     *
     * @param createip 创建IP
     */
    public void setCreateip(String createip) {
        this.createip = createip;
    }

    /**
     * 获取更新人
     *
     * @return UpdateUserName - 更新人
     */
    public String getUpdateusername() {
        return updateusername;
    }

    /**
     * 设置更新人
     *
     * @param updateusername 更新人
     */
    public void setUpdateusername(String updateusername) {
        this.updateusername = updateusername;
    }

    /**
     * 获取更新时间
     *
     * @return UpdateTime - 更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置更新时间
     *
     * @param updatetime 更新时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 获取更新IP
     *
     * @return UpdateIP - 更新IP
     */
    public String getUpdateip() {
        return updateip;
    }

    /**
     * 设置更新IP
     *
     * @param updateip 更新IP
     */
    public void setUpdateip(String updateip) {
        this.updateip = updateip;
    }

    /**
     * 获取邮箱
     *
     * @return Email - 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取备用邮箱
     *
     * @return BackupEmail - 备用邮箱
     */
    public String getBackupemail() {
        return backupemail;
    }

    /**
     * 设置备用邮箱
     *
     * @param backupemail 备用邮箱
     */
    public void setBackupemail(String backupemail) {
        this.backupemail = backupemail;
    }

    /**
     * 获取QQ
     *
     * @return QQ - QQ
     */
    public String getQq() {
        return qq;
    }

    /**
     * 设置QQ
     *
     * @param qq QQ
     */
    public void setQq(String qq) {
        this.qq = qq;
    }

    /**
     * 获取微信
     *
     * @return WeChat - 微信
     */
    public String getWechat() {
        return wechat;
    }

    /**
     * 设置微信
     *
     * @param wechat 微信
     */
    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    /**
     * 获取Instagram
     *
     * @return Instagram - Instagram
     */
    public String getInstagram() {
        return instagram;
    }

    /**
     * 设置Instagram
     *
     * @param instagram Instagram
     */
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    /**
     * 获取有效标志 用户信息并不真正删除，只是加删除标志
     *
     * @return Valid - 有效标志 用户信息并不真正删除，只是加删除标志
     */
    public String getValid() {
        return valid;
    }

    /**
     * 设置有效标志 用户信息并不真正删除，只是加删除标志
     *
     * @param valid 有效标志 用户信息并不真正删除，只是加删除标志
     */
    public void setValid(String valid) {
        this.valid = valid;
    }

    /**
     * 获取找回密码提示问题
     *
     * @return Question4Pass - 找回密码提示问题
     */
    public String getQuestion4pass() {
        return question4pass;
    }

    /**
     * 设置找回密码提示问题
     *
     * @param question4pass 找回密码提示问题
     */
    public void setQuestion4pass(String question4pass) {
        this.question4pass = question4pass;
    }

    /**
     * 获取问题答案
     *
     * @return Answer - 问题答案
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * 设置问题答案
     *
     * @param answer 问题答案
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * 获取CA信息 用户CA的相关信息
     *
     * @return CAInfo - CA信息 用户CA的相关信息
     */
    public String getCainfo() {
        return cainfo;
    }

    /**
     * 设置CA信息 用户CA的相关信息
     *
     * @param cainfo CA信息 用户CA的相关信息
     */
    public void setCainfo(String cainfo) {
        this.cainfo = cainfo;
    }

    /**
     * 获取签名
     *
     * @return Signature - 签名
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * 设置签名
     *
     * @param signature 签名
     */
    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}