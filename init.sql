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

 Date: 26/02/2022 23:58:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for iam_auth_client
-- ----------------------------
DROP TABLE IF EXISTS `iam_auth_client`;
CREATE TABLE `iam_auth_client`
(
  `id`                        bigint                                                 NOT NULL COMMENT '主键',
  `name`                      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `client_id`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端唯一id',
  `client_secret`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `access_token_validity`     bigint                                                 NOT NULL COMMENT 'access token 有效期',
  `refresh_token_validity`    bigint                                                 NOT NULL COMMENT 'refresh token 有效期',
  `access_token_auto_renewal` tinyint                                                NOT NULL COMMENT '是否自动刷新accessToken',
  `accept_repetition_login`   tinyint                                                NOT NULL COMMENT '是否允许多设备同时登录',
  `token_value`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `enabled`                   tinyint                                                NOT NULL COMMENT '是否启用',
  `version`                   bigint                                                 NOT NULL COMMENT '乐观锁版本',
  `created_time`              datetime                                               NOT NULL COMMENT '创建时间',
  `updated_time`              datetime                                               NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_id` (`client_id`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='授权客户端';

-- ----------------------------
-- Table structure for iam_role
-- ----------------------------
DROP TABLE IF EXISTS `iam_role`;
CREATE TABLE `iam_role`
(
  `id`           bigint                                                 NOT NULL COMMENT '主键',
  `tenant_id`    bigint                                                 NOT NULL COMMENT '所属租户id',
  `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `type`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色类型',
  `enabled`      tinyint                                                NOT NULL COMMENT '是否为启用状态',
  `description`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色描述',
  `version`      bigint                                                 NOT NULL COMMENT '乐观锁版本',
  `created_time` datetime                                               NOT NULL COMMENT '创建时间',
  `updated_time` datetime                                               NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `tenant_id` (`tenant_id`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='角色表';

-- ----------------------------
-- Table structure for iam_tenant
-- ----------------------------
DROP TABLE IF EXISTS `iam_tenant`;
CREATE TABLE `iam_tenant`
(
  `id`            bigint                                                 NOT NULL COMMENT '主键',
  `pid`           bigint                                                 NOT NULL COMMENT '父租户id',
  `router`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '树路由',
  `name`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '租户名称',
  `owner_user_id` bigint                                                 NOT NULL COMMENT '拥有人用户id',
  `frozen`        tinyint                                                NOT NULL COMMENT '是否冻结状态',
  `version`       bigint                                                 NOT NULL COMMENT '乐观锁版本',
  `created_time`  datetime                                               NOT NULL COMMENT '创建时间',
  `updated_time`  datetime                                               NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`) USING BTREE,
  KEY `router` (`router`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='租户表';

-- ----------------------------
-- Table structure for iam_tenant_user
-- ----------------------------
DROP TABLE IF EXISTS `iam_tenant_user`;
CREATE TABLE `iam_tenant_user`
(
  `id`           bigint                                                 NOT NULL COMMENT '主键',
  `tenant_id`    bigint                                                 NOT NULL COMMENT '租户id',
  `user_id`      bigint                                                 NOT NULL COMMENT '用户id',
  `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户姓名',
  `phone`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户手机号',
  `frozen`       tinyint                                                NOT NULL COMMENT '是否被租户冻结',
  `version`      bigint                                                 NOT NULL COMMENT '乐观锁版本',
  `created_time` datetime                                               NOT NULL COMMENT '创建时间',
  `updated_time` datetime                                               NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_user` (`tenant_id`, `user_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `phone` (`phone`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='租户用户关系';

-- ----------------------------
-- Table structure for iam_user
-- ----------------------------
DROP TABLE IF EXISTS `iam_user`;
CREATE TABLE `iam_user`
(
  `id`                 bigint                                                 NOT NULL COMMENT '主键',
  `name`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '姓名',
  `account`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
  `phone`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
  `email`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '邮箱',
  `password`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `last_password_time` bigint                                                 NOT NULL COMMENT '最近更新密码时间',
  `frozen`             tinyint                                                NOT NULL COMMENT '是否被冻结',
  `version`            bigint                                                 NOT NULL COMMENT '乐观锁版本号',
  `created_time`       datetime                                               NOT NULL COMMENT '创建时间',
  `updated_time`       datetime                                               NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE,
  UNIQUE KEY `phone` (`phone`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='用户信息';

-- ----------------------------
-- Table structure for iam_user_role
-- ----------------------------
DROP TABLE IF EXISTS `iam_user_role`;
CREATE TABLE `iam_user_role`
(
  `id`           bigint   NOT NULL COMMENT '主键',
  `tenant_id`    bigint   NOT NULL COMMENT '租户id',
  `role_id`      bigint   NOT NULL COMMENT '角色id',
  `user_id`      bigint   NOT NULL COMMENT '用户id',
  `version`      bigint   NOT NULL COMMENT '乐观锁版本',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='用户角色关系';

SET FOREIGN_KEY_CHECKS = 1;
