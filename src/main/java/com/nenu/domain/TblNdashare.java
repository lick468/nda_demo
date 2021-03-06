package com.nenu.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.service.IUserService;
import lombok.Getter;
import lombok.Setter;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.Resource;
import javax.persistence.*;

@Getter
@Setter
@Table(name = "tbl_ndashare")
public class TblNdashare implements Serializable {

    private static final long serialVersionUID = -6071827215904943510L;
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
     * NDA标题
     */
    @Column(name = "NDATitle")
    private String ndatitle;

    /**
     * 人员顺序 暂时保留，如果出现一对多的情况可以保证顺序
     */
    @Column(name = "OrderNumber")
    private Integer ordernumber = 1;

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
    //@Column(name = "PubKey")
    @Transient
    private String pubkey;

    /**
     * 私钥
     */
    //@Column(name = "PrivateKey")
    @Transient
    private String privatekey;

    /**
     * 创建人
     */
    @Column(name = "CreateUserName")
    private String createusername;

    /**
     * 创建时间
     */
    // 出参格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
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
     *  分享者
     *      0 交易请求 1 活动交易 2 我方中止 3 对方拒绝 4 等待确认 5 对方修改 6无协议活动交易 7甲方撤回
     *  接受者
            0 交易请求 1 活动交易 2 对方中止 3 我方拒绝 4 对方修改 5等待确认 6无协议活动交易  7甲方撤回
     */
    @Column(name = "ShareStatus")
    private String sharestatus = "0";

    /**
     *
     */
    @Column(name = "ReceiverStatus")
    private String receiverstatus = "0";

    /**
     * 是否携带文件   1 是  2 不是
     */
    @Column(name = "HaveFile")
    private String havefile;

    /**
     * 文件路径
     */
    @Column(name = "FilePath")
    private String filepath;

    /**
     * 交易创建人提交的分享人未查看的文件数
     */
    @Column(name = "CreateUserUploadCount")
    private Integer createUserUploadCount = 0;

    /**
     * 交易分享人提交的交易创建人未查看的文件数
     */
    @Column(name = "ShareUserUploadCount")
    private Integer shareUserUploadCount = 0;
    
    @Transient
    private TblUserinfo initiatorUser;
    @Transient
    private TblUserinfo partnerUser;

}