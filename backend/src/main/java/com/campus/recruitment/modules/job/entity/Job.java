package com.campus.recruitment.modules.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 职位实体
 */
@Data
@TableName("t_job")
public class Job {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 企业ID */
    private Long companyId;

    /** 职位名称 */
    private String jobName;

    /** 职位类型 */
    private String jobType;

    /** 工作地点 */
    private String workLocation;

    /** 薪资范围 */
    private String salaryRange;

    /** 学历要求 */
    private String educationReq;

    /** 经验要求 */
    private String experienceReq;

    /** 招聘人数 */
    private Integer recruitNum;

    /** 职位描述 */
    private String description;

    /** 职位要求 */
    private String requirement;

    /** 行业标签 */
    private String tags;

    /** 截止日期 */
    private LocalDate deadline;

    /** 审核状态 0-待审核 1-通过 2-拒绝 */
    private Integer auditStatus;

    /** 审核意见 */
    private String auditRemark;

    /** 状态 0-关闭 1-招聘中 */
    private Integer status;

    /** 浏览量 */
    private Integer viewCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
