package com.campus.recruitment.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.modules.job.dto.JobDTO;
import com.campus.recruitment.modules.job.dto.JobQueryDTO;
import com.campus.recruitment.modules.job.entity.Job;
import com.campus.recruitment.modules.job.mapper.JobMapper;
import com.campus.recruitment.modules.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Override
    public void publishJob(JobDTO dto, Long companyId) {
        Job job = new Job();
        BeanUtils.copyProperties(dto, job);
        job.setCompanyId(companyId);
        job.setAuditStatus(0);  // 待审核
        job.setStatus(1);
        job.setViewCount(0);
        save(job);
    }

    @Override
    public Page<Job> pageJobsForAdmin(@NonNull Integer page, @NonNull Integer size, String keyword, Integer auditStatus) {
        Page<Job> p = new Page<>(page, size);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(Job::getAuditStatus, auditStatus);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Job::getJobName, keyword)
                    .or().like(Job::getDescription, keyword));
        }
        wrapper.orderByDesc(Job::getCreateTime);
        return page(p, wrapper);
    }

    @Override
    public Page<Job> pageJobs(@NonNull JobQueryDTO queryDTO) {
        Page<Job> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        // 只有公开查询（无 companyId）时才强制过滤已通过审核且招聘中的职位
        // 企业查自己的职位时（传了 companyId）不过滤，可以看到所有状态
        if (queryDTO.getCompanyId() != null) {
            wrapper.eq(Job::getCompanyId, queryDTO.getCompanyId());
        } else {
            wrapper.eq(Job::getAuditStatus, 1)
                   .eq(Job::getStatus, 1);
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Job::getJobName, queryDTO.getKeyword())
                    .or().like(Job::getDescription, queryDTO.getKeyword()));
        }
        if (StringUtils.hasText(queryDTO.getJobType())) {
            wrapper.eq(Job::getJobType, queryDTO.getJobType());
        }
        if (StringUtils.hasText(queryDTO.getWorkLocation())) {
            wrapper.like(Job::getWorkLocation, queryDTO.getWorkLocation());
        }
        wrapper.orderByDesc(Job::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void auditJob(Long id, Integer status, String remark) {
        Job job = getById(id);
        if (job == null) {
            throw BusinessException.of("职位不存在");
        }
        job.setAuditStatus(status);
        job.setAuditRemark(remark);
        updateById(job);
    }

    @Override
    public void updateJob(Long id, JobDTO dto, Long companyId) {
        Job job = getById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw BusinessException.of("职位不存在或无权操作");
        }
        BeanUtils.copyProperties(dto, job);
        job.setId(id);
        job.setAuditStatus(0);  // 修改后重新审核
        updateById(job);
    }

    @Override
    public void toggleJobStatus(Long id, Integer status, Long companyId) {
        Job job = getById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw BusinessException.of("职位不存在或无权操作");
        }
        job.setStatus(status);
        updateById(job);
    }

    @Override
    public void deleteJob(Long id, Long companyId) {
        Job job = getById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw BusinessException.of("职位不存在或无权操作");
        }
        removeById(id);
    }
}
