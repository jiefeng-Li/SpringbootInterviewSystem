-- 1. 创建核心用户表
drop table if exists `t_user`;
CREATE TABLE `t_user` (
                          `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                          `username` varchar(50) NOT NULL COMMENT '用户名',
                          `phone` varchar(20)  NOT NULL COMMENT '手机号',
                          `password` varchar(255) NOT NULL COMMENT '密码',
                          `role` varchar(50) NOT NULL COMMENT '用户角色(JOB_SEEKER //求职者, RECRUITER//招聘者, COMP_ADMIN//企业管理员, SYS_ADMIN//系统管理员)',
                          `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                          `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
                          `profile` varchar(512) DEFAULT NULL COMMENT '个人简介',
                          `account_status` tinyint NOT NULL DEFAULT '1' COMMENT '账户状态(0禁用,1正常,2锁定)',
                          `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                          `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删,1已删)',
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
                          `company_id` bigint DEFAULT NULL COMMENT '用户所属公司ID',
                          PRIMARY KEY (`user_id`),
                          UNIQUE INDEX idx_phone (`phone`),
                          UNIQUE INDEX idx_username (`username`),
                          UNIQUE INDEX idx_email (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 创建公司基本信息表
CREATE TABLE `t_company` (
    `company_id` bigint NOT NULL AUTO_INCREMENT COMMENT '公司ID',
    `admin_id` bigint NOT NULL COMMENT '企业管理员ID 对应用户表user_id',
    `company_name` varchar(100) NOT NULL COMMENT '公司全称',
    `logo_url` varchar(500) DEFAULT NULL COMMENT 'Logo URL',
    `website` varchar(200) DEFAULT NULL COMMENT '官网',
    `introduction` text COMMENT '公司简介',
    `industry` varchar(100) DEFAULT NULL COMMENT '所属行业',
    `scale` varchar(20) DEFAULT NULL COMMENT '公司规模',
    `city` varchar(50) DEFAULT NULL COMMENT '所在城市',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待审,1正常,2驳回,3禁用)',
    `business_license_url` varchar(500) NOT NULL COMMENT '营业执照URL',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
    PRIMARY KEY (`company_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_industry` (`industry`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司基本信息表';


-- 3. 创建公司资质认证记录表
CREATE TABLE `t_company_certification_record` (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资质记录ID',
   `company_id` bigint NOT NULL COMMENT '公司ID',
   `admin_id` bigint NOT NULL COMMENT '企业管理员ID 对应用户表user_id',
   `contact_name` varchar(50) NOT NULL COMMENT '联系人',
   `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
   `contact_email` varchar(100) NOT NULL COMMENT '联系人邮箱',
   `contact_position` varchar(50) DEFAULT NULL COMMENT '联系人职位',
   `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待审,1通过,2驳回)',
   `review_notes` varchar(500) DEFAULT NULL COMMENT '审核意见',
   `reviewed_by` bigint DEFAULT NULL COMMENT '审核人ID',
   `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
   `reviewed_time` datetime DEFAULT NULL COMMENT '审核时间',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`),
   INDEX `idx_company_id` (`company_id`),
   INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司资质认证记录表';