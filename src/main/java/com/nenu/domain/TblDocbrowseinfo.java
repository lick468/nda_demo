package com.nenu.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_docbrowseinfo")
public class TblDocbrowseinfo {
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 文档ID tbl_NDADocInfo表中的ID
     */
    @Column(name = "DocID")
    private String docid;

    /**
     * 文档类型
     */
    @Column(name = "DocType")
    private String doctype;

    /**
     * 浏览者
     */
    @Column(name = "UserName")
    private String username;

    /**
     * 浏览时间
     */
    @Column(name = "BrowseTime")
    private Date browsetime;

    /**
     * 浏览IP
     */
    @Column(name = "BrowseIP")
    private String browseip;

    /**
     * 获取ID
     *
     * @return ID - ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取文档ID tbl_NDADocInfo表中的ID
     *
     * @return DocID - 文档ID tbl_NDADocInfo表中的ID
     */
    public String getDocid() {
        return docid;
    }

    /**
     * 设置文档ID tbl_NDADocInfo表中的ID
     *
     * @param docid 文档ID tbl_NDADocInfo表中的ID
     */
    public void setDocid(String docid) {
        this.docid = docid;
    }

    /**
     * 获取文档类型
     *
     * @return DocType - 文档类型
     */
    public String getDoctype() {
        return doctype;
    }

    /**
     * 设置文档类型
     *
     * @param doctype 文档类型
     */
    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    /**
     * 获取浏览者
     *
     * @return UserName - 浏览者
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置浏览者
     *
     * @param username 浏览者
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取浏览时间
     *
     * @return BrowseTime - 浏览时间
     */
    public Date getBrowsetime() {
        return browsetime;
    }

    /**
     * 设置浏览时间
     *
     * @param browsetime 浏览时间
     */
    public void setBrowsetime(Date browsetime) {
        this.browsetime = browsetime;
    }

    /**
     * 获取浏览IP
     *
     * @return BrowseIP - 浏览IP
     */
    public String getBrowseip() {
        return browseip;
    }

    /**
     * 设置浏览IP
     *
     * @param browseip 浏览IP
     */
    public void setBrowseip(String browseip) {
        this.browseip = browseip;
    }
}