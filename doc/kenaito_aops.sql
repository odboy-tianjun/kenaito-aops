/*
 Navicat Premium Data Transfer

 Source Server         : localhost101
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : 192.168.235.102:3308
 Source Schema         : kenaito_aops

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 06/05/2025 15:34:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `blob_data` blob NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `calendar` blob NOT NULL,
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cron_expression` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `fired_time` bigint(0) NOT NULL,
  `sched_time` bigint(0) NOT NULL,
  `priority` int(0) NOT NULL,
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE,
  INDEX `idx_qrtz_ft_trig_inst_name`(`sched_name`, `instance_name`) USING BTREE,
  INDEX `idx_qrtz_ft_inst_job_req_rcvry`(`sched_name`, `instance_name`, `requests_recovery`) USING BTREE,
  INDEX `idx_qrtz_ft_j_g`(`sched_name`, `job_name`, `job_group`) USING BTREE,
  INDEX `idx_qrtz_ft_jg`(`sched_name`, `job_group`) USING BTREE,
  INDEX `idx_qrtz_ft_t_g`(`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `idx_qrtz_ft_tg`(`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_data` blob NULL,
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE,
  INDEX `idx_qrtz_j_req_recovery`(`sched_name`, `requests_recovery`) USING BTREE,
  INDEX `idx_qrtz_j_grp`(`sched_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------
INSERT INTO `qrtz_locks` VALUES ('ClusteredScheduler', 'STATE_ACCESS');
INSERT INTO `qrtz_locks` VALUES ('ClusteredScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `last_checkin_time` bigint(0) NOT NULL,
  `checkin_interval` bigint(0) NOT NULL,
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------
INSERT INTO `qrtz_scheduler_state` VALUES ('ClusteredScheduler', 'DESKTOP-FM44BLS1742558233429', 1742558255052, 20000);

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `repeat_count` bigint(0) NOT NULL,
  `repeat_interval` bigint(0) NOT NULL,
  `times_triggered` bigint(0) NOT NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `int_prop_1` int(0) NULL DEFAULT NULL,
  `int_prop_2` int(0) NULL DEFAULT NULL,
  `long_prop_1` bigint(0) NULL DEFAULT NULL,
  `long_prop_2` bigint(0) NULL DEFAULT NULL,
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL,
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL,
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `next_fire_time` bigint(0) NULL DEFAULT NULL,
  `prev_fire_time` bigint(0) NULL DEFAULT NULL,
  `priority` int(0) NULL DEFAULT NULL,
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `start_time` bigint(0) NOT NULL,
  `end_time` bigint(0) NULL DEFAULT NULL,
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `misfire_instr` smallint(0) NULL DEFAULT NULL,
  `job_data` blob NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `idx_qrtz_t_j`(`sched_name`, `job_name`, `job_group`) USING BTREE,
  INDEX `idx_qrtz_t_jg`(`sched_name`, `job_group`) USING BTREE,
  INDEX `idx_qrtz_t_c`(`sched_name`, `calendar_name`) USING BTREE,
  INDEX `idx_qrtz_t_g`(`sched_name`, `trigger_group`) USING BTREE,
  INDEX `idx_qrtz_t_state`(`sched_name`, `trigger_state`) USING BTREE,
  INDEX `idx_qrtz_t_n_state`(`sched_name`, `trigger_name`, `trigger_group`, `trigger_state`) USING BTREE,
  INDEX `idx_qrtz_t_n_g_state`(`sched_name`, `trigger_group`, `trigger_state`) USING BTREE,
  INDEX `idx_qrtz_t_next_fire_time`(`sched_name`, `next_fire_time`) USING BTREE,
  INDEX `idx_qrtz_t_nft_st`(`sched_name`, `trigger_state`, `next_fire_time`) USING BTREE,
  INDEX `idx_qrtz_t_nft_misfire`(`sched_name`, `misfire_instr`, `next_fire_time`) USING BTREE,
  INDEX `idx_qrtz_t_nft_st_misfire`(`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_state`) USING BTREE,
  INDEX `idx_qrtz_t_nft_st_misfire_grp`(`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_group`, `trigger_state`) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for system_dept
-- ----------------------------
DROP TABLE IF EXISTS `system_dept`;
CREATE TABLE `system_dept`  (
  `dept_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pid` bigint(0) NULL DEFAULT NULL COMMENT '上级部门',
  `sub_count` int(0) NULL DEFAULT 0 COMMENT '子部门数目',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `dept_sort` int(0) NULL DEFAULT 999 COMMENT '排序',
  `enabled` bit(1) NOT NULL COMMENT '状态',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `idx_pid`(`pid`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE,
  INDEX `idx_sys_dept_dept_id`(`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_dept
-- ----------------------------
INSERT INTO `system_dept` VALUES (2, 7, 1, '研发部', 3, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dept` VALUES (5, 7, 0, '运维部', 4, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dept` VALUES (6, 8, 0, '测试部', 6, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dept` VALUES (7, NULL, 2, '华南分部', 0, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dept` VALUES (8, NULL, 2, '华北分部', 1, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dept` VALUES (15, 8, 0, 'UI部门', 7, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dept` VALUES (17, 2, 0, '研发一组', 999, b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_dict
-- ----------------------------
DROP TABLE IF EXISTS `system_dict`;
CREATE TABLE `system_dict`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_dict
-- ----------------------------
INSERT INTO `system_dict` VALUES (1, 'user_status', '用户状态', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict` VALUES (4, 'dept_status', '部门状态', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict` VALUES (5, 'job_status', '岗位状态', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_dict_detail
-- ----------------------------
DROP TABLE IF EXISTS `system_dict_detail`;
CREATE TABLE `system_dict_detail`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dict_id` bigint(0) NULL DEFAULT NULL COMMENT '字典id',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典标签',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典值',
  `dict_sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_id`(`dict_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据字典详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_dict_detail
-- ----------------------------
INSERT INTO `system_dict_detail` VALUES (1, 1, '激活', 'true', 1, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict_detail` VALUES (2, 1, '禁用', 'false', 2, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict_detail` VALUES (3, 4, '启用', 'true', 1, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict_detail` VALUES (4, 4, '停用', 'false', 2, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict_detail` VALUES (5, 5, '启用', 'true', 1, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_dict_detail` VALUES (6, 5, '停用', 'false', 2, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_job
-- ----------------------------
DROP TABLE IF EXISTS `system_job`;
CREATE TABLE `system_job`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `enabled` bit(1) NOT NULL COMMENT '岗位状态',
  `job_sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_job
-- ----------------------------
INSERT INTO `system_job` VALUES (8, '人事专员', b'1', 3, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_job` VALUES (10, '产品经理', b'1', 4, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_job` VALUES (11, '全栈开发', b'1', 2, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_job` VALUES (12, '软件测试', b'1', 5, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_menu
-- ----------------------------
DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu`  (
  `menu_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pid` bigint(0) NULL DEFAULT NULL COMMENT '上级菜单ID',
  `sub_count` int(0) NULL DEFAULT 0 COMMENT '子菜单数目',
  `type` int(0) NULL DEFAULT NULL COMMENT '菜单类型',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单标题',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件名称',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件',
  `menu_sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '链接地址',
  `i_frame` bit(1) NULL DEFAULT NULL COMMENT '是否外链',
  `cache` bit(1) NULL DEFAULT b'0' COMMENT '缓存',
  `hidden` bit(1) NULL DEFAULT b'0' COMMENT '隐藏',
  `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE,
  UNIQUE INDEX `uniq_title`(`title`) USING BTREE,
  INDEX `idx_pid`(`pid`) USING BTREE,
  INDEX `idx_sys_menu_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_menu
-- ----------------------------
INSERT INTO `system_menu` VALUES (1, NULL, 7, 0, '系统管理', NULL, NULL, 1, 'system', 'system', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (2, 1, 3, 1, '用户管理', 'User', 'system/user/index', 2, 'peoples', 'user', b'0', b'0', b'0', 'user:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (3, 1, 3, 1, '角色管理', 'Role', 'system/role/index', 3, 'role', 'role', b'0', b'0', b'0', 'roles:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (5, 1, 3, 1, '菜单管理', 'Menu', 'system/menu/index', 5, 'menu', 'menu', b'0', b'0', b'0', 'menu:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (6, NULL, 3, 0, '系统监控', NULL, NULL, 10, 'monitor', 'monitor', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (9, 6, 0, 1, 'SQL监控', 'Sql', 'monitor/sql/index', 18, 'sqlMonitor', 'druid', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (10, NULL, 12, 0, '组件管理', NULL, NULL, 50, 'zujian', 'components', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (11, 10, 0, 1, '图标库', 'Icons', 'components/icons/index', 51, 'icon', 'icon', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (14, 36, 0, 1, '邮件工具', 'Email', 'tools/email/index', 35, 'email', 'email', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (15, 10, 0, 1, '富文本', 'Editor', 'components/base/Editor', 52, 'fwb', 'tinymce', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (18, 36, 3, 1, '存储管理', 'Storage', 'tools/storage/index', 34, 'qiniu', 'storage', b'0', b'0', b'0', 'storage:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (21, NULL, 2, 0, '多级菜单', NULL, '', 900, 'menu', 'nested', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (22, 21, 2, 0, '二级菜单1', NULL, '', 999, 'menu', 'menu1', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (23, 21, 0, 1, '二级菜单2', NULL, 'nested/menu2/index', 999, 'menu', 'menu2', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (24, 22, 0, 1, '三级菜单1', 'Test', 'nested/menu1/menu1-1', 999, 'menu', 'menu1-1', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (27, 22, 0, 1, '三级菜单2', NULL, 'nested/menu1/menu1-2', 999, 'menu', 'menu1-2', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (28, 1, 3, 1, '任务调度', 'Timing', 'system/timing/index', 999, 'timing', 'timing', b'0', b'0', b'0', 'timing:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (33, 10, 0, 1, 'Markdown', 'Markdown', 'components/base/MarkDown', 53, 'markdown', 'markdown', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (34, 10, 0, 1, 'Yaml编辑器', 'YamlEdit', 'components/base/YamlEdit', 54, 'dev', 'yaml', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (35, 1, 3, 1, '部门管理', 'Dept', 'system/dept/index', 6, 'dept', 'dept', b'0', b'0', b'0', 'dept:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (36, NULL, 2, 0, '系统工具', NULL, '', 30, 'sys-tools', 'sys-tools', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (37, 1, 3, 1, '岗位管理', 'Job', 'system/job/index', 7, 'Steve-Jobs', 'job', b'0', b'0', b'0', 'job:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (39, 1, 3, 1, '字典管理', 'Dict', 'system/dict/index', 8, 'dictionary', 'dict', b'0', b'0', b'0', 'dict:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (41, 6, 0, 1, '在线用户', 'OnlineUser', 'monitor/online/index', 10, 'Steve-Jobs', 'online', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (44, 2, 0, 2, '用户新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'user:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (45, 2, 0, 2, '用户编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'user:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (46, 2, 0, 2, '用户删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'user:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (48, 3, 0, 2, '角色创建', NULL, '', 2, '', '', b'0', b'0', b'0', 'roles:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (49, 3, 0, 2, '角色修改', NULL, '', 3, '', '', b'0', b'0', b'0', 'roles:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (50, 3, 0, 2, '角色删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'roles:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (52, 5, 0, 2, '菜单新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'menu:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (53, 5, 0, 2, '菜单编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'menu:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (54, 5, 0, 2, '菜单删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'menu:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (56, 35, 0, 2, '部门新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'dept:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (57, 35, 0, 2, '部门编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'dept:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (58, 35, 0, 2, '部门删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'dept:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (60, 37, 0, 2, '岗位新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'job:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (61, 37, 0, 2, '岗位编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'job:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (62, 37, 0, 2, '岗位删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'job:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (64, 39, 0, 2, '字典新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'dict:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (65, 39, 0, 2, '字典编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'dict:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (66, 39, 0, 2, '字典删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'dict:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (73, 28, 0, 2, '任务新增', NULL, '', 2, '', '', b'0', b'0', b'0', 'timing:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (74, 28, 0, 2, '任务编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'timing:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (75, 28, 0, 2, '任务删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'timing:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (77, 18, 0, 2, '上传文件', NULL, '', 2, '', '', b'0', b'0', b'0', 'storage:add', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (78, 18, 0, 2, '文件编辑', NULL, '', 3, '', '', b'0', b'0', b'0', 'storage:edit', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (79, 18, 0, 2, '文件删除', NULL, '', 4, '', '', b'0', b'0', b'0', 'storage:del', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (80, 6, 0, 1, '服务监控', 'ServerMonitor', 'monitor/server/index', 14, 'codeConsole', 'server', b'0', b'0', b'0', 'monitor:list', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (83, 10, 0, 1, '图表库', 'Echarts', 'components/base/Echarts', 50, 'chart', 'echarts', b'0', b'1', b'0', '', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (117, 10, 0, 1, '一键复制', 'clipboardDemo', 'components/base/ClipboardDemo', 999, 'menu', 'clipboardDemo', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (118, 10, 0, 1, 'Java代码编辑器', 'javaCodemirror', 'components/base/CodemirrorDemo', 999, 'menu', 'javaCodemirror', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (119, 10, 0, 1, '数字滚动', 'countToDemo', 'components/base/CountToDemo', 999, 'menu', 'countToDemo', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (120, 10, 0, 1, '拖拽表格', 'dragTableDemo', 'components/base/DragTableDemo', 999, 'menu', 'dragTableDemo', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (121, 10, 0, 1, '分割面板', 'splitPaneDemo', 'components/base/SplitPaneDemo', 999, 'menu', 'splitPaneDemo', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (122, 10, 0, 1, 'WebSocket', 'webSocketDemo', 'components/base/WebSocketDemo', 999, 'menu', 'webSocketDemo', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_menu` VALUES (125, 10, 0, 1, '业务组件Demo', 'DemoShow', 'components/business/DemoShow', 999, 'menu', 'bussinessDemo', b'0', b'0', b'0', NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_quartz_job
-- ----------------------------
DROP TABLE IF EXISTS `system_quartz_job`;
CREATE TABLE `system_quartz_job`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Spring Bean名称',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cron 表达式',
  `is_pause` bit(1) NULL DEFAULT NULL COMMENT '状态：1暂停、0启用',
  `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名称',
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `person_in_charge` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报警邮箱',
  `sub_task` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子任务ID',
  `pause_after_failure` bit(1) NULL DEFAULT NULL COMMENT '任务失败后是否暂停',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_is_pause`(`is_pause`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_quartz_job
-- ----------------------------
INSERT INTO `system_quartz_job` VALUES (2, 'testTask', '0/5 * * * * ?', b'1', '测试1', 'run1', 'test', '带参测试，多参使用json', '测试', NULL, NULL, NULL, 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_quartz_job` VALUES (3, 'testTask', '0/5 * * * * ?', b'1', '测试', 'run', '', '不带参测试', 'Zheng Jie', '', '6', b'1', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_quartz_log
-- ----------------------------
DROP TABLE IF EXISTS `system_quartz_log`;
CREATE TABLE `system_quartz_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bean_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Bean名称',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cron 表达式',
  `is_success` bit(1) NULL DEFAULT NULL COMMENT '是否执行成功',
  `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名称',
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `time` bigint(0) NULL DEFAULT NULL COMMENT '执行耗时',
  `exception_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常详情',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_quartz_log
-- ----------------------------

-- ----------------------------
-- Table structure for system_role
-- ----------------------------
DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role`  (
  `role_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `level` int(0) NULL DEFAULT NULL COMMENT '角色级别',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `data_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据权限',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE,
  INDEX `idx_level`(`level`) USING BTREE,
  INDEX `idx_sys_role_level`(`level`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_role
-- ----------------------------
INSERT INTO `system_role` VALUES (1, '超级管理员', 1, '666', '全部', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_role` VALUES (2, '普通用户', 2, '-', '本级', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_role` VALUES (7, 'test', 3, 'testtesttesttesttesttesttesttesttest', '全部', 'admin', 'admin', '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_roles_depts
-- ----------------------------
DROP TABLE IF EXISTS `system_roles_depts`;
CREATE TABLE `system_roles_depts`  (
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_sys_roles_depts_role_dept`(`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色部门关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_roles_depts
-- ----------------------------

-- ----------------------------
-- Table structure for system_roles_menus
-- ----------------------------
DROP TABLE IF EXISTS `system_roles_menus`;
CREATE TABLE `system_roles_menus`  (
  `menu_id` bigint(0) NOT NULL COMMENT '菜单ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`menu_id`, `role_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_sys_roles_menus_role_menu`(`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_roles_menus
-- ----------------------------
INSERT INTO `system_roles_menus` VALUES (1, 1);
INSERT INTO `system_roles_menus` VALUES (2, 1);
INSERT INTO `system_roles_menus` VALUES (3, 1);
INSERT INTO `system_roles_menus` VALUES (5, 1);
INSERT INTO `system_roles_menus` VALUES (6, 1);
INSERT INTO `system_roles_menus` VALUES (9, 1);
INSERT INTO `system_roles_menus` VALUES (10, 1);
INSERT INTO `system_roles_menus` VALUES (10, 2);
INSERT INTO `system_roles_menus` VALUES (11, 1);
INSERT INTO `system_roles_menus` VALUES (11, 2);
INSERT INTO `system_roles_menus` VALUES (14, 1);
INSERT INTO `system_roles_menus` VALUES (14, 7);
INSERT INTO `system_roles_menus` VALUES (15, 1);
INSERT INTO `system_roles_menus` VALUES (15, 2);
INSERT INTO `system_roles_menus` VALUES (18, 1);
INSERT INTO `system_roles_menus` VALUES (18, 2);
INSERT INTO `system_roles_menus` VALUES (18, 7);
INSERT INTO `system_roles_menus` VALUES (21, 1);
INSERT INTO `system_roles_menus` VALUES (21, 2);
INSERT INTO `system_roles_menus` VALUES (22, 1);
INSERT INTO `system_roles_menus` VALUES (22, 2);
INSERT INTO `system_roles_menus` VALUES (23, 1);
INSERT INTO `system_roles_menus` VALUES (23, 2);
INSERT INTO `system_roles_menus` VALUES (24, 1);
INSERT INTO `system_roles_menus` VALUES (24, 2);
INSERT INTO `system_roles_menus` VALUES (27, 1);
INSERT INTO `system_roles_menus` VALUES (27, 2);
INSERT INTO `system_roles_menus` VALUES (28, 1);
INSERT INTO `system_roles_menus` VALUES (33, 1);
INSERT INTO `system_roles_menus` VALUES (33, 2);
INSERT INTO `system_roles_menus` VALUES (34, 1);
INSERT INTO `system_roles_menus` VALUES (34, 2);
INSERT INTO `system_roles_menus` VALUES (35, 1);
INSERT INTO `system_roles_menus` VALUES (36, 1);
INSERT INTO `system_roles_menus` VALUES (36, 2);
INSERT INTO `system_roles_menus` VALUES (36, 7);
INSERT INTO `system_roles_menus` VALUES (37, 1);
INSERT INTO `system_roles_menus` VALUES (39, 1);
INSERT INTO `system_roles_menus` VALUES (41, 1);
INSERT INTO `system_roles_menus` VALUES (44, 1);
INSERT INTO `system_roles_menus` VALUES (45, 1);
INSERT INTO `system_roles_menus` VALUES (46, 1);
INSERT INTO `system_roles_menus` VALUES (48, 1);
INSERT INTO `system_roles_menus` VALUES (49, 1);
INSERT INTO `system_roles_menus` VALUES (50, 1);
INSERT INTO `system_roles_menus` VALUES (52, 1);
INSERT INTO `system_roles_menus` VALUES (53, 1);
INSERT INTO `system_roles_menus` VALUES (54, 1);
INSERT INTO `system_roles_menus` VALUES (56, 1);
INSERT INTO `system_roles_menus` VALUES (57, 1);
INSERT INTO `system_roles_menus` VALUES (58, 1);
INSERT INTO `system_roles_menus` VALUES (60, 1);
INSERT INTO `system_roles_menus` VALUES (61, 1);
INSERT INTO `system_roles_menus` VALUES (62, 1);
INSERT INTO `system_roles_menus` VALUES (64, 1);
INSERT INTO `system_roles_menus` VALUES (65, 1);
INSERT INTO `system_roles_menus` VALUES (66, 1);
INSERT INTO `system_roles_menus` VALUES (73, 1);
INSERT INTO `system_roles_menus` VALUES (74, 1);
INSERT INTO `system_roles_menus` VALUES (75, 1);
INSERT INTO `system_roles_menus` VALUES (77, 1);
INSERT INTO `system_roles_menus` VALUES (77, 2);
INSERT INTO `system_roles_menus` VALUES (77, 7);
INSERT INTO `system_roles_menus` VALUES (78, 1);
INSERT INTO `system_roles_menus` VALUES (78, 2);
INSERT INTO `system_roles_menus` VALUES (78, 7);
INSERT INTO `system_roles_menus` VALUES (79, 1);
INSERT INTO `system_roles_menus` VALUES (79, 2);
INSERT INTO `system_roles_menus` VALUES (79, 7);
INSERT INTO `system_roles_menus` VALUES (80, 1);
INSERT INTO `system_roles_menus` VALUES (80, 2);
INSERT INTO `system_roles_menus` VALUES (83, 1);
INSERT INTO `system_roles_menus` VALUES (83, 2);
INSERT INTO `system_roles_menus` VALUES (117, 1);
INSERT INTO `system_roles_menus` VALUES (117, 2);
INSERT INTO `system_roles_menus` VALUES (118, 1);
INSERT INTO `system_roles_menus` VALUES (118, 2);
INSERT INTO `system_roles_menus` VALUES (119, 1);
INSERT INTO `system_roles_menus` VALUES (119, 2);
INSERT INTO `system_roles_menus` VALUES (120, 1);
INSERT INTO `system_roles_menus` VALUES (120, 2);
INSERT INTO `system_roles_menus` VALUES (121, 1);
INSERT INTO `system_roles_menus` VALUES (121, 2);
INSERT INTO `system_roles_menus` VALUES (122, 1);
INSERT INTO `system_roles_menus` VALUES (122, 2);
INSERT INTO `system_roles_menus` VALUES (125, 1);

-- ----------------------------
-- Table structure for system_user
-- ----------------------------
DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user`  (
  `user_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门名称',
  `username` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `avatar_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像真实路径',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `is_admin` bit(1) NULL DEFAULT b'0' COMMENT '是否为admin账号',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '状态：1启用、0禁用',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `pwd_reset_time` datetime(0) NULL DEFAULT NULL COMMENT '修改密码的时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uniq_email`(`email`) USING BTREE,
  UNIQUE INDEX `uniq_username`(`username`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE,
  INDEX `uniq_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_user
-- ----------------------------
INSERT INTO `system_user` VALUES (1, 2, 'admin', '管理员', '男', '18888888888', '1943815081@qq.com', '', '', '$2a$10$Egp1/gvFlt7zhlXVfEFw4OfWQCGPw0ClmMcc6FjTnvXNRVf9zdMRa', b'1', b'1', 'admin', 'admin', '2020-05-03 16:38:31', '2025-01-05 00:00:00', '2025-01-05 00:00:00');
INSERT INTO `system_user` VALUES (3, 2, '测试', '测试', '男', '18888888881', '188888@qq.com', NULL, NULL, '$2a$10$DpAGWp3CYtYySPqJe.CnL.c/OCR0kkPLLAwAyID0yFHLamtYdYsvy', b'0', b'1', 'admin', 'admin', NULL, '2025-01-05 00:00:00', '2025-01-05 00:00:00');

-- ----------------------------
-- Table structure for system_users_jobs
-- ----------------------------
DROP TABLE IF EXISTS `system_users_jobs`;
CREATE TABLE `system_users_jobs`  (
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `job_id` bigint(0) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `job_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_job_id`(`job_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_users_jobs
-- ----------------------------
INSERT INTO `system_users_jobs` VALUES (1, 11);
INSERT INTO `system_users_jobs` VALUES (3, 11);

-- ----------------------------
-- Table structure for system_users_roles
-- ----------------------------
DROP TABLE IF EXISTS `system_users_roles`;
CREATE TABLE `system_users_roles`  (
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_users_roles
-- ----------------------------
INSERT INTO `system_users_roles` VALUES (1, 1);
INSERT INTO `system_users_roles` VALUES (3, 2);

-- ----------------------------
-- Table structure for tool_email_config
-- ----------------------------
DROP TABLE IF EXISTS `tool_email_config`;
CREATE TABLE `tool_email_config`  (
  `config_id` bigint(0) NOT NULL COMMENT 'ID',
  `from_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收件人',
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮件服务器SMTP地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `port` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发件者用户名',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮箱配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tool_email_config
-- ----------------------------

-- ----------------------------
-- Table structure for tool_local_storage
-- ----------------------------
DROP TABLE IF EXISTS `tool_local_storage`;
CREATE TABLE `tool_local_storage`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件真实的名称',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后缀',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  `size` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '大小',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '本地存储' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tool_local_storage
-- ----------------------------
INSERT INTO `tool_local_storage` VALUES (11, 'bg-202504100131576.jpg', 'bg', 'jpg', 'C:\\cutejava\\file\\image\\bg-202504100131576.jpg', 'image', '20.20MB   ', 'admin', 'admin', '2025-04-10 13:32:00', '2025-04-10 13:32:00');

-- ----------------------------
-- Table structure for tool_qiniu_config
-- ----------------------------
DROP TABLE IF EXISTS `tool_qiniu_config`;
CREATE TABLE `tool_qiniu_config`  (
  `config_id` bigint(0) NOT NULL COMMENT 'ID',
  `access_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'accessKey',
  `bucket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Bucket 识别符',
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '外链域名',
  `secret_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'secretKey',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '空间类型',
  `zone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '机房',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '七牛云配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tool_qiniu_config
-- ----------------------------

-- ----------------------------
-- Table structure for tool_qiniu_content
-- ----------------------------
DROP TABLE IF EXISTS `tool_qiniu_content`;
CREATE TABLE `tool_qiniu_content`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bucket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Bucket 识别符',
  `name` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件大小',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型：私有或公开',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件url',
  `suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件后缀',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '上传或同步的时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '七牛云文件存储' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tool_qiniu_content
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
