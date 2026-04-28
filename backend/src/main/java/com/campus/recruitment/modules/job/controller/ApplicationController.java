package com.campus.recruitment.modules.job.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.recruitment.common.result.PageResult;
import com.campus.recruitment.common.result.Result;
import com.campus.recruitment.common.utils.UserContext;
import com.campus.recruitment.modules.job.dto.ApplicationDetailVO;
import com.campus.recruitment.modules.job.dto.ApplicationVO;
import com.campus.recruitment.modules.job.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 投递记录接口
 */
@Tag(name = "投递模块")
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "投递简历")
    @PostMapping("/apply/{jobId}")
    public Result<?> apply(@PathVariable Long jobId) {
        Long studentId = UserContext.getUserId();
        applicationService.apply(studentId, jobId);
        return Result.ok("投递成功");
    }

    @Operation(summary = "是否已投递")
    @GetMapping("/hasApplied/{jobId}")
    public Result<Boolean> hasApplied(@PathVariable Long jobId) {
        Long studentId = UserContext.getUserId();
        return Result.ok(applicationService.hasApplied(studentId, jobId));
    }

    @Operation(summary = "学生投递记录")
    @GetMapping("/student/list")
    public Result<PageResult<ApplicationDetailVO>> studentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long studentId = UserContext.getUserId();
        Page<ApplicationDetailVO> p = applicationService.pageByStudentWithDetail(studentId, page, size);
        return Result.ok(PageResult.of(p));
    }

    @Operation(summary = "企业收到的投递")
    @GetMapping("/company/list")
    public Result<PageResult<ApplicationVO>> companyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long jobId) {
        Long companyId = UserContext.getUserId();
        Page<ApplicationVO> p = applicationService.pageByCompanyWithDetail(companyId, jobId, page, size);
        return Result.ok(PageResult.of(p));
    }

    @Operation(summary = "更新投递状态（企业）")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO dto) {
        applicationService.updateStatus(id, dto.getStatus(), dto.getRemark(),
                dto.getInterviewLocation(), dto.getInterviewTime());
        return Result.ok("更新成功");
    }

    @Data
    static class UpdateStatusDTO {
        private Integer status;
        private String remark;
        private String interviewLocation;
        private String interviewTime;
    }
}
