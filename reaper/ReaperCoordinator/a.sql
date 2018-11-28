/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.5.12-log : Database - netpf_maintain_dbc
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`netpf_maintain_dbc` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `netpf_maintain_dbc`;

/*Table structure for table `nsdomain` */

DROP TABLE IF EXISTS `nsdomain`;

CREATE TABLE `nsdomain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rid` varchar(1024) NOT NULL COMMENT '串号，一级8位，二级8位，其它4位',
  `domain` varchar(1024) NOT NULL COMMENT '当前域名',
  `domainname` varchar(1024) NOT NULL COMMENT '站点名称',
  `domainname_en` varchar(1024) DEFAULT NULL,
  `domainurl` varchar(4096) DEFAULT NULL COMMENT '站点URL',
  `level` bigint(20) NOT NULL COMMENT '域名级别，一级、二级...',
  `pdomain` varchar(1024) DEFAULT NULL COMMENT '上级域名',
  `rootdomain` varchar(1024) NOT NULL COMMENT '一级域名',
  `fullcover` bigint(20) DEFAULT NULL COMMENT '1,支持全站覆盖；非1，不支持。',
  `domaintype` int(11) DEFAULT NULL COMMENT '0境内；1境外',
  `SEO` int(11) DEFAULT NULL,
  `SITEAREA` varchar(1024) DEFAULT NULL,
  `SITEAREACODE` bigint(22) DEFAULT NULL,
  `siteClassificationID` bigint(20) DEFAULT NULL,
  `ICP_NO` varchar(256) DEFAULT NULL,
  `ICP_NAME` varchar(1024) DEFAULT NULL,
  `ICP_AREA` varchar(1024) DEFAULT NULL,
  `ICP_AREACODE` bigint(22) DEFAULT NULL,
  `ipLong` bigint(32) DEFAULT NULL,
  `ipString` varchar(32) DEFAULT NULL,
  `ipAddr` varchar(256) DEFAULT NULL,
  `ipAreaCode` bigint(22) DEFAULT NULL,
  `languageID` bigint(20) DEFAULT NULL,
  `mediaClassificationID` bigint(20) DEFAULT NULL,
  `weight` bigint(20) DEFAULT NULL,
  `updatetime` bigint(20) DEFAULT NULL,
  `updateuser` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30507 DEFAULT CHARSET=utf8;

/*Table structure for table `nsproxy` */

DROP TABLE IF EXISTS `nsproxy`;

CREATE TABLE `nsproxy` (
  `proxyID` bigint(22) NOT NULL AUTO_INCREMENT,
  `IP` bigint(22) DEFAULT NULL,
  `port` bigint(22) DEFAULT NULL,
  `type` bigint(22) DEFAULT NULL COMMENT '1,http;2,socks',
  `userName` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `available` bigint(22) DEFAULT NULL COMMENT '0,可用；1，不可用',
  `proxyType` bigint(22) DEFAULT NULL COMMENT '代理类型，2，境外；4，境内',
  KEY `proxyID` (`proxyID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `nsscript` */

DROP TABLE IF EXISTS `nsscript`;

CREATE TABLE `nsscript` (
  `ID` bigint(22) NOT NULL AUTO_INCREMENT,
  `siteName` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `domainID` bigint(22) NOT NULL,
  `dataSourceType` bigint(22) NOT NULL,
  `charset` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reverserClassPath` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `extractorClassPath` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `floorXPath` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `topicURLFormat` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `topicSubpageURLFormat` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `forumPageURLFormat` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `forumSubpageURLFormat` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `levelSwitch` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `comment` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createTime` bigint(22) DEFAULT NULL,
  `updateTime` bigint(22) DEFAULT NULL,
  `createUser` bigint(22) DEFAULT NULL,
  `updateUser` bigint(22) DEFAULT NULL,
  `status` bigint(22) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `reaper_task` */

DROP TABLE IF EXISTS `reaper_task`;

CREATE TABLE `reaper_task` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `domainID` bigint(11) DEFAULT NULL,
  `siteType` bigint(11) DEFAULT NULL,
  `domainType` bigint(11) DEFAULT NULL,
  `proxyType` bigint(11) DEFAULT NULL,
  `enable` bigint(11) DEFAULT NULL,
  `charset` varchar(32) DEFAULT 'utf-8',
  `status` bigint(11) DEFAULT NULL,
  `processHost` varchar(256) DEFAULT NULL,
  `lastUpdateTime` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
