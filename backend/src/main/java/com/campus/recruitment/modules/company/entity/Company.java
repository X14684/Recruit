package com.campus.recruitment.modules.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.recruitment.common.annotation.SensitiveField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业实体
 */
@Data
@TableName("t_company")
public class Company {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 企业名称 */
    private String companyName;

    /** 登录账号 */
    private String username;

    /** 密码（加密） */
    @SensitiveField
    private String password;

    /** 企业Logo */
    private String logo;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 联系邮箱 */
    private String contactEmail;

    /** 所属行业 */
    private String industry;

    /** 企业规模 */
    private String scale;

    /** 企业性质 */
    private String nature;

    /** 注册地址 */
    private String address;

    /** 企业简介 */
    private String introduction;

    /** 营业执照URL */
    private String licenseUrl;

    /** 审核状态 0-待审核 1-通过 2-拒绝 */
    private Integer auditStatus;

    /** 审核意见 */
    private String auditRemark;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
