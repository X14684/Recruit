package com.campus.recruitment.modules.college.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 校园宣讲会实体
 */
@Data
@TableName("t_campus_talk")
public class CampusTalk {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 企业ID */
    private Long companyId;

    /** 学院ID */
    private Long collegeId;

    /** 宣讲会标题 */
    private String title;

    /** 宣讲会描述 */
    private String description;

    /** 举办时间 */
    private LocalDateTime talkTime;

    /** 举办地点 */
    private String location;

    /** 报名截止时间 */
    private LocalDate signDeadline;

    /** 最大报名人数 */
    private Integer maxSignNum;

    /** 当前报名人数 */
    private Integer currentSignNum;

    /** 审核状态 0-待审核 1-通过 2-拒绝 */
    private Integer auditStatus;

    /** 审核意见 */
    private String auditRemark;

    /** 状态 0-取消 1-正常 */
    private Integer status;

    /** 版本号（乐观锁） */
    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
