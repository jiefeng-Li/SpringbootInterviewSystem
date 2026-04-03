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
                          `account_status` tinyint NOT NULL DEFAULT '1' COMMENT '账户状态(0禁用,1正常,2锁定,3注销)',
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

-- 4. 创建招聘者绑定公司的记录表
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

-- 6. 简历主表基本信息
drop table if exists `t_resume`;
CREATE TABLE `t_resume`(
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '简历ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `template_id` int NOT NULL COMMENT '简历模板ID',
    `name` varchar(50) NOT NULL COMMENT '姓名',
    `gender` tinyint NOT NULL COMMENT '性别(0男,1女,2保密)',
    `birthday` date DEFAULT NULL COMMENT '出生日期',
    `phone` varchar(20) NOT NULL COMMENT '手机号',
    `email` varchar(100) NOT NULL COMMENT '邮箱',
    `address` varchar(200) DEFAULT NULL COMMENT '现居地址',
    `avatar` varchar(200) DEFAULT NULL COMMENT '头像',
    `city` varchar(50) DEFAULT NULL COMMENT '期望工作城市',
    `summary` varchar(500) DEFAULT NULL COMMENT '个人简介/求职意向',
    `is_default` tinyint NOT NULL COMMENT '是否默认简历（用于多份简历管理）',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
);
-- 6.1 教育经历表关联简历表
drop table if exists `t_resume_education`;
CREATE TABLE `t_resume_education`(
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resume_id` bigint NOT NULL COMMENT '简历ID',
    `school` varchar(100) NOT NULL COMMENT '学校名称',
    `major` varchar(100) NOT NULL COMMENT '专业',
    `degree` varchar(20) NOT NULL COMMENT '学历',
    `start_date` datetime NOT NULL COMMENT '开始时间',
    `end_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '毕业时间（或至今）',
    `description` TEXT COMMENT '在校经历、荣誉等',
    PRIMARY KEY (`id`),
    INDEX `idx_resume_id` (`resume_id`)
);
-- 6.2 工作经历表关联简历表
drop table if exists `t_resume_experience`;
CREATE TABLE `t_resume_experience`(
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resume_id` bigint NOT NULL COMMENT '简历ID',
    `company` varchar(100) NOT NULL COMMENT '公司名称',
    `position` varchar(100) NOT NULL COMMENT '职位名称',
    `start_date` datetime NOT NULL COMMENT '开始时间',
    `end_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间（或至今）',
    `description` TEXT COMMENT '工作内容、业绩等',
    PRIMARY KEY (`id`),
    INDEX `idx_resume_id` (`resume_id`)
);
-- 6.3 项目经历表关联简历表
drop table if exists `t_resume_project`;
CREATE TABLE `t_resume_project`(
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resume_id` bigint NOT NULL COMMENT '简历ID',
    `name` varchar(100) NOT NULL COMMENT '项目名称',
    `description` TEXT COMMENT '项目描述',
    `start_date` datetime NOT NULL COMMENT '开始时间',
    `end_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间（或至今）',
    PRIMARY KEY (`id`),
    INDEX `idx_resume_id` (`resume_id`)
 );

# 7. 简历投递记录表
drop table if exists `t_job_application`;
CREATE TABLE `t_job_application` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '投递记录ID',
    `company_id` bigint NOT NULL COMMENT '投递的公司',
    `job_position_id` bigint NOT NULL COMMENT '职位ID',
    `user_id` bigint NOT NULL COMMENT '求职者用户ID',
    `resume_id` bigint DEFAULT NULL COMMENT '投递时使用的简历ID',
    `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待处理,1已查看,2初筛通过,3初筛不通过,4面试中,5已发Offer,6已录用,7已淘汰)',
#     `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型(1主动投递,2HR邀请,3内部推荐)',
#     `source` varchar(50) DEFAULT NULL COMMENT '来源渠道',
    `cover_letter` text COMMENT '求职信',
    `remarks` text COMMENT 'HR备注',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_job_position_id` (`job_position_id`),
    kEY `idx_company_id` (`company_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_apply_time` (`apply_time`),
    KEY `idx_job_user` (`job_position_id`, `user_id`) -- 联合索引，加速查重
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历投递记录表';

# 8. 聊天记录表
drop table if exists `t_chat_message`;
CREATE TABLE `t_chat_message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `send_id` bigint NOT NULL COMMENT '发送者ID',
    `receive_id` bigint NOT NULL COMMENT '接收者ID',
    `content` text NOT NULL COMMENT '消息内容',
    `timestamp` bigint NOT NULL COMMENT '客户端发送时间戳',
    `sent_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '服务端发送时间',
    `read_time` datetime DEFAULT NULL COMMENT '已读时间',
    `revoke_time` datetime DEFAULT NULL COMMENT '撤回时间',
    `msg_type` tinyint NOT NULL DEFAULT '0' COMMENT '消息类型(0文本,1系统消息)',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '消息状态(未读0,已读1,撤回2)',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_send_id` (`send_id`),
    KEY `idx_receive_id` (`receive_id`),
    KEY `idx_timestamp` (`timestamp`),
    KEY `idx_unread` (`receive_id`, `status`, `is_deleted`, `sent_time`),
    KEY `idx_dialog_sr` (`send_id`, `receive_id`, `sent_time`),
    KEY `idx_dialog_rs` (`receive_id`, `send_id`, `sent_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';

# 9. 面试通知表
drop table if exists `t_interview_notice`;
CREATE TABLE `t_interview_notice` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `job_application_id` bigint NOT NULL COMMENT '投递记录ID',
    `interview_time` datetime NOT NULL COMMENT '面试时间',
    `interview_address` varchar(255) NOT NULL COMMENT '面试地址',
    `comment` varchar(255) DEFAULT NULL COMMENT '备注',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_job_application_id` (`job_application_id`),
    KEY `idx_interview_time` (`interview_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试通知表';

# 10. Offer记录表
drop table if exists `t_offer_record`;
CREATE TABLE `t_offer_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `job_application_id` bigint NOT NULL COMMENT '投递记录ID',
    `company_id` bigint NOT NULL COMMENT '公司ID',
    `job_seeker_id` bigint NOT NULL COMMENT '求职者用户ID',
    `recruiter_id` bigint NOT NULL COMMENT '发送Offer的招聘者ID',
    `interview_notice_id` bigint DEFAULT NULL COMMENT '关联面试通知ID',
    `job_position_id` bigint NOT NULL COMMENT '职位ID',
    `expected_entry_date` date DEFAULT NULL COMMENT '预计入职日期',
    `salary_monthly` decimal(10,2) DEFAULT NULL COMMENT '税前月薪',
    `salary_month_count` tinyint DEFAULT NULL COMMENT '年薪月数（如13薪）',
    `probation_months` tinyint DEFAULT NULL COMMENT '试用期（月）',
    `offer_deadline` datetime NOT NULL COMMENT 'Offer确认截止时间',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待确认,1已接受,2已拒绝,3已过期,4已撤回,5已取消)',
    `welfare` varchar(500) DEFAULT NULL COMMENT '福利说明',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
    `respond_time` datetime DEFAULT NULL COMMENT '候选人响应时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_job_application_id` (`job_application_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_job_seeker_id` (`job_seeker_id`),
    KEY `idx_recruiter_id` (`recruiter_id`),
    KEY `idx_job_position_id` (`job_position_id`),
    KEY `idx_offer_deadline` (`offer_deadline`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Offer记录表';


INSERT INTO `t_job_position` (`company_id`, `title`, `description`, `requirement`, `work_city`, `job_type`, `tags`, `min_salary`, `max_salary`, `experience`, `education`, `headcount`, `hiring_manager_id`, `status`, `publish_time`, `view_count`, `apply_count`, `is_deleted`) VALUES
(1, 'Java高级开发工程师', '负责核心业务系统的架构设计与开发工作，参与技术方案调研和选型，解决开发过程中的技术难题，指导初中级开发工程师。', '1、本科及以上学历，计算机相关专业，5年以上Java开发经验；\n2、精通Java语言，熟悉JVM原理，熟悉Spring Boot/Cloud等主流框架；\n3、熟悉分布式系统设计，有高并发、高性能系统开发经验；\n4、具备良好的团队协作能力和沟通能力。', '北京', 0, '["技术大牛","五险一金","16薪"]', 30000, 45000, '5-10年', '本科', 3, 5, 1, '2024-01-15 10:00:00', 1250, 48, 0),
(1, '前端开发工程师', '负责公司产品Web前端开发，与后端工程师协作完成数据交互，优化前端性能，提升用户体验。', '1、3年以上前端开发经验，本科及以上学历；\n2、精通HTML5/CSS3/JavaScript，熟悉Vue或React框架；\n3、熟悉前端工程化，有Webpack/Vite等工具使用经验；\n4、有移动端开发经验者优先。', '上海', 0, '["Vue","React","双休"]', 18000, 28000, '3-5年', '本科', 2, 6, 1, '2024-02-20 09:30:00', 890, 32, 0),
(1, '产品经理', '负责招聘SaaS产品的规划与设计，撰写PRD文档，协调研发、设计、运营等团队推进产品迭代，跟踪分析产品数据。', '1、3年以上ToB产品经验，有HR领域经验者优先；\n2、熟练使用Axure/XMind等工具，具备优秀的文档能力；\n3、逻辑清晰，沟通能力强，具备项目管理意识；\n4、全日制本科及以上学历。', '深圳', 0, '["SaaS","Axure","弹性工作"]', 20000, 35000, '3-5年', '本科', 1, 7, 1, '2024-03-01 14:00:00', 620, 26, 0),
(1, 'UI设计师', '负责公司Web和移动端产品的界面设计，制定设计规范，跟进开发还原度，参与用户研究。', '1、3年以上UI设计经验，美术或设计相关专业；\n2、熟练使用Figma/Sketch/PS等设计工具；\n3、有完整的项目设计经验，作品集优秀；\n4、具备良好的审美能力和沟通能力。', '杭州', 0, '["Figma","插画","14薪"]', 15000, 25000, '3-5年', '本科', 2, 6, 1, '2024-01-10 11:20:00', 430, 21, 0),
(1, '销售经理', '负责公司SaaS产品的销售工作，开拓新客户，维护老客户关系，完成销售目标，参与行业展会。', '1、3年以上B2B销售经验，有软件行业背景；\n2、具备良好的商务谈判能力和抗压能力；\n3、有丰富的客户资源者优先；\n4、大专及以上学历。', '广州', 0, '["B2B","高提成","六险一金"]', 12000, 24000, '3-5年', '大专', 5, 5, 1, '2024-02-15 09:00:00', 760, 45, 0),
(1, '数据分析师', '负责业务数据分析体系建设，搭建数据看板，深入分析用户行为数据，为业务决策提供数据支持。', '1、3年以上数据分析经验，统计学、数学或计算机专业；\n2、熟练使用SQL/Python，掌握Tableau/FineBI等工具；\n3、熟悉常用数据分析方法和模型；\n4、本科及以上学历。', '北京', 0, '["SQL","Python","Tableau"]', 20000, 30000, '3-5年', '本科', 2, 7, 1, '2024-03-05 16:00:00', 520, 19, 0),
(1, '测试开发工程师', '负责自动化测试框架开发，编写测试用例，执行功能测试、性能测试，把控产品质量。', '1、3年以上测试或开发经验；\n2、熟悉Java/Python，有自动化测试经验；\n3、熟悉Linux操作系统和数据库；\n4、有性能测试经验者优先。', '成都', 0, '["自动化","性能测试","双休"]', 15000, 25000, '3-5年', '本科', 2, 6, 1, '2024-02-01 13:30:00', 380, 16, 0),
(1, '招聘专员', '负责技术岗位的招聘工作，发布职位、筛选简历、组织面试、offer沟通，维护招聘渠道。', '1、2年以上招聘经验，有IT行业招聘背景；\n2、熟悉各种招聘渠道，具备人才寻访能力；\n3、沟通能力强，结果导向；\n4、人力资源或心理学专业优先。', '上海', 0, '["招聘","沟通能力强","五险一金"]', 10000, 18000, '1-3年', '本科', 1, 7, 1, '2024-03-10 10:30:00', 340, 28, 0),
(1, '运维工程师', '负责公司服务器和应用的日常运维，监控系统状态，处理故障，优化系统性能，保障服务稳定性。', '1、3年以上Linux运维经验；\n2、熟悉Shell/Python脚本，熟悉Docker/K8s；\n3、熟悉常见的监控工具Zabbix/Prometheus；\n4、有AWS/阿里云使用经验者优先。', '北京', 0, '["Docker","K8s","阿里云"]', 18000, 28000, '3-5年', '本科', 2, 5, 1, '2024-01-20 15:45:00', 290, 12, 0),
(1, '市场运营', '负责公司品牌推广和内容运营，策划线上线下活动，撰写宣传文案，运营公众号和社交媒体。', '1、2年以上市场运营经验；\n2、具备优秀的文案能力和创意策划能力；\n3、熟悉各类社交媒体运营；\n4、本科及以上学历，新闻、广告专业优先。', '深圳', 0, '["内容运营","活动策划","弹性工作"]', 12000, 20000, '1-3年', '本科', 1, 6, 1, '2024-02-25 11:00:00', 260, 17, 0),
(1, '兼职Java讲师', '负责周末线上Java培训课程讲授，准备课件，辅导学员完成项目，参与课程优化。', '1、5年以上Java开发经验；\n2、表达能力强，有教学或培训经验；\n3、熟悉主流技术栈；\n4、本科及以上学历。', '远程', 1, '["培训","周末","远程授课"]', 500, 800, '5-10年', '本科', 3, 5, 1, '2024-03-01 09:00:00', 180, 9, 0),
(1, 'UI设计实习生', '协助完成产品界面设计，参与设计规范的制定，跟进设计实现，配合完成其他设计工作。', '1、视觉传达或设计相关专业在校生；\n2、熟练使用Figma/PS等设计软件；\n3、每周保证4天以上出勤；\n4、投递需附带作品。', '上海', 2, '["Figma","可转正","餐补"]', 2000, 3000, '在校生', '本科', 2, 7, 1, '2024-03-01 10:00:00', 450, 32, 0),
(1, '前端开发实习生', '参与公司项目的前端开发，配合完成页面实现和功能调试，学习新技术和框架。', '1、计算机相关专业大三或研二；\n2、熟悉HTML/CSS/JavaScript，了解Vue；\n3、学习能力强，有团队协作意识；\n4、每周出勤4天以上。', '杭州', 2, '["Vue","导师制","双休"]', 2500, 3500, '在校生', '本科', 3, 7, 1, '2024-03-05 14:00:00', 320, 24, 0),
(1, '远程Python开发', '参与公司内部工具开发，负责脚本编写和数据处理，远程协作完成任务，定期同步进度。', '1、3年以上Python开发经验；\n2、熟悉Django/Flask框架；\n3、有良好的自我管理能力；\n4、能保证每天4小时以上的协作时间。', '远程', 3, '["Python","远程","弹性时间"]', 15000, 22000, '3-5年', '本科', 1, 6, 1, '2024-02-10 15:00:00', 220, 8, 0),
(1, '项目经理', '负责项目全流程管理，制定项目计划，把控进度和质量，协调各方资源，控制项目风险。', '1、5年以上项目管理经验，有PMP证书优先；\n2、熟悉敏捷开发流程；\n3、优秀的沟通和协调能力；\n4、本科及以上学历。', '北京', 0, '["PMP","敏捷开发","项目管理"]', 30000, 45000, '5-10年', '本科', 1, 5, 1, '2024-01-08 16:30:00', 340, 13, 0),
(1, '算法工程师', '负责推荐系统和搜索排序算法优化，处理海量数据，提升算法效果，跟踪前沿技术。', '1、硕士及以上学历，计算机相关专业；\n2、熟悉机器学习常用算法，有推荐/广告经验；\n3、熟练使用TensorFlow/PyTorch；\n4、良好的工程实现能力。', '深圳', 0, '["机器学习","推荐系统","TensorFlow"]', 35000, 50000, '3-5年', '硕士', 2, 6, 1, '2024-02-18 13:20:00', 620, 21, 0),
(1, '信息安全工程师', '负责公司安全体系建设，进行渗透测试和安全评估，处理安全事件，制定安全规范。', '1、3年以上安全相关经验；\n2、熟悉常见安全漏洞原理和修复方案；\n3、有CISP/CISSP证书优先；\n4、本科及以上学历。', '成都', 0, '["渗透测试","CISP","安全加固"]', 20000, 35000, '3-5年', '本科', 1, 6, 2, '2024-01-25 11:40:00', 180, 7, 0),
(1, '客服专员', '负责处理用户咨询和投诉，解答产品使用问题，收集用户反馈，维护客户关系。', '1、1年以上客服经验；\n2、耐心细致，沟通表达能力强；\n3、熟练使用办公软件；\n4、大专及以上学历。', '广州', 0, '["客服","五险一金","年终奖"]', 6000, 9000, '1年以下', '大专', 4, 7, 3, '2024-02-05 09:30:00', 190, 15, 0),
(1, '财务经理', '负责公司财务管理工作，制定财务制度，组织会计核算，编制财务报表，进行税务筹划。', '1、8年以上财务工作经验，3年以上管理经验；\n2、有中级会计师及以上职称；\n3、熟悉国家财税法规；\n4、本科及以上学历，财务相关专业。', '北京', 0, '["中级会计师","税务筹划","CPA优先"]', 25000, 40000, '10年以上', '本科', 1, 5, 4, '2024-01-18 14:30:00', 120, 4, 0),
(1, '行政助理', '负责日常行政事务，包括来访接待、办公用品管理、会议安排、文件整理等。', '1、1年以上行政工作经验；\n2、熟练使用Office办公软件；\n3、工作细致，有服务意识；\n4、大专及以上学历。', '上海', 0, '["行政","双休","带薪年假"]', 7000, 10000, '1-3年', '大专', 1, 7, 1, '2024-03-08 10:00:00', 160, 11, 0);


