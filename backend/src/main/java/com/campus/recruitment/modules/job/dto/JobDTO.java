package com.campus.recruitment.modules.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobDTO {
    @NotBlank(message = "职位名称不能为空")
    private String jobName;
    private String jobType;
    @NotBlank(message = "工作地点不能为空")
    private String workLocation;
    private String salaryRange;
    private String educationReq;
    private String experienceReq;
    @NotNull(message = "招聘人数不能为空")
    private Integer recruitNum;
    @NotBlank(message = "职位描述不能为空")
    private String description;
    private String requirement;
    private String tags;
    private LocalDate deadline;
}
