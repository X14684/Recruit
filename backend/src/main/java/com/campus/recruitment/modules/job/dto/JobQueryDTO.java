package com.campus.recruitment.modules.job.dto;

import lombok.Data;

@Data
public class JobQueryDTO {
    private String keyword;
    private String jobType;
    private String workLocation;
    private String education;
    private String salaryRange;
    private Long companyId;
    private Integer page = 1;
    private Integer size = 10;
}
