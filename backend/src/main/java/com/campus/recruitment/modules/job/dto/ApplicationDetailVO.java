package com.campus.recruitment.modules.job.dto;

import lombok.Data;

/**
 * 投递记录详情VO（用于学生端）
 */
@Data
public class ApplicationDetailVO {

    private Long id;
    private Long studentId;
    private Long jobId;
    private String jobName;          // 职位名称
    private Long companyId;
    private String companyName;      // 企业名称
    private String resumeUrl;
    private Integer status;
    private String companyRemark;
    private String interviewTime;
    private String interviewLocation;
    private String createTime;
    private String updateTime;
}
