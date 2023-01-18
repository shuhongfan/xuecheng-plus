/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.101.65
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : 192.168.101.65:3306
 Source Schema         : xcplus_orders

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 05/10/2022 10:24:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mq_message
-- ----------------------------
DROP TABLE IF EXISTS `mq_message`;
CREATE TABLE `mq_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息类型代码: course_publish ,  media_test',
  `business_key1` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `execute_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '通知次数',
  `state` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '处理状态，0:初始，1:成功',
  `returnfailure_date` datetime NULL DEFAULT NULL COMMENT '回复失败时间',
  `returnsuccess_date` datetime NULL DEFAULT NULL COMMENT '回复成功时间',
  `returnfailure_msg` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复失败内容',
  `execute_date` datetime NULL DEFAULT NULL COMMENT '最近通知时间',
  `stage_state1` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段1处理状态, 0:初始，1:成功',
  `stage_state2` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段2处理状态, 0:初始，1:成功',
  `stage_state3` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段3处理状态, 0:初始，1:成功',
  `stage_state4` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段4处理状态, 0:初始，1:成功',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mq_message
-- ----------------------------

-- ----------------------------
-- Table structure for mq_message_history
-- ----------------------------
DROP TABLE IF EXISTS `mq_message_history`;
CREATE TABLE `mq_message_history`  (
  `id` bigint NOT NULL COMMENT '消息id',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息类型代码',
  `business_key1` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `execute_num` int UNSIGNED NULL DEFAULT NULL COMMENT '通知次数',
  `state` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '处理状态，0:初始，1:成功，2:失败',
  `returnfailure_date` datetime NULL DEFAULT NULL COMMENT '回复失败时间',
  `returnsuccess_date` datetime NULL DEFAULT NULL COMMENT '回复成功时间',
  `returnfailure_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复失败内容',
  `execute_date` datetime NULL DEFAULT NULL COMMENT '最近通知时间',
  `stage_state1` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stage_state2` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stage_state3` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stage_state4` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mq_message_history
-- ----------------------------
INSERT INTO `mq_message_history` VALUES (15, 'payresult_notify', '11', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '0', '0', '0', '0');

-- ----------------------------
-- Table structure for xc_orders
-- ----------------------------
DROP TABLE IF EXISTS `xc_orders`;
CREATE TABLE `xc_orders`  (
  `id` bigint NOT NULL COMMENT '订单号',
  `total_price` float(8, 2) NOT NULL COMMENT '总价',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易状态',
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `order_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单类型',
  `order_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单名称',
  `order_descrip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单描述',
  `order_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单明细json',
  `out_business_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '外部系统业务id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orders_unioue`(`out_business_id`) USING BTREE COMMENT '外部系统的业务id'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xc_orders
-- ----------------------------
INSERT INTO `xc_orders` VALUES (1577177773194113024, 1.00, '2022-10-04 14:04:18', '600002', '50', '60201', '测试课程01', '购买课程:测试课程01', '[{\"goodsId\":2,\"goodsType\":\"60201\",\"goodsName\":\"测试课程01\",\"goodsPrice\":1}]', '10');
INSERT INTO `xc_orders` VALUES (1577258681653280768, 1.00, '2022-10-04 19:25:48', '600002', '52', '60201', 'Nacos微服务开发实战', '购买课程:Nacos微服务开发实战', '[{\"goodsId\":117,\"goodsType\":\"60201\",\"goodsName\":\"Nacos微服务开发实战\",\"goodsPrice\":1}]', '11');

-- ----------------------------
-- Table structure for xc_orders_goods
-- ----------------------------
DROP TABLE IF EXISTS `xc_orders_goods`;
CREATE TABLE `xc_orders_goods`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单号',
  `goods_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品id',
  `goods_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品类型',
  `goods_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
  `goods_price` float(10, 2) NOT NULL COMMENT '商品交易价，单位分',
  `goods_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品详情json',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xc_orders_goods
-- ----------------------------
INSERT INTO `xc_orders_goods` VALUES (2, 1577177773194113024, '2', '60201', '测试课程01', 1.00, NULL);
INSERT INTO `xc_orders_goods` VALUES (3, 1577258681653280768, '117', '60201', 'Nacos微服务开发实战', 1.00, NULL);

-- ----------------------------
-- Table structure for xc_pay_record
-- ----------------------------
DROP TABLE IF EXISTS `xc_pay_record`;
CREATE TABLE `xc_pay_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pay_no` bigint NOT NULL COMMENT '本系统支付交易号',
  `out_pay_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '第三方支付交易流水号',
  `out_pay_channel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '第三方支付渠道编号',
  `order_id` bigint NOT NULL COMMENT '商品订单号',
  `order_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单名称',
  `total_price` float(8, 2) NOT NULL COMMENT '订单总价单位元',
  `currency` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '币种CNY',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '支付状态',
  `pay_success_time` datetime NULL DEFAULT NULL COMMENT '支付成功时间',
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pay_order_unioue`(`out_pay_no`) USING BTREE COMMENT '第三方支付订单号'
) ENGINE = InnoDB AUTO_INCREMENT = 1577182653388025859 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xc_pay_record
-- ----------------------------
INSERT INTO `xc_pay_record` VALUES (1577177773231415298, 1577177773231861760, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 14:04:18', '601001', NULL, '50');
INSERT INTO `xc_pay_record` VALUES (1577179016003612674, 1577179015973519360, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 14:09:15', '601001', NULL, '50');
INSERT INTO `xc_pay_record` VALUES (1577181370643955713, 1577181370624544768, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 14:18:36', '601001', NULL, '50');
INSERT INTO `xc_pay_record` VALUES (1577182027190972417, 1577182027171524608, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 14:21:13', '601001', NULL, '50');
INSERT INTO `xc_pay_record` VALUES (1577182653388025858, 1577182653344460800, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 14:23:42', '601001', NULL, '50');
INSERT INTO `xc_pay_record` VALUES (1577237009017651202, 1577237008990695424, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 17:59:41', '601001', NULL, '50');
INSERT INTO `xc_pay_record` VALUES (1577239361250500609, 1577239361225244672, NULL, NULL, 1577177773194113024, '测试课程01', 1.00, 'CNY', '2022-10-04 18:09:02', '601002', '2022-10-04 18:09:44', '50');
INSERT INTO `xc_pay_record` VALUES (1577419635984793601, 1577419635962195968, '2022100522001422760505741092', 'Alipay', 1577258681653280768, 'Nacos微服务开发实战', 1.00, 'CNY', '2022-10-05 06:05:23', '601002', '2022-10-05 06:06:39', '52');

SET FOREIGN_KEY_CHECKS = 1;
