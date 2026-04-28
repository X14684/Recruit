package com.campus.recruitment.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.modules.job.dto.ApplicationDetailVO;
import com.campus.recruitment.modules.job.dto.ApplicationVO;
import com.campus.recruitment.modules.job.entity.Application;
import com.campus.recruitment.modules.job.entity.Job;
import com.campus.recruitment.modules.job.mapper.ApplicationMapper;
import com.campus.recruitment.modules.job.service.ApplicationService;
import com.campus.recruitment.modules.job.service.JobService;
import com.campus.recruitment.modules.student.entity.Student;
import com.campus.recruitment.modules.student.service.StudentService;
import com.campus.recruitment.modules.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    private final JobService jobService;
    private final StudentService studentService;
    private final CompanyService companyService;

    @Override
    public void apply(Long studentId, Long jobId) {
        // 检查是否已投递
        if (hasApplied(studentId, jobId)) {
            throw BusinessException.of("您已经投递过该职位");
        }
        Job job = jobService.getById(jobId);
        if (job == null || job.getStatus() != 1 || job.getAuditStatus() != 1) {
            throw BusinessException.of("职位不存在或已关闭");
        }
        Student student = studentService.getById(studentId);
        Application application = new Application();
        application.setStudentId(studentId);
        application.setJobId(jobId);
        application.setCompanyId(job.getCompanyId());
        application.setResumeUrl(student.getResumeUrl());
        application.setStatus(0);  // 已投递
        save(application);
    }

    @Override
    public void updateStatus(Long id, Integer status, String remark, String interviewLocation, String interviewTime) {
        Application application = getById(id);
        if (application == null) {
            throw BusinessException.of("投递记录不存在");
        }
        application.setStatus(status);
        application.setCompanyRemark(remark);
        if (interviewLocation != null) {
            application.setInterviewLocation(interviewLocation);
        }
        if (interviewTime != null) {
            try {
                LocalDateTime parsedTime;
                // 支持两种格式：ISO 8601（带T）和 yyyy-MM-dd HH:mm:ss（带空格）
                if (interviewTime.contains("T")) {
                    // ISO 8601 格式，如 2026-03-25T10:00:00
                    parsedTime = LocalDateTime.parse(interviewTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    // 传统格式，如 2026-03-25 10:00:00
                    parsedTime = LocalDateTime.parse(interviewTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                application.setInterviewTime(parsedTime);
            } catch (DateTimeParseException e) {
                throw BusinessException.of("面试时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 格式");
            }
        }
        updateById(application);
    }

    @Override
    public Page<Application> pageByStudent(Long studentId, Integer page, Integer size) {
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, studentId)
                        .orderByDesc(Application::getCreateTime));
    }

    @Override
    public Page<Application> pageByCompany(Long companyId, Long jobId, Integer page, Integer size) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<Application>()
                .eq(Application::getCompanyId, companyId)
                .orderByDesc(Application::getCreateTime);
        if (jobId != null) {
            wrapper.eq(Application::getJobId, jobId);
        }
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<ApplicationVO> pageByCompanyWithDetail(Long companyId, Long jobId, Integer page, Integer size) {
        Page<Application> appPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getCompanyId, companyId)
                        .eq(jobId != null, Application::getJobId, jobId)
                        .orderByDesc(Application::getCreateTime));

        Page<ApplicationVO> voPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        voPage.setRecords(new ArrayList<>());
        if (appPage.getRecords().isEmpty()) {
            return voPage;
        }

        for (Application app : appPage.getRecords()) {
            ApplicationVO vo = new ApplicationVO();
            vo.setId(app.getId());
            vo.setStudentId(app.getStudentId());
            vo.setJobId(app.getJobId());
            vo.setCompanyId(app.getCompanyId());
            vo.setResumeUrl(app.getResumeUrl());
            vo.setStatus(app.getStatus());
            vo.setCompanyRemark(app.getCompanyRemark());
            if (app.getInterviewTime() != null) {
                vo.setInterviewTime(app.getInterviewTime().toString());
            }
            vo.setInterviewLocation(app.getInterviewLocation());
            vo.setCreateTime(app.getCreateTime() != null ? app.getCreateTime().toString().replace('T', ' ') : null);
            vo.setUpdateTime(app.getUpdateTime() != null ? app.getUpdateTime().toString().replace('T', ' ') : null);

            if (app.getStudentId() != null) {
                Student student = studentService.getById(app.getStudentId());
                if (student != null) {
                    vo.setStudentName(student.getRealName());
                    vo.setStudentNo(student.getStudentNo());
                }
            }
            if (app.getJobId() != null) {
                Job job = jobService.getById(app.getJobId());
                if (job != null) {
                    vo.setJobName(job.getJobName());
                }
            }
            voPage.getRecords().add(vo);
        }
        return voPage;
    }

    @Override
    public Page<ApplicationDetailVO> pageByStudentWithDetail(Long studentId, Integer page, Integer size) {
        Page<Application> appPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, studentId)
                        .orderByDesc(Application::getCreateTime));

        Page<ApplicationDetailVO> voPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        voPage.setRecords(new ArrayList<>());
        if (appPage.getRecords().isEmpty()) {
            return voPage;
        }

        for (Application app : appPage.getRecords()) {
            ApplicationDetailVO vo = new ApplicationDetailVO();
            vo.setId(app.getId());
            vo.setStudentId(app.getStudentId());
            vo.setJobId(app.getJobId());
            vo.setCompanyId(app.getCompanyId());
            vo.setResumeUrl(app.getResumeUrl());
            vo.setStatus(app.getStatus());
            vo.setCompanyRemark(app.getCompanyRemark());
            if (app.getInterviewTime() != null) {
                vo.setInterviewTime(app.getInterviewTime().toString());
            }
            vo.setInterviewLocation(app.getInterviewLocation());
            vo.setCreateTime(app.getCreateTime() != null ? app.getCreateTime().toString().replace('T', ' ') : null);
            vo.setUpdateTime(app.getUpdateTime() != null ? app.getUpdateTime().toString().replace('T', ' ') : null);

            if (app.getJobId() != null) {
                Job job = jobService.getById(app.getJobId());
                if (job != null) {
                    vo.setJobName(job.getJobName());
                }
            }
            if (app.getCompanyId() != null) {
                var company = companyService.getById(app.getCompanyId());
                if (company != null) {
                    vo.setCompanyName(company.getCompanyName());
                }
            }
            voPage.getRecords().add(vo);
        }
        return voPage;
    }

    @Override
    public boolean hasApplied(Long studentId, Long jobId) {
        return count(new LambdaQueryWrapper<Application>()
                .eq(Application::getStudentId, studentId)
                .eq(Application::getJobId, jobId)) > 0;
    }
}
