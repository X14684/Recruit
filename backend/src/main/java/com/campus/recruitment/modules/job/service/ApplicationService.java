package com.campus.recruitment.modules.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.job.dto.ApplicationDetailVO;
import com.campus.recruitment.modules.job.dto.ApplicationVO;
import com.campus.recruitment.modules.job.entity.Application;

public interface ApplicationService extends IService<Application> {
    /** 投递简历 */
    void apply(Long studentId, Long jobId);
    /** 更新投递状态（企业操作） */
    void updateStatus(Long id, Integer status, String remark, String interviewLocation, String interviewTime);
    /** 学生投递记录分页 */
    Page<Application> pageByStudent(Long studentId, Integer page, Integer size);
    /** 企业收到的投递分页 */
    Page<Application> pageByCompany(Long companyId, Long jobId, Integer page, Integer size);
    /** 企业收到的投递分页（带学生姓名和职位名称） */
    Page<ApplicationVO> pageByCompanyWithDetail(Long companyId, Long jobId, Integer page, Integer size);
    /** 学生投递记录分页（带企业和职位名称） */
    Page<ApplicationDetailVO> pageByStudentWithDetail(Long studentId, Integer page, Integer size);
    /** 是否已投递 */
    boolean hasApplied(Long studentId, Long jobId);
}
