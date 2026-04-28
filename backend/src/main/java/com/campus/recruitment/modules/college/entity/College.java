package com.campus.recruitment.modules.college.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.recruitment.common.annotation.SensitiveField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学院实体
 */
@Data
@TableName("t_college")
public class College {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学院名称 */
    private String collegeName;

    /** 学院编码 */
    private String collegeCode;

    /** 负责人账号 */
    private String username;

    /** 密码 */
    @SensitiveField
    private String password;

    /** 负责人姓名 */
    private String directorName;

    /** 联系电话 */
    private String phone;

    /** 联系邮箱 */
    private String email;

    /** 学院简介 */
    private String introduction;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
