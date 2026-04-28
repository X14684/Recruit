package com.campus.recruitment.modules.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.recruitment.common.annotation.SensitiveField;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生实体
 */
@Data
@TableName("t_student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学号 */
    private String studentNo;

    /** 姓名 */
    private String realName;

    /** 登录账号 */
    private String username;

    /** 密码（加密） */
    @SensitiveField
    private String password;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 性别 0-女 1-男 */
    private Integer gender;

    /** 头像 */
    private String avatar;

    /** 学院ID */
    private Long collegeId;

    /** 专业 */
    private String major;

    /** 年级 */
    private String grade;

    /** 学历 */
    private String education;

    /** 毕业时间 */
    private LocalDate graduationDate;

    /** 出生日期 */
    private LocalDate birthday;

    /** 个人简介 */
    private String introduction;

    /** 求职意向-职位 */
    private String intentionPosition;

    /** 求职意向-城市 */
    private String intentionCity;

    /** 求职意向-行业 */
    private String intentionIndustry;

    /** 求职意向-薪资 */
    private String intentionSalary;

    /** 简历文件URL */
    private String resumeUrl;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
