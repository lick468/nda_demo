package com.nenu.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_ndabasicinfo")
public class TblNdabasicinfo implements Serializable {
    private static final long serialVersionUID = -2797579602567812643L;
    /**
     * ID 同一个文档的不同历史版本公用此ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 标题
     */
    @Column(name = "Title")
    private String title;

    /**
     * 摘要
     */
    @Column(name = "AbstractContext")
    private String abstractcontext;

    /**
     * 关键词
     */
    @Column(name = "KeyWords")
    private String keywords;

    /**
     * 文件名 本文档所属NDA
     */
    @Column(name = "FileName")
    private String filename;

    /**
     * 扩展名
     */
    @Column(name = "FileExtension")
    private String fileextension;

    /**
     * NDA文件Hash
     */
    @Column(name = "FileHash")
    private String filehash;

    /**
     * 时间戳
     */
    @Column(name = "TimeStamp")
    private String timestamp;

    /**
     * 文档类型 tbl_DictDocTypes中的文档类型ID
     */
    @Column(name = "DocTypeID")
    private String doctypeid;

    /**
     * 文档顺序 此文档在NDA所有文档中按照上传时间的排序，避免每次显示顺序变化
     */
    @Column(name = "DocOrder")
    private Integer docorder;

    /**
     * 发起人 创建人在tbl_User中的UserName
     */
    @Column(name = "InitiatorUserName")
    private String initiatorusername;

    /**
     * 发起时间
     */
    @Column(name = "InitiatorTime")
    private Date initiatortime;

    /**
     * 发起IP
     */
    @Column(name = "InitiatorIP")
    private String initiatorip;

    /**
     * 最近更新人
     */
    @Column(name = "UpdateUserName")
    private String updateusername;

    /**
     * 最近更新时间 最后一次更新时间
     */
    @Column(name = "UpdateTime")
    private Date updatetime;

    /**
     * 最新文档序号 本NDA最新文档序号，每增加一个新文档，本字段加1
     */
    @Column(name = "LastDocNo")
    private Integer lastdocno;

    /**
     * 发送人 公钥
     */
    @Column(name = "SenderPubKey")
    private String senderpubkey;

    /**
     * 发送人 私钥
     */
    @Column(name = "SenderPrivateKey")
    private String senderprivatekey;

    /**
     * 备注
     */
    private String memo;

    /**
     * 乐观锁 为了解决并发冲突增加的字段
     */
    @Column(name = "Revision")
    private String revision;

    /**
     * 接收人 公钥
     */
    @Column(name = "ReceiverPubKey")
    private String receiverpubkey;

    /**
     * 接收人 私钥
     */
    @Column(name = "ReceiverPrivateKey")
    private String receiverprivatekey;

    /**
     * 发送人对NDA条款的签名
     */
    @Column(name = "SenderSign")
    private String sendersign;

    /**
     * 接收人对NDA条款的签名
     */
    @Column(name = "ReceiverSign")
    private String receiversign;

    /**
     * NDA条款 NDA条款文本
     */
    @Column(name = "NDAItems")
    private String ndaitems;

    /**
     * 获取ID 同一个文档的不同历史版本公用此ID
     *
     * @return ID - ID 同一个文档的不同历史版本公用此ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID 同一个文档的不同历史版本公用此ID
     *
     * @param id ID 同一个文档的不同历史版本公用此ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取标题
     *
     * @return Title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取摘要
     *
     * @return AbstractContext - 摘要
     */
    public String getAbstractcontext() {
        return abstractcontext;
    }

    /**
     * 设置摘要
     *
     * @param abstractcontext 摘要
     */
    public void setAbstractcontext(String abstractcontext) {
        this.abstractcontext = abstractcontext;
    }

    /**
     * 获取关键词
     *
     * @return KeyWords - 关键词
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * 设置关键词
     *
     * @param keywords 关键词
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * 获取文件名 本文档所属NDA
     *
     * @return FileName - 文件名 本文档所属NDA
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 设置文件名 本文档所属NDA
     *
     * @param filename 文件名 本文档所属NDA
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
     * 获取NDA文件Hash
     *
     * @return FileHash - NDA文件Hash
     */
    public String getFilehash() {
        return filehash;
    }

    /**
     * 设置NDA文件Hash
     *
     * @param filehash NDA文件Hash
     */
    public void setFilehash(String filehash) {
        this.filehash = filehash;
    }

    /**
     * 获取时间戳
     *
     * @return TimeStamp - 时间戳
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     *
     * @param timestamp 时间戳
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取文档类型 tbl_DictDocTypes中的文档类型ID
     *
     * @return DocTypeID - 文档类型 tbl_DictDocTypes中的文档类型ID
     */
    public String getDoctypeid() {
        return doctypeid;
    }

    /**
     * 设置文档类型 tbl_DictDocTypes中的文档类型ID
     *
     * @param doctypeid 文档类型 tbl_DictDocTypes中的文档类型ID
     */
    public void setDoctypeid(String doctypeid) {
        this.doctypeid = doctypeid;
    }

    /**
     * 获取文档顺序 此文档在NDA所有文档中按照上传时间的排序，避免每次显示顺序变化
     *
     * @return DocOrder - 文档顺序 此文档在NDA所有文档中按照上传时间的排序，避免每次显示顺序变化
     */
    public Integer getDocorder() {
        return docorder;
    }

    /**
     * 设置文档顺序 此文档在NDA所有文档中按照上传时间的排序，避免每次显示顺序变化
     *
     * @param docorder 文档顺序 此文档在NDA所有文档中按照上传时间的排序，避免每次显示顺序变化
     */
    public void setDocorder(Integer docorder) {
        this.docorder = docorder;
    }

    /**
     * 获取发起人 创建人在tbl_User中的UserName
     *
     * @return InitiatorUserName - 发起人 创建人在tbl_User中的UserName
     */
    public String getInitiatorusername() {
        return initiatorusername;
    }

    /**
     * 设置发起人 创建人在tbl_User中的UserName
     *
     * @param initiatorusername 发起人 创建人在tbl_User中的UserName
     */
    public void setInitiatorusername(String initiatorusername) {
        this.initiatorusername = initiatorusername;
    }

    /**
     * 获取发起时间
     *
     * @return InitiatorTime - 发起时间
     */
    public Date getInitiatortime() {
        return initiatortime;
    }

    /**
     * 设置发起时间
     *
     * @param initiatortime 发起时间
     */
    public void setInitiatortime(Date initiatortime) {
        this.initiatortime = initiatortime;
    }

    /**
     * 获取发起IP
     *
     * @return InitiatorIP - 发起IP
     */
    public String getInitiatorip() {
        return initiatorip;
    }

    /**
     * 设置发起IP
     *
     * @param initiatorip 发起IP
     */
    public void setInitiatorip(String initiatorip) {
        this.initiatorip = initiatorip;
    }

    /**
     * 获取最近更新人
     *
     * @return UpdateUserName - 最近更新人
     */
    public String getUpdateusername() {
        return updateusername;
    }

    /**
     * 设置最近更新人
     *
     * @param updateusername 最近更新人
     */
    public void setUpdateusername(String updateusername) {
        this.updateusername = updateusername;
    }

    /**
     * 获取最近更新时间 最后一次更新时间
     *
     * @return UpdateTime - 最近更新时间 最后一次更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置最近更新时间 最后一次更新时间
     *
     * @param updatetime 最近更新时间 最后一次更新时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 获取最新文档序号 本NDA最新文档序号，每增加一个新文档，本字段加1
     *
     * @return LastDocNo - 最新文档序号 本NDA最新文档序号，每增加一个新文档，本字段加1
     */
    public Integer getLastdocno() {
        return lastdocno;
    }

    /**
     * 设置最新文档序号 本NDA最新文档序号，每增加一个新文档，本字段加1
     *
     * @param lastdocno 最新文档序号 本NDA最新文档序号，每增加一个新文档，本字段加1
     */
    public void setLastdocno(Integer lastdocno) {
        this.lastdocno = lastdocno;
    }

    /**
     * 获取发送人 公钥
     *
     * @return SenderPubKey - 发送人 公钥
     */
    public String getSenderpubkey() {
        return senderpubkey;
    }

    /**
     * 设置发送人 公钥
     *
     * @param senderpubkey 发送人 公钥
     */
    public void setSenderpubkey(String senderpubkey) {
        this.senderpubkey = senderpubkey;
    }

    /**
     * 获取发送人 私钥
     *
     * @return SenderPrivateKey - 发送人 私钥
     */
    public String getSenderprivatekey() {
        return senderprivatekey;
    }

    /**
     * 设置发送人 私钥
     *
     * @param senderprivatekey 发送人 私钥
     */
    public void setSenderprivatekey(String senderprivatekey) {
        this.senderprivatekey = senderprivatekey;
    }

    /**
     * 获取备注
     *
     * @return memo - 备注
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置备注
     *
     * @param memo 备注
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 获取乐观锁 为了解决并发冲突增加的字段
     *
     * @return Revision - 乐观锁 为了解决并发冲突增加的字段
     */
    public String getRevision() {
        return revision;
    }

    /**
     * 设置乐观锁 为了解决并发冲突增加的字段
     *
     * @param revision 乐观锁 为了解决并发冲突增加的字段
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * 获取接收人 公钥
     *
     * @return ReceiverPubKey - 接收人 公钥
     */
    public String getReceiverpubkey() {
        return receiverpubkey;
    }

    /**
     * 设置接收人 公钥
     *
     * @param receiverpubkey 接收人 公钥
     */
    public void setReceiverpubkey(String receiverpubkey) {
        this.receiverpubkey = receiverpubkey;
    }

    /**
     * 获取接收人 私钥
     *
     * @return ReceiverPrivateKey - 接收人 私钥
     */
    public String getReceiverprivatekey() {
        return receiverprivatekey;
    }

    /**
     * 设置接收人 私钥
     *
     * @param receiverprivatekey 接收人 私钥
     */
    public void setReceiverprivatekey(String receiverprivatekey) {
        this.receiverprivatekey = receiverprivatekey;
    }

    /**
     * 获取发送人对NDA条款的签名
     *
     * @return SenderSign - 发送人对NDA条款的签名
     */
    public String getSendersign() {
        return sendersign;
    }

    /**
     * 设置发送人对NDA条款的签名
     *
     * @param sendersign 发送人对NDA条款的签名
     */
    public void setSendersign(String sendersign) {
        this.sendersign = sendersign;
    }

    /**
     * 获取接收人对NDA条款的签名
     *
     * @return ReceiverSign - 接收人对NDA条款的签名
     */
    public String getReceiversign() {
        return receiversign;
    }

    /**
     * 设置接收人对NDA条款的签名
     *
     * @param receiversign 接收人对NDA条款的签名
     */
    public void setReceiversign(String receiversign) {
        this.receiversign = receiversign;
    }

    /**
     * 获取NDA条款 NDA条款文本
     *
     * @return NDAItems - NDA条款 NDA条款文本
     */
    public String getNdaitems() {
        return ndaitems;
    }

    /**
     * 设置NDA条款 NDA条款文本
     *
     * @param ndaitems NDA条款 NDA条款文本
     */
    public void setNdaitems(String ndaitems) {
        this.ndaitems = ndaitems;
    }
}