package com.campus.recruitment.modules.job.dto;

import lombok.Data;

/**
 * 投递记录VO（包含学生姓名和职位名称）
 */
@Data
public class ApplicationVO {

    private Long id;
    private Long studentId;
    private String studentName;      // 学生姓名
    private String studentNo;        // 学号
    private Long jobId;
    private String jobName;          // 职位名称
    private Long companyId;
    private String resumeUrl;
    private Integer status;
    private String companyRemark;
    private String interviewTime;
    private String interviewLocation;
    private String createTime;
    private String updateTime;
}
