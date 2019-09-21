package com.nenu.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_ndadocinfo")
public class TblNdadocinfo implements Serializable {
    private static final long serialVersionUID = -4165844540041677925L;
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * NDA文档ID tbl_NDADocBasicInfo中的ID
     */
    @Column(name = "NDADocID")
    private String ndadocid;

    /**
     * NDA条款pdf 文件 上传到IPFS 上返回的hash
     */
    @Column(name = "NDAHash")
    private String ndahash;

    /**
     * 文档Hash 文档内容本身的Hash，用于在IPFS中检索
     */
    @Column(name = "DocHash")
    private String dochash;

    /**
     * 时间戳 利用文档Hash、时间等生成的Hash
     */
    @Column(name = "TimeStamp")
    private String timestamp;

    /**
     * 文件名
     */
    @Column(name = "FileName")
    private String filename;

    /**
     * 扩展名
     */
    @Column(name = "FileExtension")
    private String fileextension;

    /**
     * 顺序 按照上传时间对文档进行编号排序
     */
    @Column(name = "FileOrder")
    private Integer fileorder;

    /**
     * 上传人
     */
    @Column(name = "UploadUserName")
    private String uploadusername;

    /**
     * 上传时间
     */
    // 出参格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UploadTime")
    private Date uploadtime;

    /**
     * 上传IP
     */
    @Column(name = "UploadIP")
    private String uploadip;

    /**
     * 前一文档ID 此表中的ID
     */
    @Column(name = "PrevID")
    private Integer previd;

    /**
     * 前一文档时间戳
     */
    @Column(name = "PrevTimeStamp")
    private String prevtimestamp;

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
     * 获取NDA文档ID tbl_NDADocBasicInfo中的ID
     *
     * @return NDADocID - NDA文档ID tbl_NDADocBasicInfo中的ID
     */
    public String getNdadocid() {
        return ndadocid;
    }

    /**
     * 设置NDA文档ID tbl_NDADocBasicInfo中的ID
     *
     * @param ndadocid NDA文档ID tbl_NDADocBasicInfo中的ID
     */
    public void setNdadocid(String ndadocid) {
        this.ndadocid = ndadocid;
    }

    /**
     * 获取NDA条款pdf 文件 上传到IPFS 上返回的hash
     *
     * @return NDAHash - NDA条款pdf 文件 上传到IPFS 上返回的hash
     */
    public String getNdahash() {
        return ndahash;
    }

    /**
     * 设置NDA条款pdf 文件 上传到IPFS 上返回的hash
     *
     * @param ndahash NDA条款pdf 文件 上传到IPFS 上返回的hash
     */
    public void setNdahash(String ndahash) {
        this.ndahash = ndahash;
    }

    /**
     * 获取文档Hash 文档内容本身的Hash，用于在IPFS中检索
     *
     * @return DocHash - 文档Hash 文档内容本身的Hash，用于在IPFS中检索
     */
    public String getDochash() {
        return dochash;
    }

    /**
     * 设置文档Hash 文档内容本身的Hash，用于在IPFS中检索
     *
     * @param dochash 文档Hash 文档内容本身的Hash，用于在IPFS中检索
     */
    public void setDochash(String dochash) {
        this.dochash = dochash;
    }

    /**
     * 获取时间戳 利用文档Hash、时间等生成的Hash
     *
     * @return TimeStamp - 时间戳 利用文档Hash、时间等生成的Hash
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳 利用文档Hash、时间等生成的Hash
     *
     * @param timestamp 时间戳 利用文档Hash、时间等生成的Hash
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取文件名
     *
     * @return FileName - 文件名
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 设置文件名
     *
     * @param filename 文件名
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 获取扩展名
     *
     * @return FileExtension - 扩展名
     */
    public String getFileextension() {
        return fileextension;
    }

    /**
     * 设置扩展名
     *
     * @param fileextension 扩展名
     */
    public void setFileextension(String fileextension) {
        this.fileextension = fileextension;
    }

    /**
     * 获取顺序 按照上传时间对文档进行编号排序
     *
     * @return FileOrder - 顺序 按照上传时间对文档进行编号排序
     */
    public Integer getFileorder() {
        return fileorder;
    }

    /**
     * 设置顺序 按照上传时间对文档进行编号排序
     *
     * @param fileorder 顺序 按照上传时间对文档进行编号排序
     */
    public void setFileorder(Integer fileorder) {
        this.fileorder = fileorder;
    }

    /**
     * 获取上传人
     *
     * @return UploadUserName - 上传人
     */
    public String getUploadusername() {
        return uploadusername;
    }

    /**
     * 设置上传人
     *
     * @param uploadusername 上传人
     */
    public void setUploadusername(String uploadusername) {
        this.uploadusername = uploadusername;
    }

    /**
     * 获取上传时间
     *
     * @return UploadTime - 上传时间
     */
    public Date getUploadtime() {
        return uploadtime;
    }

    /**
     * 设置上传时间
     *
     * @param uploadtime 上传时间
     */
    public void setUploadtime(Date uploadtime) {
        this.uploadtime = uploadtime;
    }

    /**
     * 获取上传IP
     *
     * @return UploadIP - 上传IP
     */
    public String getUploadip() {
        return uploadip;
    }

    /**
     * 设置上传IP
     *
     * @param uploadip 上传IP
     */
    public void setUploadip(String uploadip) {
        this.uploadip = uploadip;
    }

    /**
     * 获取前一文档ID 此表中的ID
     *
     * @return PrevID - 前一文档ID 此表中的ID
     */
    public Integer getPrevid() {
        return previd;
    }

    /**
     * 设置前一文档ID 此表中的ID
     *
     * @param previd 前一文档ID 此表中的ID
     */
    public void setPrevid(Integer previd) {
        this.previd = previd;
    }

    /**
     * 获取前一文档时间戳
     *
     * @return PrevTimeStamp - 前一文档时间戳
     */
    public String getPrevtimestamp() {
        return prevtimestamp;
    }

    /**
     * 设置前一文档时间戳
     *
     * @param prevtimestamp 前一文档时间戳
     */
    public void setPrevtimestamp(String prevtimestamp) {
        this.prevtimestamp = prevtimestamp;
    }
}