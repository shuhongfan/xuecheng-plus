/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.101.65
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : 192.168.101.65:3306
 Source Schema         : xcplus_media

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 15/10/2022 10:47:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for media_files
-- ----------------------------
DROP TABLE IF EXISTS `media_files`;
CREATE TABLE `media_files`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件id,md5值',
  `company_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `company_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构名称',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
  `file_type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型（图片、文档，视频）',
  `tags` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签',
  `bucket` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储目录',
  `file_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储路径',
  `file_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件id',
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '媒资文件访问地址',
  `username` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传人',
  `create_date` datetime NULL DEFAULT NULL COMMENT '上传时间',
  `change_date` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `status` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '状态,1:正常，0:不展示',
  `remark` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `audit_status` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核状态',
  `audit_mind` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核意见',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_fileid`(`file_id`) USING BTREE COMMENT '文件id唯一索引 '
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '媒资信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_files
-- ----------------------------
INSERT INTO `media_files` VALUES ('1580180577525002241', 1232141425, NULL, '1.jpg', '001003', NULL, 'mediafiles', NULL, '8383a8c2c1d098fcc07da7d6e79ae31e', '/mediafiles/2022/10/12/8383a8c2c1d098fcc07da7d6e79ae31e.jpg', NULL, '2022-10-12 20:56:23', NULL, '1', NULL, NULL, NULL, 5767);
INSERT INTO `media_files` VALUES ('1d0f0e6ed8a0c4a89bfd304b84599d9c', 1232141425, NULL, 'asset-icoGather.png', '001001', NULL, 'mediafiles', '2022/09/20/1d0f0e6ed8a0c4a89bfd304b84599d9c.png', '1d0f0e6ed8a0c4a89bfd304b84599d9c', '/mediafiles/2022/09/20/1d0f0e6ed8a0c4a89bfd304b84599d9c.png', NULL, '2022-09-20 21:21:28', NULL, '1', '', '002003', NULL, 8059);
INSERT INTO `media_files` VALUES ('1f229319d6fed3431d2f9d06193a433b', 1232141425, NULL, '01-分布式事务专题课程介绍.avi', '001002', '课程视频', 'video', '1/f/1f229319d6fed3431d2f9d06193a433b/1f229319d6fed3431d2f9d06193a433b.avi', '1f229319d6fed3431d2f9d06193a433b', '/video/1/f/1f229319d6fed3431d2f9d06193a433b/1f229319d6fed3431d2f9d06193a433b.mp4', NULL, '2022-09-14 18:30:02', NULL, '1', '', '002003', NULL, 14705152);
INSERT INTO `media_files` VALUES ('23f83ae728bd1269eee7ea2236e79644', 1232141425, NULL, '16-Nacos配置管理-课程总结.avi', '001002', '课程视频', 'video', '2/3/23f83ae728bd1269eee7ea2236e79644/23f83ae728bd1269eee7ea2236e79644.avi', '23f83ae728bd1269eee7ea2236e79644', '/video/2/3/23f83ae728bd1269eee7ea2236e79644/23f83ae728bd1269eee7ea2236e79644.mp4', NULL, '2022-09-14 18:21:44', NULL, '1', '', '002003', NULL, 26053632);
INSERT INTO `media_files` VALUES ('287cdd91c5d444e0752b626cbd95b41c', 1232141425, NULL, 'nacos01.mp4', '001002', '课程视频', 'video', '2/8/287cdd91c5d444e0752b626cbd95b41c/287cdd91c5d444e0752b626cbd95b41c.mp4', '287cdd91c5d444e0752b626cbd95b41c', '/video/2/8/287cdd91c5d444e0752b626cbd95b41c/287cdd91c5d444e0752b626cbd95b41c.mp4', NULL, '2022-09-14 18:28:43', NULL, '1', '', '002003', NULL, 25953447);
INSERT INTO `media_files` VALUES ('33c643206bb7c08e2cb99b622d7a1b63', 1232141425, NULL, '1.png', '001001', NULL, 'mediafiles', '2022/10/07/33c643206bb7c08e2cb99b622d7a1b63.png', '33c643206bb7c08e2cb99b622d7a1b63', '/mediafiles/2022/10/07/33c643206bb7c08e2cb99b622d7a1b63.png', NULL, '2022-10-07 06:20:05', NULL, '1', '', '002003', NULL, 169788);
INSERT INTO `media_files` VALUES ('345db593849aada5675ed1e438650eeb', 1232141425, NULL, '1.png', '001001', NULL, 'mediafiles', '2022/10/07/345db593849aada5675ed1e438650eeb.png', '345db593849aada5675ed1e438650eeb', '/mediafiles/2022/10/07/345db593849aada5675ed1e438650eeb.png', NULL, '2022-10-07 09:31:46', NULL, '1', '', '002003', NULL, 70171);
INSERT INTO `media_files` VALUES ('3a5a861d1c745d05166132c47b44f9e4', 1232141425, NULL, '01-Nacos配置管理-内容介绍.avi', '001002', '课程视频', 'video', '3/a/3a5a861d1c745d05166132c47b44f9e4/3a5a861d1c745d05166132c47b44f9e4.avi', '3a5a861d1c745d05166132c47b44f9e4', '/video/3/a/3a5a861d1c745d05166132c47b44f9e4/3a5a861d1c745d05166132c47b44f9e4.mp4', NULL, '2022-09-14 18:19:24', NULL, '1', '', '002003', NULL, 23839232);
INSERT INTO `media_files` VALUES ('3fb1d9a565cb92f395f384bd62ef24cd', 1232141425, NULL, '1614759607876_0.png', '001001', '课程图片', 'mediafiles', '2022/09/20/3fb1d9a565cb92f395f384bd62ef24cd.png', '3fb1d9a565cb92f395f384bd62ef24cd', '/mediafiles/2022/09/20/3fb1d9a565cb92f395f384bd62ef24cd.png', NULL, '2022-09-20 21:06:11', NULL, '1', '', '002003', NULL, 58873);
INSERT INTO `media_files` VALUES ('500598cae139f77c1bb54459909e0443', 1232141425, NULL, 'course8561649859933456434.html', '001003', NULL, 'mediafiles', 'course/119.html', '500598cae139f77c1bb54459909e0443', '/mediafiles/course/119.html', NULL, '2022-10-07 09:39:49', NULL, '1', '', '002003', NULL, 35652);
INSERT INTO `media_files` VALUES ('6ad24a762f67c18f61966c1b8c55abe6', 1232141425, NULL, '07-分布式事务基础理论-BASE理论.avi', '001002', '课程视频', 'video', '6/a/6ad24a762f67c18f61966c1b8c55abe6/6ad24a762f67c18f61966c1b8c55abe6.avi', '6ad24a762f67c18f61966c1b8c55abe6', '/video/6/a/6ad24a762f67c18f61966c1b8c55abe6/6ad24a762f67c18f61966c1b8c55abe6.mp4', NULL, '2022-09-14 18:30:16', NULL, '1', '', '002003', NULL, 13189632);
INSERT INTO `media_files` VALUES ('70a98b4a2fffc89e50b101f959cc33ca', 1232141425, NULL, '22-Hmily实现TCC事务-开发bank2的confirm方法.avi', '001002', '课程视频', 'video', '7/0/70a98b4a2fffc89e50b101f959cc33ca/70a98b4a2fffc89e50b101f959cc33ca.avi', '70a98b4a2fffc89e50b101f959cc33ca', '/video/7/0/70a98b4a2fffc89e50b101f959cc33ca/70a98b4a2fffc89e50b101f959cc33ca.mp4', NULL, '2022-09-14 18:30:52', NULL, '1', '', '002003', NULL, 18444288);
INSERT INTO `media_files` VALUES ('74b386417bb9f3764009dc94068a5e44', 1232141425, NULL, 'test2.html', '001003', NULL, 'mediafiles', 'course/74b386417bb9f3764009dc94068a5e44.html', '74b386417bb9f3764009dc94068a5e44', '/mediafiles/course/74b386417bb9f3764009dc94068a5e44.html', NULL, '2022-09-20 21:56:02', NULL, '1', '', '002003', NULL, 40124);
INSERT INTO `media_files` VALUES ('757891eae4473e7ba78827656b1ccacf', 1232141425, NULL, 'studyuser.png', '001001', NULL, 'mediafiles', NULL, '757891eae4473e7ba78827656b1ccacf', '/mediafiles/2022/10/13/757891eae4473e7ba78827656b1ccacf.png', NULL, '2022-10-13 19:57:01', NULL, '1', NULL, '002003', NULL, 4922);
INSERT INTO `media_files` VALUES ('8026f17cf7b8697eccec2c8406d0c96c', 1232141425, NULL, 'nacos.png', '001001', NULL, 'mediafiles', '2022/10/04/8026f17cf7b8697eccec2c8406d0c96c.png', '8026f17cf7b8697eccec2c8406d0c96c', '/mediafiles/2022/10/04/8026f17cf7b8697eccec2c8406d0c96c.png', NULL, '2022-10-04 18:55:01', NULL, '1', '', '002003', NULL, 128051);
INSERT INTO `media_files` VALUES ('809694a6a974c35e3a36f36850837d7c', 1232141425, NULL, '1.avi', '001002', '课程视频', 'video', NULL, '809694a6a974c35e3a36f36850837d7c', '/video/8/0/809694a6a974c35e3a36f36850837d7c/809694a6a974c35e3a36f36850837d7c.avi', NULL, '2022-10-13 21:27:14', NULL, '1', '', '002003', NULL, NULL);
INSERT INTO `media_files` VALUES ('a16da7a132559daf9e1193166b3e7f52', 1232141425, NULL, '1.jpg', '001003', NULL, 'mediafiles', '2022/09/20/a16da7a132559daf9e1193166b3e7f52.jpg', 'a16da7a132559daf9e1193166b3e7f52', '/mediafiles/2022/09/20/a16da7a132559daf9e1193166b3e7f52.jpg', NULL, '2022-09-20 21:26:08', NULL, '1', '', '002003', NULL, 248329);
INSERT INTO `media_files` VALUES ('a6fb4fc7faf1d3d0831819969529b888', 1232141425, NULL, '1.项目背景.mp4', '001002', '课程视频', 'video', NULL, 'a6fb4fc7faf1d3d0831819969529b888', '/video/a/6/a6fb4fc7faf1d3d0831819969529b888/a6fb4fc7faf1d3d0831819969529b888.mp4', NULL, '2022-10-13 21:30:17', NULL, '1', '', '002003', NULL, NULL);
INSERT INTO `media_files` VALUES ('b2deb4a098bb12653c57bbaa0099697a', 1232141425, NULL, 'course3448922126748441722.html', '001003', NULL, 'mediafiles', 'course/117.html', 'b2deb4a098bb12653c57bbaa0099697a', '/mediafiles/course/117.html', NULL, '2022-10-04 19:20:01', NULL, '1', '', '002003', NULL, 37705);
INSERT INTO `media_files` VALUES ('ca1d75b0a37b85fafd5f2e443f6f3f01', 1232141425, NULL, 'course2996275631019924973.html', '001003', NULL, 'mediafiles', 'course/118.html', 'ca1d75b0a37b85fafd5f2e443f6f3f01', '/mediafiles/course/118.html', NULL, '2022-10-07 06:40:51', NULL, '1', '', '002003', NULL, 35905);
INSERT INTO `media_files` VALUES ('d4af959873182feb0fdb91dc6c1958b5', 1232141425, NULL, 'widget-5.png', '001001', '课程图片', 'mediafiles', '2022/09/18/d4af959873182feb0fdb91dc6c1958b5.png', 'd4af959873182feb0fdb91dc6c1958b5', '/mediafiles/2022/09/18/d4af959873182feb0fdb91dc6c1958b5.png', NULL, '2022-09-18 21:49:55', NULL, '1', '', '002003', NULL, 17799);
INSERT INTO `media_files` VALUES ('db4e24f27d78ccac14401b7479b35c26', 1232141425, NULL, 'nonepic.jpg', '001001', NULL, 'mediafiles', '2022/09/23/db4e24f27d78ccac14401b7479b35c26.jpg', 'db4e24f27d78ccac14401b7479b35c26', '/mediafiles/2022/09/23/db4e24f27d78ccac14401b7479b35c26.jpg', NULL, '2022-09-23 18:27:26', NULL, '1', '', '002003', NULL, 6919);
INSERT INTO `media_files` VALUES ('df39983fcad83a6ceef14bfeeb1bc523', 1232141425, NULL, 'add.jpg', '001001', NULL, 'mediafiles', '2022/09/20/df39983fcad83a6ceef14bfeeb1bc523.jpg', 'df39983fcad83a6ceef14bfeeb1bc523', '/mediafiles/2022/09/20/df39983fcad83a6ceef14bfeeb1bc523.jpg', NULL, '2022-09-20 21:13:59', NULL, '1', '', '002003', NULL, 10487);
INSERT INTO `media_files` VALUES ('e00ce88f53cc391d9ffd51a81834d2af', 1232141425, NULL, 'widget-1.jpg', '001001', '课程图片', 'mediafiles', '2022/09/18/e00ce88f53cc391d9ffd51a81834d2af.jpg', 'e00ce88f53cc391d9ffd51a81834d2af', '/mediafiles/2022/09/18/e00ce88f53cc391d9ffd51a81834d2af.jpg', NULL, '2022-09-18 21:48:50', NULL, '1', '', '002003', NULL, 71386);
INSERT INTO `media_files` VALUES ('e726b71ba99c70e8c9d2850c2a7019d7', 1232141425, NULL, 'asset-login_img.jpg', '001001', NULL, 'mediafiles', '2022/09/20/e726b71ba99c70e8c9d2850c2a7019d7.jpg', 'e726b71ba99c70e8c9d2850c2a7019d7', '/mediafiles/2022/09/20/e726b71ba99c70e8c9d2850c2a7019d7.jpg', NULL, '2022-09-20 21:44:50', NULL, '1', '', '002003', NULL, 22620);
INSERT INTO `media_files` VALUES ('fbb57de7001cccf1e28fbe34c7506ddc', 1232141425, NULL, 'asset-logo.png', '001001', NULL, 'mediafiles', '2022/09/20/fbb57de7001cccf1e28fbe34c7506ddc.png', 'fbb57de7001cccf1e28fbe34c7506ddc', '/mediafiles/2022/09/20/fbb57de7001cccf1e28fbe34c7506ddc.png', NULL, '2022-09-20 21:55:25', NULL, '1', '', '002003', NULL, 4355);

-- ----------------------------
-- Table structure for media_process
-- ----------------------------
DROP TABLE IF EXISTS `media_process`;
CREATE TABLE `media_process`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_id` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件标识',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
  `bucket` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储桶',
  `file_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储路径',
  `status` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态,1:未处理，2：处理成功  3处理失败',
  `create_date` datetime NOT NULL COMMENT '上传时间',
  `finish_date` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '媒资文件访问地址',
  `errormsg` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_fileid`(`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_process
-- ----------------------------

-- ----------------------------
-- Table structure for media_process_history
-- ----------------------------
DROP TABLE IF EXISTS `media_process_history`;
CREATE TABLE `media_process_history`  (
  `id` bigint NOT NULL,
  `file_id` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件标识',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
  `bucket` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储源',
  `status` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态,1:未处理，2：处理成功  3处理失败',
  `create_date` datetime NOT NULL COMMENT '上传时间',
  `finish_date` datetime NOT NULL COMMENT '完成时间',
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '媒资文件访问地址',
  `file_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `errormsg` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '失败原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_process_history
-- ----------------------------
INSERT INTO `media_process_history` VALUES (1, '1f229319d6fed3431d2f9d06193a433b', '01-分布式事务专题课程介绍.avi', 'video', '2', '2022-09-14 18:30:02', '2022-09-14 20:32:12', 'video1/f/1f229319d6fed3431d2f9d06193a433b/1f229319d6fed3431d2f9d06193a433b.mp4', '1/f/1f229319d6fed3431d2f9d06193a433b/1f229319d6fed3431d2f9d06193a433b.avi', NULL);
INSERT INTO `media_process_history` VALUES (2, '23f83ae728bd1269eee7ea2236e79644', '16-Nacos配置管理-课程总结.avi', 'video', '2', '2022-09-14 18:21:44', '2022-09-14 20:32:20', 'video2/3/23f83ae728bd1269eee7ea2236e79644/23f83ae728bd1269eee7ea2236e79644.mp4', '2/3/23f83ae728bd1269eee7ea2236e79644/23f83ae728bd1269eee7ea2236e79644.avi', NULL);
INSERT INTO `media_process_history` VALUES (3, '3a5a861d1c745d05166132c47b44f9e4', '01-Nacos配置管理-内容介绍.avi', 'video', '2', '2022-09-14 18:19:24', '2022-09-14 20:33:27', 'video3/a/3a5a861d1c745d05166132c47b44f9e4/3a5a861d1c745d05166132c47b44f9e4.mp4', '3/a/3a5a861d1c745d05166132c47b44f9e4/3a5a861d1c745d05166132c47b44f9e4.avi', NULL);
INSERT INTO `media_process_history` VALUES (4, '6ad24a762f67c18f61966c1b8c55abe6', '07-分布式事务基础理论-BASE理论.avi', 'video', '2', '2022-09-14 18:30:16', '2022-09-14 20:33:46', 'video6/a/6ad24a762f67c18f61966c1b8c55abe6/6ad24a762f67c18f61966c1b8c55abe6.mp4', '6/a/6ad24a762f67c18f61966c1b8c55abe6/6ad24a762f67c18f61966c1b8c55abe6.avi', NULL);
INSERT INTO `media_process_history` VALUES (5, '70a98b4a2fffc89e50b101f959cc33ca', '22-Hmily实现TCC事务-开发bank2的confirm方法.avi', 'video', '2', '2022-09-14 18:30:52', '2022-09-14 20:34:33', 'video7/0/70a98b4a2fffc89e50b101f959cc33ca/70a98b4a2fffc89e50b101f959cc33ca.mp4', '7/0/70a98b4a2fffc89e50b101f959cc33ca/70a98b4a2fffc89e50b101f959cc33ca.avi', NULL);

-- ----------------------------
-- Table structure for mq_message
-- ----------------------------
DROP TABLE IF EXISTS `mq_message`;
CREATE TABLE `mq_message`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息id',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息类型代码',
  `business_key1` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `mq_host` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息队列主机',
  `mq_port` int NOT NULL COMMENT '消息队列端口',
  `mq_virtualhost` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息队列虚拟主机',
  `mq_queue` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '队列名称',
  `inform_num` int UNSIGNED NOT NULL COMMENT '通知次数',
  `state` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '处理状态，0:初始，1:成功',
  `returnfailure_date` datetime NULL DEFAULT NULL COMMENT '回复失败时间',
  `returnsuccess_date` datetime NULL DEFAULT NULL COMMENT '回复成功时间',
  `returnfailure_msg` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复失败内容',
  `inform_date` datetime NULL DEFAULT NULL COMMENT '最近通知时间',
  `stage_state1` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阶段1处理状态, 0:初始，1:成功',
  `stage_state2` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阶段2处理状态, 0:初始，1:成功',
  `stage_state3` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阶段3处理状态, 0:初始，1:成功',
  `stage_state4` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阶段4处理状态, 0:初始，1:成功',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mq_message
-- ----------------------------
INSERT INTO `mq_message` VALUES ('f29a3149-7429-40be-8a4e-9909f32003b0', 'xc.mq.msgsync.coursepub', '111', NULL, NULL, '127.0.0.1', 5607, '/', 'xc.course.publish.queue', 0, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for mq_message_history
-- ----------------------------
DROP TABLE IF EXISTS `mq_message_history`;
CREATE TABLE `mq_message_history`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息id',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息类型代码',
  `business_key1` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `business_key3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联业务信息',
  `mq_host` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息队列主机',
  `mq_port` int NOT NULL COMMENT '消息队列端口',
  `mq_virtualhost` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息队列虚拟主机',
  `mq_queue` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '队列名称',
  `inform_num` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '通知次数',
  `state` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '处理状态，0:初始，1:成功，2:失败',
  `returnfailure_date` datetime NULL DEFAULT NULL COMMENT '回复失败时间',
  `returnsuccess_date` datetime NULL DEFAULT NULL COMMENT '回复成功时间',
  `returnfailure_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复失败内容',
  `inform_date` datetime NULL DEFAULT NULL COMMENT '最近通知时间',
  `stage_state1` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stage_state2` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stage_state3` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `stage_state4` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mq_message_history
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
