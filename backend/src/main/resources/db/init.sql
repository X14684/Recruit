-- =====================================================
-- 校园求职招聘系统 数据库初始化脚本
-- 版本：v1.0  日期：2026-03-16
-- =====================================================
CREATE DATABASE IF NOT EXISTS campus_recruitment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_recruitment;

-- ==================== 学院表 ====================
CREATE TABLE IF NOT EXISTS `t_college` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `college_name`  VARCHAR(100) NOT NULL COMMENT '学院名称',
    `college_code`  VARCHAR(50)  COMMENT '学院编码',
    `username`      VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
    `password`      VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
    `director_name` VARCHAR(50)  COMMENT '负责人姓名',
    `phone`         VARCHAR(20)  COMMENT '联系电话',
    `email`         VARCHAR(100) COMMENT '联系邮箱',
    `introduction`  TEXT         COMMENT '学院简介',
    `status`        TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `deleted`       TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- ==================== 学生表 ====================
CREATE TABLE IF NOT EXISTS `t_student` (
    `id`                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_no`         VARCHAR(50)  NOT NULL UNIQUE COMMENT '学号',
    `real_name`          VARCHAR(50)  NOT NULL COMMENT '姓名',
    `username`           VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
    `password`           VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
    `email`              VARCHAR(100) COMMENT '邮箱',
    `phone`              VARCHAR(20)  COMMENT '手机号',
    `gender`             TINYINT DEFAULT 1 COMMENT '0-女 1-男',
    `avatar`             VARCHAR(500) COMMENT '头像URL',
    `college_id`         BIGINT COMMENT '学院ID',
    `major`              VARCHAR(100) COMMENT '专业',
    `grade`              VARCHAR(20)  COMMENT '年级',
    `education`          VARCHAR(20)  DEFAULT '本科' COMMENT '学历',
    `graduation_date`    DATE COMMENT '预计毕业时间',
    `birthday`           DATE COMMENT '出生日期',
    `introduction`       TEXT COMMENT '个人简介',
    `intention_position` VARCHAR(100) COMMENT '求职意向-职位',
    `intention_city`     VARCHAR(50)  COMMENT '求职意向-城市',
    `intention_industry` VARCHAR(100) COMMENT '求职意向-行业',
    `intention_salary`   VARCHAR(50)  COMMENT '求职意向-薪资',
    `resume_url`         VARCHAR(500) COMMENT '简历文件URL',
    `status`             TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `deleted`            TINYINT DEFAULT 0,
    `create_time`        DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- ==================== 企业表 ====================
CREATE TABLE IF NOT EXISTS `t_company` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `company_name`  VARCHAR(100) NOT NULL COMMENT '企业名称',
    `username`      VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
    `password`      VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
    `logo`          VARCHAR(500) COMMENT '企业Logo URL',
    `contact_name`  VARCHAR(50)  COMMENT 'HR联系人',
    `contact_phone` VARCHAR(20)  COMMENT '联系电话',
    `contact_email` VARCHAR(100) COMMENT '联系邮箱',
    `industry`      VARCHAR(50)  COMMENT '所属行业',
    `scale`         VARCHAR(50)  COMMENT '企业规模',
    `nature`        VARCHAR(50)  COMMENT '企业性质（国企/民企/外企等）',
    `address`       VARCHAR(200) COMMENT '注册地址',
    `introduction`  TEXT COMMENT '企业简介',
    `license_url`   VARCHAR(500) COMMENT '营业执照图片URL',
    `audit_status`  TINYINT DEFAULT 0 COMMENT '0-待审核 1-通过 2-拒绝',
    `audit_remark`  VARCHAR(500) COMMENT '审核意见',
    `status`        TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    `deleted`       TINYINT DEFAULT 0,
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';

-- ==================== 管理员表 ====================
CREATE TABLE IF NOT EXISTS `t_admin` (
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username`       VARCHAR(50)  NOT NULL UNIQUE COMMENT '账号',
    `password`       VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
    `real_name`      VARCHAR(50)  COMMENT '真实姓名',
    `phone`          VARCHAR(20)  COMMENT '手机号',
    `email`          VARCHAR(100) COMMENT '邮箱',
    `avatar`         VARCHAR(500) COMMENT '头像',
    `is_super_admin` TINYINT DEFAULT 0 COMMENT '是否超级管理员',
    `status`         TINYINT DEFAULT 1,
    `deleted`        TINYINT DEFAULT 0,
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ==================== 职位表 ====================
CREATE TABLE IF NOT EXISTS `t_job` (
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `company_id`     BIGINT NOT NULL COMMENT '企业ID',
    `job_name`       VARCHAR(100) NOT NULL COMMENT '职位名称',
    `job_type`       VARCHAR(50)  COMMENT '类型（全职/实习/兼职）',
    `work_location`  VARCHAR(100) COMMENT '工作地点',
    `salary_range`   VARCHAR(50)  COMMENT '薪资范围',
    `education_req`  VARCHAR(50)  COMMENT '学历要求',
    `experience_req` VARCHAR(50)  COMMENT '经验要求',
    `recruit_num`    INT DEFAULT 1 COMMENT '招聘人数',
    `description`    TEXT COMMENT '职位描述',
    `requirement`    TEXT COMMENT '任职要求',
    `tags`           VARCHAR(200) COMMENT '标签（JSON）',
    `deadline`       DATE COMMENT '截止日期',
    `audit_status`   TINYINT DEFAULT 0 COMMENT '0-待审核 1-通过 2-拒绝',
    `audit_remark`   VARCHAR(500) COMMENT '审核意见',
    `status`         TINYINT DEFAULT 1 COMMENT '0-关闭 1-招聘中',
    `view_count`     INT DEFAULT 0 COMMENT '浏览量',
    `deleted`        TINYINT DEFAULT 0,
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_company_id` (`company_id`),
    INDEX `idx_audit_status` (`audit_status`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- ==================== 投递记录表 ====================
CREATE TABLE IF NOT EXISTS `t_application` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id`          BIGINT NOT NULL COMMENT '学生ID',
    `job_id`              BIGINT NOT NULL COMMENT '职位ID',
    `company_id`          BIGINT NOT NULL COMMENT '企业ID（冗余，提高查询效率）',
    `resume_url`          VARCHAR(500) COMMENT '投递时简历快照',
    `status`              TINYINT DEFAULT 0 COMMENT '0-已投递 1-已查看 2-初审通过 3-面试邀请 4-已录用 5-不合适',
    `company_remark`      VARCHAR(500) COMMENT '企业备注',
    `interview_time`      DATETIME COMMENT '面试时间',
    `interview_location`  VARCHAR(200) COMMENT '面试地点',
    `deleted`             TINYINT DEFAULT 0,
    `create_time`         DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_student_job` (`student_id`, `job_id`),
    INDEX `idx_company_id` (`company_id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';

-- ==================== 校园宣讲会表 ====================
CREATE TABLE IF NOT EXISTS `t_campus_talk` (
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY,
    `company_id`       BIGINT NOT NULL COMMENT '发起企业ID',
    `college_id`       BIGINT COMMENT '目标学院ID（null=不限）',
    `title`            VARCHAR(200) NOT NULL COMMENT '宣讲会标题',
    `description`      TEXT COMMENT '详细描述',
    `talk_time`        DATETIME COMMENT '举办时间',
    `location`         VARCHAR(200) COMMENT '举办地点',
    `sign_deadline`    DATE COMMENT '报名截止日期',
    `max_sign_num`     INT DEFAULT 200 COMMENT '最大报名人数',
    `current_sign_num` INT DEFAULT 0 COMMENT '当前报名人数',
    `audit_status`     TINYINT DEFAULT 0 COMMENT '0-待审核 1-审核通过 2-拒绝',
    `audit_remark`     VARCHAR(500) COMMENT '审核意见',
    `status`           TINYINT DEFAULT 1 COMMENT '0-取消 1-正常',
    `version`          INT DEFAULT 0 COMMENT '版本号（乐观锁）',
    `deleted`          TINYINT DEFAULT 0,
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_college_id` (`college_id`),
    INDEX `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园宣讲会表';


-- ==========================================================
-- 初始化数据
-- 所有密码均为：123456  (BCrypt哈希)
-- ==========================================================

-- 超级管理员：admin / 123456
INSERT IGNORE INTO `t_admin` (`username`, `password`, `real_name`, `is_super_admin`, `status`)
VALUES ('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '超级管理员', 1, 1);

-- 学院账号：college_cs / 123456 等
INSERT IGNORE INTO `t_college` (`college_name`, `college_code`, `username`, `password`, `director_name`, `phone`, `email`, `introduction`, `status`)
VALUES
('大数据学院',   'CS',  'college_cs',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '张明华', '13800000001', 'cs@university.edu.cn',  '大数据学院是学校的重点学院，拥有数据科学、人工智能、软件工程等专业。', 1),
('经济管理学院', 'ECO', 'college_eco', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '李建国', '13800000002', 'eco@university.edu.cn', '经济管理学院设有经济学、工商管理、市场营销、会计学等专业。', 1),
('机械工程学院', 'ME',  'college_me',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '王志远', '13800000003', 'me@university.edu.cn',  '机械工程学院拥有机械设计、工业工程、车辆工程等优势专业。', 1),
('电气信息学院', 'EE',  'college_ee',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '陈晓东', '13800000004', 'ee@university.edu.cn',  '电气信息学院涵盖电气工程、电子信息、通信工程等专业。', 1);

-- 企业账号（审核通过）：company_ali / 123456 等
INSERT IGNORE INTO `t_company` (`company_name`, `username`, `password`, `contact_name`, `contact_phone`, `contact_email`, `industry`, `scale`, `nature`, `address`, `introduction`, `audit_status`, `status`)
VALUES
('阿里巴巴（中国）有限公司', 'company_ali', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '王芳', '021-88888888', 'hr@alibaba.com', '互联网', '10000人以上', '上市公司', '浙江省杭州市余杭区文一西路969号', '阿里巴巴集团是全球最大的电商平台，旗下拥有淘宝、天猫、支付宝等知名产品。', 1, 1),
('腾讯科技（深圳）有限公司', 'company_tx',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '李明', '0755-86013388', 'hr@tencent.com', '互联网', '10000人以上', '上市公司', '广东省深圳市南山区高新科技园', '腾讯是中国领先的互联网服务提供商，旗下产品包括微信、QQ等。', 1, 1),
('字节跳动有限公司',         'company_byte','$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '张丽', '010-65863388', 'hr@bytedance.com','互联网', '10000人以上', '非上市企业', '北京市海淀区知春路甲48号盈都大厦', '字节跳动旗下拥有抖音、今日头条、飞书等产品，是全球最大独角兽企业之一。', 1, 1),
('华为技术有限公司',         'company_hw',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '赵强', '0755-28780808', 'hr@huawei.com', '硬件/通信', '10000人以上', '非上市企业', '广东省深圳市龙岗区坂田华为基地', '华为是全球领先的ICT基础设施和智能终端提供商，业务遍布全球170多个国家。', 1, 1),
('美团科技有限公司',         'company_mt',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '孙丽', '010-57289888', 'hr@meituan.com', '互联网', '10000人以上', '上市公司', '北京市朝阳区望京东路6号望京SOHO', '美团是国内领先的生活服务电商平台，拥有外卖、酒旅、出行等业务。', 1, 1);

-- 学生测试账号：student001 / 123456
INSERT IGNORE INTO `t_student` (`student_no`, `real_name`, `username`, `password`, `email`, `phone`, `gender`, `college_id`, `major`, `grade`, `education`, `graduation_date`, `introduction`, `intention_position`, `intention_city`, `intention_industry`, `intention_salary`, `status`)
VALUES
('2022001001', '李小明', 'student001', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'lxm@stu.edu.cn', '13900000001', 1, 1, '软件工程', '2022', '本科', '2026-07-01', '热爱编程，熟悉Java/Vue，有项目开发经验。', 'Java开发工程师', '北京', '互联网', '15K-25K', 1),
('2022001002', '王小红', 'student002', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'wxh@stu.edu.cn', '13900000002', 0, 1, '计算机科学', '2022', '本科', '2026-07-01', '主攻前端开发，熟悉React/Vue，关注用户体验。', '前端开发工程师', '上海', '互联网', '12K-20K', 1),
('2022002001', '张大伟', 'student003', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'zdw@stu.edu.cn', '13900000003', 1, 2, '工商管理',   '2022', '本科', '2026-07-01', '具备良好的商业思维和沟通能力，实习期间参与多个运营项目。', '产品经理', '深圳', '互联网', '10K-18K', 1);

-- 职位测试数据（已审核通过）
INSERT IGNORE INTO `t_job` (`company_id`, `job_name`, `job_type`, `work_location`, `salary_range`, `education_req`, `experience_req`, `recruit_num`, `description`, `requirement`, `audit_status`, `status`, `view_count`)
VALUES
(1, 'Java后端开发工程师',   '全职', '杭州', '15K-25K', '本科', '应届生可', 10,
 'Java后端开发，参与阿里电商核心业务系统研发，承担服务端架构设计、研发工作。',
 '熟悉Java语言及Spring Boot框架；熟悉MySQL/Redis；有良好的编码习惯；应届生需有实习经历。',
 1, 1, 256),
(1, '数据分析师（校招）',   '全职', '杭州', '12K-18K', '本科', '应届生可', 5,
 '负责业务数据采集、清洗、分析与可视化，协助业务团队做决策支持。',
 '统计学/数学/计算机相关专业；熟悉Python/SQL；有数据分析或BI项目经验者优先。',
 1, 1, 128),
(2, '微信事业群-前端开发',  '全职', '深圳', '18K-30K', '本科', '应届生可', 8,
 '参与微信核心功能模块的前端开发，优化用户体验，解决技术难题。',
 '熟悉HTML/CSS/JS；掌握Vue或React框架；有良好的组件化设计能力；关注性能优化。',
 1, 1, 512),
(2, 'iOS/Android移动端开发', '全职', '深圳', '20K-35K', '本科', '应届生可', 6,
 '参与腾讯核心产品的移动端开发，构建高性能客户端应用。',
 '熟悉Swift/ObjC 或 Kotlin/Java；了解移动端性能优化；有上架APP经历者优先。',
 1, 1, 380),
(3, '抖音产品实习生',       '实习', '北京', '200-300元/天', '本科在读', '无要求',  20,
 '协助产品经理进行需求分析、竞品调研、数据分析，参与产品迭代全流程。',
 '本科及以上在读（2026/2027届）；对产品有热情；逻辑思维强；每周至少实习4天。',
 1, 1, 624),
(3, '算法工程师（NLP方向）', '全职', '北京', '25K-45K', '硕士', '应届生可', 5,
 '负责今日头条/抖音的NLP核心算法研究与落地，包括文本理解、意图识别等方向。',
 '计算机/统计学硕士及以上；熟悉深度学习框架（PyTorch/TF）；有NLP项目经验。',
 1, 1, 290),
(4, '嵌入式软件工程师',     '全职', '深圳', '14K-22K', '本科', '应届生可', 15,
 '参与华为终端、服务器等产品嵌入式软件开发及测试工作。',
 '电子/计算机/通信相关专业；熟悉C/C++；了解嵌入式Linux；有RTOS经验者优先。',
 1, 1, 175),
(5, '商业分析实习生',       '实习', '上海', '180-250元/天', '本科在读', '无要求', 30,
 '参与美团本地生活业务的市场数据分析、用户研究等工作。',
 '2026/2027届本科在读；Excel/SQL基础扎实；对消费互联网有热情；实习6个月以上。',
 1, 1, 320);

-- 宣讲会测试数据
INSERT IGNORE INTO `t_campus_talk` (`company_id`, `college_id`, `title`, `description`, `talk_time`, `location`, `sign_deadline`, `max_sign_num`, `current_sign_num`, `audit_status`, `status`)
VALUES
(1, NULL, '2026阿里巴巴校园招聘宣讲会',
 '阿里巴巴集团2026届校园招聘即将启动！本次宣讲会将介绍公司业务、技术栈及校招流程，欢迎对互联网技术感兴趣的同学参加。',
 '2026-04-10 14:00:00', '图书馆报告厅', '2026-04-08', 300, 156, 1, 1),
(2, 1, '腾讯X大数据学院专场宣讲',
'腾讯大数据学院专场宣讲，重点介绍客户端、后台开发、AI等岗位机会，安排技术大牛分享成长经验。',
'2026-04-15 10:00:00', '大数据学院报告厅A', '2026-04-13', 200, 89, 1, 1),
(3, NULL, '字节跳动春招宣讲暨内推分享会',
 '字节跳动2026春招全面启动！数百个岗位虚位以待，HR直面解答，现场内推通道开放。',
 '2026-04-20 15:00:00', '大学生活动中心', '2026-04-18', 400, 212, 1, 1);
