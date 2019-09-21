package com.nenu.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_orgnization")
public class TblOrgnization {
    /**
     * ID 组织ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织名称
     */
    @Column(name = "OrgName")
    private String orgname;

    /**
     * 组织法人
     */
    @Column(name = "OrgLeader")
    private String orgleader;

    /**
     * 省
     */
    @Column(name = "Province")
    private String province;

    /**
     * 市
     */
    @Column(name = "City")
    private String city;

    /**
     * 区
     */
    @Column(name = "District")
    private String district;

    /**
     * 地址
     */
    @Column(name = "Address")
    private String address;

    /**
     * 邮政编码
     */
    @Column(name = "PostCode")
    private String postcode;

    /**
     * 联系电话
     */
    @Column(name = "Telephone")
    private String telephone;

    /**
     * 邮箱
     */
    @Column(name = "Email")
    private String email;

    /**
     * 组织类型 组织在平台中的类型：管理，用户
     */
    @Column(name = "OrgType")
    private String orgtype;

    /**
     * 密码
     */
    @Column(name = "Password")
    private String password;

    /**
     * 创建时间
     */
    @Column(name = "CreateTime")
    private Date createtime;

    /**
     * 修改时间
     */
    @Column(name = "UpdateTime")
    private Date updatetime;

    /**
     * 获取ID 组织ID
     *
     * @return ID - ID 组织ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置ID 组织ID
     *
     * @param id ID 组织ID
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取组织法人
     *
     * @return OrgLeader - 组织法人
     */
    public String getOrgleader() {
        return orgleader;
    }

    /**
     * 设置组织法人
     *
     * @param orgleader 组织法人
     */
    public void setOrgleader(String orgleader) {
        this.orgleader = orgleader;
    }

    /**
     * 获取省
     *
     * @return Province - 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省
     *
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取市
     *
     * @return City - 市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置市
     *
     * @param city 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取区
     *
     * @return District - 区
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 设置区
     *
     * @param district 区
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取地址
     *
     * @return Address - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取邮政编码
     *
     * @return PostCode - 邮政编码
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * 设置邮政编码
     *
     * @param postcode 邮政编码
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * 获取联系电话
     *
     * @return Telephone - 联系电话
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * 设置联系电话
     *
     * @param telephone 联系电话
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
     * 获取组织类型 组织在平台中的类型：管理，用户
     *
     * @return OrgType - 组织类型 组织在平台中的类型：管理，用户
     */
    public String getOrgtype() {
        return orgtype;
    }

    /**
     * 设置组织类型 组织在平台中的类型：管理，用户
     *
     * @param orgtype 组织类型 组织在平台中的类型：管理，用户
     */
    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    /**
     * 获取密码
     *
     * @return Password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
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
     * 获取修改时间
     *
     * @return UpdateTime - 修改时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置修改时间
     *
     * @param updatetime 修改时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}