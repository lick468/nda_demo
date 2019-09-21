package com.nenu.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_ndaitemtpl")
public class TblNdaitemtpl implements Serializable {
    private static final long serialVersionUID = 83369091803180387L;
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
     * 创建时IP
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
     * 更新时IP
     */
    @Column(name = "UpdateIP")
    private String updateip;

    /**
     * NDA标题
     */
    @Column(name = "NDATitle")
    private String ndatitle;

    /**
     * NDA条款
     */
    @Column(name = "NDAItem")
    private String ndaitem;

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
     * 获取创建时IP
     *
     * @return CreateIP - 创建时IP
     */
    public String getCreateip() {
        return createip;
    }

    /**
     * 设置创建时IP
     *
     * @param createip 创建时IP
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
     * 获取更新时IP
     *
     * @return UpdateIP - 更新时IP
     */
    public String getUpdateip() {
        return updateip;
    }

    /**
     * 设置更新时IP
     *
     * @param updateip 更新时IP
     */
    public void setUpdateip(String updateip) {
        this.updateip = updateip;
    }

    /**
     * 获取NDA标题
     *
     * @return NDATitle - NDA标题
     */
    public String getNdatitle() {
        return ndatitle;
    }

    /**
     * 设置NDA标题
     *
     * @param ndatitle NDA标题
     */
    public void setNdatitle(String ndatitle) {
        this.ndatitle = ndatitle;
    }

    /**
     * 获取NDA条款
     *
     * @return NDAItem - NDA条款
     */
    public String getNdaitem() {
        return ndaitem;
    }

    /**
     * 设置NDA条款
     *
     * @param ndaitem NDA条款
     */
    public void setNdaitem(String ndaitem) {
        this.ndaitem = ndaitem;
    }
}