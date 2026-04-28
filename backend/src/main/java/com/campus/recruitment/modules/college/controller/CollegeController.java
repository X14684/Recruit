package com.campus.recruitment.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.recruitment.common.result.PageResult;
import com.campus.recruitment.common.result.Result;
import com.campus.recruitment.common.utils.UserContext;
import com.campus.recruitment.modules.admin.service.AdminService;
import com.campus.recruitment.modules.college.entity.CampusTalk;
import com.campus.recruitment.modules.college.entity.College;
import com.campus.recruitment.modules.college.service.CampusTalkService;
import com.campus.recruitment.modules.college.service.CollegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学院端接口
 */
@Tag(name = "学院模块")
@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;
    private final CampusTalkService campusTalkService;
    private final AdminService adminService;

    @Operation(summary = "学院列表（公开）")
    @GetMapping("/list")
    public Result<PageResult<College>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<College> p = collegeService.page(new Page<>(page, size),
                new LambdaQueryWrapper<College>().eq(College::getStatus, 1));
        p.getRecords().forEach(c -> c.setPassword(null));
        return Result.ok(PageResult.of(p));
    }

    @Operation(summary = "获取学院信息")
    @GetMapping("/profile")
    public Result<College> getProfile() {
        Long collegeId = UserContext.getUserId();
        College college = collegeService.getById(collegeId);
        if (college != null) college.setPassword(null);
        return Result.ok(college);
    }

    @Operation(summary = "发布宣讲会")
    @PostMapping("/talk")
    public Result<?> publishTalk(@RequestBody CampusTalk campusTalk) {
        // 企业可发起，学院审核
        campusTalkService.publishTalk(campusTalk);
        return Result.ok("发布成功，等待学院审核");
    }

    @Operation(summary = "审核宣讲会（学院）")
    @PutMapping("/talk/{id}/audit")
    public Result<?> auditTalk(@PathVariable Long id, @RequestBody AuditDTO dto) {
        campusTalkService.auditTalk(id, dto.getStatus(), dto.getRemark());
        return Result.ok("审核完成");
    }

    @Operation(summary = "宣讲会列表（公开）")
    @GetMapping("/talk/list")
    public Result<PageResult<CampusTalk>> talkList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long collegeId) {
        return Result.ok(PageResult.of(campusTalkService.pageTalks(page, size, collegeId, 1)));
    }

    @Operation(summary = "企业自己的宣讲会列表（含待审核）")
    @GetMapping("/talk/my")
    public Result<PageResult<CampusTalk>> myTalkList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        Long companyId = UserContext.getUserId();
        return Result.ok(PageResult.of(campusTalkService.pageTalksByCompany(page, size, companyId)));
    }

    @Operation(summary = "报名宣讲会（学生）")
    @PostMapping("/talk/{id}/signup")
    public Result<?> signUp(@PathVariable Long id) {
        Long studentId = UserContext.getUserId();
        campusTalkService.signUp(id, studentId);
        return Result.ok("报名成功");
    }

    @Operation(summary = "学院就业统计（真实数据）")
    @GetMapping("/employment-statistics")
    public Result<Map<String, Object>> employmentStatistics() {
        Long collegeId = UserContext.getUserId();
        return Result.ok(adminService.getCollegeEmploymentStatistics(collegeId));
    }

    @Data
    static class AuditDTO {
        private Integer status;
        private String remark;
    }
}
