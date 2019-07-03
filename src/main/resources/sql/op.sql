/*
Navicat MySQL Data Transfer

Source Server         : diamond
Source Server Version : 50556
Source Host           : localhost:3308
Source Database       : op

Target Server Type    : MYSQL
Target Server Version : 50556
File Encoding         : 65001

Date: 2019-06-27 20:29:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_dictdoctypes
-- ----------------------------
DROP TABLE IF EXISTS `tbl_dictdoctypes`;
CREATE TABLE `tbl_dictdoctypes` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `DocType` varchar(32) DEFAULT NULL COMMENT '文档类型 1.NDA协议文档，2.其它主文档，3.文档附件，4.其它文档',
  `DocTable` varchar(32) DEFAULT NULL COMMENT '文档对应表',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tbl_DictDocTypes 文档类型数据字典，这里的类型不是文档对应的应用程序类型，而是指该文档是NDA中的主文档还是附件';

-- ----------------------------
-- Records of tbl_dictdoctypes
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_docbrowseinfo
-- ----------------------------
DROP TABLE IF EXISTS `tbl_docbrowseinfo`;
CREATE TABLE `tbl_docbrowseinfo` (
  `ID` varchar(32) NOT NULL COMMENT 'ID',
  `DocID` varchar(32) DEFAULT NULL COMMENT '文档ID tbl_NDADocInfo表中的ID',
  `DocType` varchar(32) DEFAULT NULL COMMENT '文档类型',
  `UserName` varchar(32) DEFAULT NULL COMMENT '浏览者',
  `BrowseTime` datetime DEFAULT NULL COMMENT '浏览时间',
  `BrowseIP` varchar(32) DEFAULT NULL COMMENT '浏览IP',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tbl_DocBrowseInfo 用户浏览文档信息表';

-- ----------------------------
-- Records of tbl_docbrowseinfo
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_ndabasicinfo
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ndabasicinfo`;
CREATE TABLE `tbl_ndabasicinfo` (
  `ID` varchar(32) NOT NULL DEFAULT '1' COMMENT 'ID 同一个文档的不同历史版本公用此ID',
  `Title` varchar(128) DEFAULT NULL COMMENT '标题',
  `AbstractContext` varchar(3072) DEFAULT NULL COMMENT '摘要',
  `KeyWords` varchar(1024) DEFAULT NULL COMMENT '关键词',
  `FileName` varchar(128) DEFAULT NULL COMMENT '文件名 本文档所属NDA',
  `FileExtension` varchar(32) DEFAULT NULL COMMENT '扩展名',
  `FileHash` varchar(128) DEFAULT NULL COMMENT 'NDA文件Hash',
  `TimeStamp` varchar(128) DEFAULT NULL COMMENT '时间戳',
  `DocTypeID` varchar(32) DEFAULT NULL COMMENT '文档类型 tbl_DictDocTypes中的文档类型ID',
  `DocOrder` int(11) DEFAULT NULL COMMENT '文档顺序 此文档在NDA所有文档中按照上传时间的排序，避免每次显示顺序变化',
  `InitiatorUserName` varchar(32) NOT NULL COMMENT '发起人 创建人在tbl_User中的UserName',
  `InitiatorTime` datetime NOT NULL COMMENT '发起时间',
  `InitiatorIP` varchar(32) DEFAULT NULL COMMENT '发起IP',
  `UpdateUserName` varchar(32) DEFAULT NULL COMMENT '最近更新人',
  `UpdateTime` datetime DEFAULT NULL COMMENT '最近更新时间 最后一次更新时间',
  `LastDocNo` int(11) DEFAULT '0' COMMENT '最新文档序号 本NDA最新文档序号，每增加一个新文档，本字段加1',
  `PubKey` varchar(128) DEFAULT NULL COMMENT '公钥',
  `PrivateKey` varchar(128) DEFAULT NULL COMMENT '私钥',
  `NDAItems` text COMMENT 'NDA条款 NDA条款文本',
  `memo` varchar(1024) DEFAULT NULL COMMENT '备注',
  `Revision` varchar(32) DEFAULT NULL COMMENT '乐观锁 为了解决并发冲突增加的字段',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tbl_NDABasicInfo 某一NDA的基本信息表，后续的文档以此表为发起点';

-- ----------------------------
-- Records of tbl_ndabasicinfo
-- ----------------------------
INSERT INTO `tbl_ndabasicinfo` VALUES ('793e0ffd755746898cce1ba95d40cf17', '这是分享给aaa的一个测试', '这是简介', null, null, null, null, null, null, null, 'lick', '2019-06-26 14:18:56', '127.0.0.1', 'lick', '2019-06-27 20:07:11', null, null, null, '<h1>这是一个测sd试sss</h1>\n\n<p>&nbsp;</p>\n\n<h2>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <s><em>仅仅是fff测试</em></s></h2>\n\n<h1><s><em>asdaafasdf</em></s><em>asfasfasfas</em></h1>\n\n<p>&nbsp;</p>\n\n<p><strong><em>sdfd阿斯蒂芬阿斯顿发生士大夫</em></strong></p>\n\n<p>&nbsp;</p>\n\n<p><strong><em>豆腐干地方兄弟v现场v</em></strong></p>\n', '这是备注', null);

-- ----------------------------
-- Table structure for tbl_ndadocinfo
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ndadocinfo`;
CREATE TABLE `tbl_ndadocinfo` (
  `ID` varchar(32) NOT NULL COMMENT 'ID',
  `NDADocID` varchar(32) NOT NULL COMMENT 'NDA文档ID tbl_NDADocBasicInfo中的ID',
  `DocHash` varchar(1024) NOT NULL COMMENT '文档Hash 文档内容本身的Hash，用于在IPFS中检索',
  `TimeStamp` varchar(1024) DEFAULT NULL COMMENT '时间戳 利用文档Hash、时间等生成的Hash',
  `FileName` varchar(128) DEFAULT NULL COMMENT '文件名',
  `FileExtension` varchar(32) DEFAULT NULL COMMENT '扩展名',
  `FileOrder` int(11) NOT NULL DEFAULT '1' COMMENT '顺序 按照上传时间对文档进行编号排序',
  `UploadUserName` varchar(32) DEFAULT NULL COMMENT '上传人',
  `UploadTime` datetime DEFAULT NULL COMMENT '上传时间',
  `UploadIP` varchar(32) DEFAULT NULL COMMENT '上传IP',
  `PrevID` varchar(32) DEFAULT NULL COMMENT '前一文档ID 此表中的ID',
  `PrevTimeStamp` varchar(1024) DEFAULT NULL COMMENT '前一文档时间戳',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tbl_NDADocInfo NDA文档记录信息表，这里的文档是指除了NDA协议本身外的其它文档。每增加一个NDA文档，在此表中增加一条新记录。文档时间戳由文档内容hash值+其它信息生成。按照现在的思路，文档的内容存放在IPFS文件系统中，文档Hash在保存时返回。除此以外，要保存上一文档的时间戳';

-- ----------------------------
-- Records of tbl_ndadocinfo
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_ndaitemtpl
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ndaitemtpl`;
CREATE TABLE `tbl_ndaitemtpl` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CreateUserName` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CreateTime` datetime DEFAULT NULL COMMENT '创建时间',
  `CreateIP` varchar(32) DEFAULT NULL COMMENT '创建时IP',
  `UpdateUserName` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UpdateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `UpdateIP` varchar(32) DEFAULT NULL COMMENT '更新时IP',
  `NDATitle` varchar(300) DEFAULT NULL COMMENT 'NDA标题',
  `NDAItem` text NOT NULL COMMENT 'NDA条款',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='tbl_NDAItemTpl NDA条款模板表';

-- ----------------------------
-- Records of tbl_ndaitemtpl
-- ----------------------------
INSERT INTO `tbl_ndaitemtpl` VALUES ('1', 'lick', '2019-06-22 00:10:28', null, 'lick', '2019-06-22 10:32:04', null, '测试1', '<h1>这是一个测sd试</h1>\n\n<p>&nbsp;</p>\n\n<h2>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <s><em>仅仅是fff测试</em></s></h2>\n\n<h1><s><em>asdaafasdf</em></s><em>asfasfasfas</em></h1>\n\n<p>&nbsp;</p>\n\n<p><strong><em>sdfd阿斯蒂芬阿斯顿发生士大夫</em></strong></p>\n\n<p>&nbsp;</p>\n\n<p><strong><em>豆腐干地方兄弟v现场v</em></strong></p>\n');
INSERT INTO `tbl_ndaitemtpl` VALUES ('2', 'lick', '2019-06-25 08:53:21', null, null, null, null, '测试2', '<h1>asf</h1>\n\n<h2>受到</h2>\n');

-- ----------------------------
-- Table structure for tbl_ndashare
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ndashare`;
CREATE TABLE `tbl_ndashare` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `NDAID` varchar(32) DEFAULT NULL COMMENT 'NDA编号',
  `OrderNumber` int(11) DEFAULT NULL COMMENT '人员顺序 暂时保留，如果出现一对多的情况可以保证顺序',
  `OrgName` varchar(128) DEFAULT '0' COMMENT '组织名称',
  `UserName` varchar(32) NOT NULL DEFAULT '0' COMMENT '用户名 tbl_UserInfo中的用户名',
  `PubKey` varchar(128) DEFAULT NULL COMMENT '公钥',
  `PrivateKey` varchar(128) DEFAULT NULL COMMENT '私钥',
  `CreateUserName` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CreateTime` datetime DEFAULT NULL COMMENT '创建时间',
  `OperateIP` varchar(32) DEFAULT NULL COMMENT '操作IP',
  `Valid` varchar(1) DEFAULT '1' COMMENT '有效 为以后留证据，取消分享后部真正删除本记录，而是做标记',
  `ShareStatus` varchar(6) DEFAULT NULL COMMENT '是否查看  0 未查看 1 同意 2 拒绝 3 修改(不同意) 4 回返 5 待确认',
  `ReceiverStatus` varchar(6) DEFAULT NULL COMMENT '是否查看  0 未查看 1 同意 2 拒绝 3 修改(不同意) 4 回返 5 待确认',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='tbl_NDAShare 创意分享信息表，即确定某创意都谁可以看到';

-- ----------------------------
-- Records of tbl_ndashare
-- ----------------------------
INSERT INTO `tbl_ndashare` VALUES ('1', '11', null, '0', 'aaa', null, null, 'lick', '2019-06-22 12:54:59', null, '1', '0', null);
INSERT INTO `tbl_ndashare` VALUES ('2', '1', null, '0', 'bbb', null, null, 'lick', '2019-06-23 09:58:07', null, '1', '0', null);
INSERT INTO `tbl_ndashare` VALUES ('3', '13', null, '0', 'ccc', null, null, 'lick', '2019-06-23 09:58:25', null, '1', '1', null);
INSERT INTO `tbl_ndashare` VALUES ('4', '4', null, '0', 'lick', null, null, 'aaa', '2019-06-23 09:59:55', null, '1', '0', null);
INSERT INTO `tbl_ndashare` VALUES ('5', '5', null, '0', 'lick', null, null, 'bbb', '2019-06-23 10:00:10', null, '1', '0', null);
INSERT INTO `tbl_ndashare` VALUES ('6', '6', null, '0', 'lick', null, null, 'ccc', '2019-06-23 10:00:22', null, '1', '0', null);
INSERT INTO `tbl_ndashare` VALUES ('7', '7', null, '0', 'lick', null, null, 'aaa', '2019-06-23 10:00:37', null, '1', '0', null);
INSERT INTO `tbl_ndashare` VALUES ('8', '793e0ffd755746898cce1ba95d40cf17', null, null, 'aaa', null, null, 'lick', '2019-06-26 14:18:56', '127.0.0.1', null, '0', null);

-- ----------------------------
-- Table structure for tbl_orgnization
-- ----------------------------
DROP TABLE IF EXISTS `tbl_orgnization`;
CREATE TABLE `tbl_orgnization` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID 组织ID',
  `OrgName` varchar(128) NOT NULL COMMENT '组织名称',
  `OrgLeader` varchar(128) DEFAULT NULL COMMENT '组织法人',
  `Province` varchar(32) DEFAULT NULL COMMENT '省',
  `City` varchar(32) DEFAULT NULL COMMENT '市',
  `District` varchar(32) DEFAULT NULL COMMENT '区',
  `Address` varchar(128) DEFAULT NULL COMMENT '地址',
  `PostCode` varchar(6) DEFAULT NULL COMMENT '邮政编码',
  `Telephone` varchar(128) DEFAULT NULL COMMENT '联系电话',
  `Email` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `OrgType` varchar(128) DEFAULT NULL COMMENT '组织类型 组织在平台中的类型：管理，用户',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tbl_Orgnization 组织名称表，现在不用，但是可以先保留';

-- ----------------------------
-- Records of tbl_orgnization
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_userinfo
-- ----------------------------
DROP TABLE IF EXISTS `tbl_userinfo`;
CREATE TABLE `tbl_userinfo` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `UserName` varchar(32) NOT NULL COMMENT '用户名',
  `Password` varchar(1024) NOT NULL COMMENT '密码 加密的密码',
  `OrgName` varchar(128) DEFAULT NULL COMMENT '所属组织',
  `Name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `Gender` varchar(32) DEFAULT NULL COMMENT '性别',
  `Birthday` date DEFAULT NULL COMMENT '出生日期',
  `CertID` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `HomeProvince` varchar(32) DEFAULT NULL COMMENT '家庭所在省',
  `HomeCity` varchar(32) DEFAULT NULL COMMENT '家庭所在市',
  `HomeDistrict` varchar(32) DEFAULT NULL COMMENT '家庭所在区',
  `HomeAddress` varchar(128) DEFAULT NULL COMMENT '家庭地址',
  `HomePostCode` varchar(6) DEFAULT NULL COMMENT '家庭邮编',
  `MailProvince` varchar(32) DEFAULT NULL COMMENT '通讯地址省',
  `MailCity` varchar(32) DEFAULT NULL COMMENT '通讯地址市',
  `MailDistrict` varchar(32) DEFAULT NULL COMMENT '通讯地址区',
  `MailAddress` varchar(32) DEFAULT NULL COMMENT '详细通讯地址',
  `MailPostCode` varchar(6) DEFAULT NULL COMMENT '通讯地址邮编',
  `PhoneNo` varchar(32) DEFAULT NULL COMMENT '固定电话',
  `MobilePhone` varchar(32) DEFAULT NULL COMMENT '移动电话',
  `CreateUserName` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CreateTime` datetime DEFAULT NULL COMMENT '创建时间',
  `CreateIP` varchar(32) DEFAULT NULL COMMENT '创建IP',
  `UpdateUserName` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UpdateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `UpdateIP` varchar(32) DEFAULT NULL COMMENT '更新IP',
  `Email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `BackupEmail` varchar(32) DEFAULT NULL COMMENT '备用邮箱',
  `Signature` blob COMMENT '签名',
  `QQ` varchar(32) DEFAULT NULL COMMENT 'QQ',
  `WeChat` varchar(32) DEFAULT NULL COMMENT '微信',
  `Instagram` varchar(32) DEFAULT NULL COMMENT 'Instagram',
  `Valid` varchar(1) DEFAULT '1' COMMENT '有效标志 用户信息并不真正删除，只是加删除标志',
  `Question4Pass` varchar(32) DEFAULT NULL COMMENT '找回密码提示问题',
  `Answer` varchar(32) DEFAULT NULL COMMENT '问题答案',
  `CAInfo` varchar(1024) DEFAULT NULL COMMENT 'CA信息 用户CA的相关信息',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UserName` (`UserName`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='tbl_UserInfo 用户表';

-- ----------------------------
-- Records of tbl_userinfo
-- ----------------------------
INSERT INTO `tbl_userinfo` VALUES ('1', 'lick', '123456', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '1', null, null, null);
INSERT INTO `tbl_userinfo` VALUES ('2', 'admin', '123456', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '1', null, null, null);
INSERT INTO `tbl_userinfo` VALUES ('3', 'aaa', '123456', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '1', null, null, null);
INSERT INTO `tbl_userinfo` VALUES ('4', 'bbb', '123456', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '1', null, null, null);
INSERT INTO `tbl_userinfo` VALUES ('5', 'ccc', '123456', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '1', null, null, null);
INSERT INTO `tbl_userinfo` VALUES ('6', 'hello', '123456', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '13913452659', null, '2019-06-23 14:39:54', '127.0.0.1', null, null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for tbl_userlogin
-- ----------------------------
DROP TABLE IF EXISTS `tbl_userlogin`;
CREATE TABLE `tbl_userlogin` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `UserName` varchar(32) NOT NULL COMMENT '用户名',
  `LoginTime` datetime NOT NULL COMMENT '登录时间',
  `LoginIP` varchar(32) DEFAULT NULL COMMENT '登录IP',
  `LogoutTime` datetime DEFAULT NULL COMMENT '登出时间',
  `LogoutAction` varchar(32) DEFAULT NULL COMMENT '登出动作 登出动作包括点击退出，超时突出，关闭网页退出等',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tbl_UserLogin 用户登录信息表';

-- ----------------------------
-- Records of tbl_userlogin
-- ----------------------------
