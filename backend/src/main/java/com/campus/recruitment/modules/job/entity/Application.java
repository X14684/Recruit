package com.campus.recruitment.modules.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投递记录实体
 */
@Data
@TableName("t_application")
public class Application {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 职位ID */
    private Long jobId;

    /** 企业ID */
    private Long companyId;

    /** 简历URL（投递时快照） */
    private String resumeUrl;

    /**
     * 状态：
     * 0-已投递
     * 1-已查看
     * 2-初审通过
     * 3-面试邀请
     * 4-已录用
     * 5-不合适
     */
    private Integer status;

    /** 企业备注 */
    private String companyRemark;

    /** 面试时间 */
    private LocalDateTime interviewTime;

    /** 面试地点 */
    private String interviewLocation;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
