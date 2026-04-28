package com.campus.recruitment.modules.job.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.recruitment.common.result.PageResult;
import com.campus.recruitment.common.result.Result;
import com.campus.recruitment.common.utils.UserContext;
import com.campus.recruitment.modules.job.dto.JobDTO;
import com.campus.recruitment.modules.job.dto.JobQueryDTO;
import com.campus.recruitment.modules.job.entity.Job;
import com.campus.recruitment.modules.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 职位接口
 */
@Tag(name = "职位模块")
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "发布职位")
    @PostMapping
    public Result<?> publish(@Valid @RequestBody JobDTO dto) {
        Long companyId = UserContext.getUserId();
        jobService.publishJob(dto, companyId);
        return Result.ok("发布成功，等待审核");
    }

    @Operation(summary = "分页查询职位（公开）")
    @GetMapping("/list")
    public Result<PageResult<Job>> list(JobQueryDTO queryDTO) {
        Page<Job> page = jobService.pageJobs(queryDTO);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "职位详情")
    @GetMapping("/{id}")
    public Result<Job> detail(@PathVariable Long id) {
        Job job = jobService.getById(id);
        // 增加浏览量
        if (job != null) {
            job.setViewCount(job.getViewCount() + 1);
            jobService.updateById(job);
        }
        return Result.ok(job);
    }

    @Operation(summary = "更新职位")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody JobDTO dto) {
        Long companyId = UserContext.getUserId();
        jobService.updateJob(id, dto, companyId);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除职位")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Long companyId = UserContext.getUserId();
        jobService.deleteJob(id, companyId);
        return Result.ok("删除成功");
    }

    @Operation(summary = "开关职位状态")
    @PutMapping("/{id}/status")
    public Result<?> toggleStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        Long companyId = UserContext.getUserId();
        jobService.toggleJobStatus(id, dto.getStatus(), companyId);
        return Result.ok("操作成功");
    }

    @Operation(summary = "审核职位（管理员）")
    @PutMapping("/{id}/audit")
    public Result<?> audit(@PathVariable Long id, @RequestBody AuditDTO dto) {
        jobService.auditJob(id, dto.getStatus(), dto.getRemark());
        return Result.ok("审核完成");
    }

    @Data
    static class StatusDTO {
        private Integer status;
    }

    @Data
    static class AuditDTO {
        private Integer status;
        private String remark;
    }
}
