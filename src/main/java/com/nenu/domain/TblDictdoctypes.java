package com.nenu.domain;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "tbl_dictdoctypes")
public class TblDictdoctypes implements Serializable {
    private static final long serialVersionUID = -8436505294446097189L;
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 文档类型 1.NDA协议文档，2.其它主文档，3.文档附件，4.其它文档
     */
    @Column(name = "DocType")
    private String doctype;

    /**
     * 文档对应表
     */
    @Column(name = "DocTable")
    private String doctable;

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
     * 获取文档类型 1.NDA协议文档，2.其它主文档，3.文档附件，4.其它文档
     *
     * @return DocType - 文档类型 1.NDA协议文档，2.其它主文档，3.文档附件，4.其它文档
     */
    public String getDoctype() {
        return doctype;
    }

    /**
     * 设置文档类型 1.NDA协议文档，2.其它主文档，3.文档附件，4.其它文档
     *
     * @param doctype 文档类型 1.NDA协议文档，2.其它主文档，3.文档附件，4.其它文档
     */
    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    /**
     * 获取文档对应表
     *
     * @return DocTable - 文档对应表
     */
    public String getDoctable() {
        return doctable;
    }

    /**
     * 设置文档对应表
     *
     * @param doctable 文档对应表
     */
    public void setDoctable(String doctable) {
        this.doctable = doctable;
    }
}