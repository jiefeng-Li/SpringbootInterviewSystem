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
                          INDEX idx_phone (`phone`),
                          INDEX idx_username (`username`),
                          INDEX idx_email (`email`)
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
#     `longitude`  DECIMAL(10, 7) NULL COMMENT '经度', 后续扩展
#     `latitude` DECIMAL(10, 7) NULL COMMENT '纬度', 后续扩展
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待审,1正常,2禁用,3注销)',
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
   `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待审,1通过,2驳回,3取消)',
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

-- 4. 创建求职者绑定公司的记录表
CREATE TABLE `t_binding_request` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请记录ID',
     `user_id` bigint NOT NULL COMMENT '申请人ID（招聘者）',
     `company_id` bigint NOT NULL COMMENT '目标公司ID',
     `application_notes` text COMMENT '申请备注',
     `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待审,1通过,2拒绝,3取消)',
     `reviewed_by` bigint DEFAULT NULL COMMENT '审核人ID（公司管理员）',
     `review_notes` text COMMENT '审核意见',
     `reviewed_time` datetime DEFAULT NULL COMMENT '审核时间',
     `expires_at` datetime NOT NULL COMMENT '申请失效时间',
     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`),
     INDEX `idx_applicant_id` (`user_id`),
     INDEX `idx_company_id` (`company_id`),
     INDEX `idx_status` (`status`),
     INDEX `idx_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘者绑定公司申请记录表';


-- 5. 创建核心职位表
drop table if exists `t_job_position`;
CREATE TABLE `t_job_position` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `company_id` bigint NOT NULL COMMENT '发布公司',
    `title` varchar(200) NOT NULL COMMENT '职位标题',
    `description` longtext NOT NULL COMMENT '职位描述',
    `requirement` longtext NOT NULL COMMENT '职位要求',
    `work_city` varchar(50) NOT NULL COMMENT '工作城市',
    `job_type` tinyint NOT NULL DEFAULT '0 ' COMMENT '工作性质(0全职,1兼职,2实习,3远程 )',
    `tags` varchar(512)  NULL COMMENT '职位标签{JSON数组}',
    `min_salary` int DEFAULT NULL COMMENT '最低薪资',
    `max_salary` int DEFAULT NULL COMMENT '最高薪资',
    `experience` varchar(20) DEFAULT NULL COMMENT '经验要求',
    `education` varchar(20) DEFAULT NULL COMMENT '学历要求',
    `headcount` int NOT NULL DEFAULT '1' COMMENT '招聘人数',
    `hiring_manager_id` bigint DEFAULT NULL COMMENT '负责人',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '职位状态(0草稿,1招聘中,2已暂停,3已招满,4已关闭)',
    `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
    `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
    `apply_count` int NOT NULL DEFAULT '0' COMMENT '投递量',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_company_id` (`company_id`),
    INDEX `idx_status_publish` (`status`, `publish_time` DESC),
    INDEX `x_city_job_type` (`work_city`, `job_type`),
    INDEX `idx_hiring_manager` (`hiring_manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位发布表';