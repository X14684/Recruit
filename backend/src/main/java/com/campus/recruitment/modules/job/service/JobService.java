package com.campus.recruitment.modules.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.job.dto.JobDTO;
import com.campus.recruitment.modules.job.dto.JobQueryDTO;
import com.campus.recruitment.modules.job.entity.Job;
import org.springframework.lang.NonNull;

public interface JobService extends IService<Job> {
    /** 发布职位 */
    void publishJob(JobDTO dto, Long companyId);
    /** 分页查询职位（公开/企业用） */
    Page<Job> pageJobs(@NonNull JobQueryDTO queryDTO);
    /** 分页查询职位（管理员用，不过滤审核状态） */
    Page<Job> pageJobsForAdmin(@NonNull Integer page, @NonNull Integer size, String keyword, Integer auditStatus);
    /** 审核职位 */
    void auditJob(Long id, Integer status, String remark);
    /** 更新职位 */
    void updateJob(Long id, JobDTO dto, Long companyId);
    /** 关闭/开启职位 */
    void toggleJobStatus(Long id, Integer status, Long companyId);
    /** 删除职位 */
    void deleteJob(Long id, Long companyId);
}
