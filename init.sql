/*
 Navicat Premium Data Transfer

 Source Server         : 本机_mysql
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : zzsong

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 23/02/2022 21:07:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for iam_oauth_client
-- ----------------------------
DROP TABLE IF EXISTS `iam_oauth_client`;
CREATE TABLE `iam_oauth_client`
(
  `id`                        bigint                           NOT NULL COMMENT '主键',
  `name`                      varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `client_id`                 varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '客户端唯一id',
  `client_secret`             varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `access_token_validity`     bigint                           NOT NULL COMMENT 'access token 有效期',
  `refresh_token_validity`    bigint                           NOT NULL COMMENT 'refresh token 有效期',
  `access_token_auto_renewal` tinyint                          NOT NULL COMMENT '是否自动刷新accessToken',
  `accept_repetition_login`   tinyint                          NOT NULL COMMENT '是否允许多设备同时登录',
  `token_value`               varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `enabled`                   tinyint                          NOT NULL COMMENT '是否启用',
  `version`                   bigint                           NOT NULL COMMENT '乐观锁版本',
  `created_time`              datetime                         NOT NULL COMMENT '创建时间',
  `updated_time`              datetime                         NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_id` (`client_id`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for iam_user
-- ----------------------------
DROP TABLE IF EXISTS `iam_user`;
CREATE TABLE `iam_user`
(
  `id`                 bigint                           NOT NULL COMMENT '主键',
  `name`               varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '姓名',
  `account`            varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
  `phone`              varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
  `email`              varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '邮箱',
  `password`           varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `last_password_time` bigint                           NOT NULL COMMENT '最近更新密码时间',
  `frozen`             tinyint                          NOT NULL COMMENT '是否被冻结',
  `version`            bigint                           NOT NULL COMMENT '乐观锁版本号',
  `created_time`       datetime                         NOT NULL COMMENT '创建时间',
  `updated_time`       datetime                         NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE,
  UNIQUE KEY `phone` (`phone`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

SET FOREIGN_KEY_CHECKS = 1;
