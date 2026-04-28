package com.campus.recruitment.modules.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.recruitment.common.result.PageResult;
import com.campus.recruitment.common.result.Result;
import com.campus.recruitment.modules.admin.dto.LoginDTO;
import com.campus.recruitment.modules.admin.service.AdminService;
import com.campus.recruitment.modules.college.entity.College;
import com.campus.recruitment.modules.job.entity.Job;
import com.campus.recruitment.modules.job.service.JobService;
import com.campus.recruitment.modules.college.service.CollegeService;
import com.campus.recruitment.modules.company.service.CompanyService;
import com.campus.recruitment.modules.student.entity.Student;
import com.campus.recruitment.modules.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员接口（统一登录 + 管理功能）
 */
@Slf4j
@Tag(name = "管理员模块")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final StudentService studentService;
    private final CompanyService companyService;
    private final CollegeService collegeService;
    private final JobService jobService;
    private final BCryptPasswordEncoder passwordEncoder;

    // ==================== 统一登录 ====================

    @Operation(summary = "统一登录（学生/企业/学院/管理员）")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        Map<String, Object> result;
        switch (dto.getRole().toUpperCase()) {
            case "STUDENT" -> result = studentService.login(dto.getUsername(), dto.getPassword());
            case "COMPANY" -> result = companyService.login(dto.getUsername(), dto.getPassword());
            case "COLLEGE" -> result = collegeService.login(dto.getUsername(), dto.getPassword());
            case "ADMIN" -> result = adminService.login(dto.getUsername(), dto.getPassword());
            default -> throw new com.campus.recruitment.common.exception.BusinessException("未知角色");
        }
        return Result.ok(result);
    }

    // ==================== 统计数据 ====================

    @Operation(summary = "系统统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        return Result.ok(adminService.getStatistics());
    }

    @Operation(summary = "就业统计数据（真实计算）")
    @GetMapping("/employment-statistics")
    public Result<Map<String, Object>> employmentStatistics() {
        return Result.ok(adminService.getEmploymentStatistics());
    }

    // ==================== 企业审核 ====================

    @Operation(summary = "审核企业")
    @PutMapping("/company/{id}/audit")
    public Result<?> auditCompany(@PathVariable Long id, @RequestBody AuditDTO dto) {
        companyService.audit(id, dto.getStatus(), dto.getRemark());
        return Result.ok("审核完成");
    }

    // ==================== 学院管理 ====================

    @Operation(summary = "添加学院")
    @PostMapping("/college")
    public Result<?> addCollege(@RequestBody College college) {
        String rawPwd = college.getPassword();
        // 密码为空时使用默认密码 123456
        if (rawPwd == null || rawPwd.trim().isEmpty()) {
            rawPwd = "123456";
        }
        college.setPassword(passwordEncoder.encode(rawPwd));
        college.setStatus(1);
        collegeService.save(college);
        return Result.ok("添加成功");
    }

    @Operation(summary = "学院列表")
    @GetMapping("/college/list")
    public Result<PageResult<College>> collegeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<College> p = collegeService.page(new Page<>(page, size),
                new LambdaQueryWrapper<College>().orderByDesc(College::getCreateTime));
        p.getRecords().forEach(c -> c.setPassword(null));
        return Result.ok(PageResult.of(p));
    }

    @Operation(summary = "重置学院密码")
    @PutMapping("/college/{id}/password")
    public Result<?> resetCollegePassword(@PathVariable Long id, @RequestBody ResetPwdDTO dto) {
        College college = collegeService.getById(id);
        if (college == null) {
            return Result.fail("学院不存在");
        }
        college.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        collegeService.updateById(college);
        return Result.ok("密码重置成功");
    }

    @Operation(summary = "禁用/启用学院")
    @PutMapping("/college/{id}/status")
    public Result<?> toggleCollegeStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        College college = collegeService.getById(id);
        if (college != null) {
            college.setStatus(dto.getStatus());
            collegeService.updateById(college);
        }
        return Result.ok("操作成功");
    }

    // ==================== 职位管理（管理员） ====================

    @Operation(summary = "管理员职位列表（查全部，包括待审核）")
    @GetMapping("/job/list")
    public Result<?> adminJobList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer auditStatus) {
        Page<Job> p = jobService.pageJobsForAdmin(page, size, keyword, auditStatus);
        return Result.ok(PageResult.of(p));
    }

    // ==================== 学生管理 ====================

    @Operation(summary = "禁用/启用学生")
    @PutMapping("/student/{id}/status")
    public Result<?> toggleStudentStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        Student student = studentService.getById(id);
        if (student != null) {
            student.setStatus(dto.getStatus());
            studentService.updateById(student);
        }
        return Result.ok("操作成功");
    }

    @Data
    static class ResetPwdDTO {
        private String newPassword;
    }

    @Data
    static class AuditDTO {
        private Integer status;
        private String remark;
    }

    @Data
    static class StatusDTO {
        private Integer status;
    }

    // ==================== 临时：重置管理员密码 ====================
    @Operation(summary = "重置管理员密码（临时）")
    @PostMapping("/reset-password")
    public Result<?> resetPassword() {
        var admin = adminService.getById(1L);
        if (admin != null) {
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminService.updateById(admin);
            return Result.ok("密码已重置为: admin123");
        }
        return Result.fail("未找到管理员");
    }
}
