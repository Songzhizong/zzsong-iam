/*
 Navicat Premium Data Transfer

 Source Server         : 本地_mysql
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : zzsong

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 23/02/2022 17:51:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for iam_oauth_client
-- ----------------------------
DROP TABLE IF EXISTS `iam_oauth_client`;
CREATE TABLE `iam_oauth_client`
(
  `id`                        bigint                           NOT NULL,
  `name`                      varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `client_id`                 varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `client_secret`             varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `access_token_validity`     bigint                           NOT NULL,
  `refresh_token_validity`    bigint                           NOT NULL,
  `access_token_auto_renewal` tinyint                          NOT NULL,
  `accept_repetition_login`   tinyint                          NOT NULL,
  `token_value`               varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `enabled`                   tinyint                          NOT NULL,
  `version`                   bigint                           NOT NULL,
  `created_time`              datetime                         NOT NULL,
  `updated_time`              datetime                         NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_id` (`client_id`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

SET FOREIGN_KEY_CHECKS = 1;
