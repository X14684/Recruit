package com.campus.recruitment.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.result.PageResult;
import com.campus.recruitment.common.result.Result;
import com.campus.recruitment.common.utils.UserContext;
import com.campus.recruitment.modules.company.dto.CompanyRegisterDTO;
import com.campus.recruitment.modules.company.entity.Company;
import com.campus.recruitment.modules.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 企业端接口
 */
@Slf4j
@Tag(name = "企业模块")
@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    private static final List<String> ALLOWED_RESUME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Operation(summary = "企业注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody CompanyRegisterDTO dto) {
        companyService.register(dto);
        return Result.ok("注册成功，请等待管理员审核");
    }

    @Operation(summary = "获取企业信息")
    @GetMapping("/profile")
    public Result<Company> getProfile() {
        Long companyId = UserContext.getUserId();
        Company company = companyService.getById(companyId);
        if (company == null) {
            throw BusinessException.of("企业不存在");
        }
        company.setPassword(null);
        return Result.ok(company);
    }

    @Operation(summary = "获取企业详情（公开）")
    @GetMapping("/{id}")
    public Result<Company> getById(@PathVariable Long id) {
        Company company = companyService.getById(id);
        if (company != null) company.setPassword(null);
        return Result.ok(company);
    }

    @Operation(summary = "更新企业信息")
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Company company) {
        company.setId(UserContext.getUserId());
        companyService.updateProfile(company);
        return Result.ok("更新成功");
    }

    @Operation(summary = "企业列表（管理员用）")
    @GetMapping("/list")
    public Result<PageResult<Company>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) wrapper.eq(Company::getAuditStatus, auditStatus);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Company::getCompanyName, keyword);
        }
        wrapper.orderByDesc(Company::getCreateTime);
        Page<Company> companyPage = companyService.page(new Page<>(page, size), wrapper);
        companyPage.getRecords().forEach(c -> c.setPassword(null));
        return Result.ok(PageResult.of(companyPage));
    }

    @Operation(summary = "上传企业Logo")
    @PostMapping("/logo/upload")
    public Result<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) {
        log.info("Logo上传，文件名: {}, 大小: {}, 类型: {}",
            file.getOriginalFilename(), file.getSize(), file.getContentType());

        if (file.isEmpty()) {
            return Result.fail("请选择要上传的图片");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.fail("图片大小不能超过2MB");
        }

        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return Result.fail("仅支持图片格式");
        }

        try {
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path targetDir = basePath.resolve("logos").resolve(dateDir);

            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            Path targetPath = targetDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());

            String fileUrl = "/uploads/logos/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);

            log.info("Logo上传成功: {}", fileUrl);
            return Result.ok(result);

        } catch (IOException e) {
            log.error("Logo上传失败: {}", e.getMessage(), e);
            return Result.fail("图片上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传营业执照")
    @PostMapping("/license/upload")
    public Result<Map<String, String>> uploadLicense(@RequestParam("file") MultipartFile file) {
        log.info("营业执照上传，文件名: {}, 大小: {}, 类型: {}",
            file.getOriginalFilename(), file.getSize(), file.getContentType());

        if (file.isEmpty()) {
            return Result.fail("请选择要上传的文件");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.fail("文件大小不能超过5MB");
        }

        String contentType = file.getContentType();
        if (!ALLOWED_RESUME_TYPES.contains(contentType) && !contentType.startsWith("image/")) {
            return Result.fail("仅支持 PDF、图片格式");
        }

        try {
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path targetDir = basePath.resolve("licenses").resolve(dateDir);

            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            Path targetPath = targetDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());

            String fileUrl = "/uploads/licenses/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);

            log.info("营业执照上传成功: {}", fileUrl);
            return Result.ok(result);

        } catch (IOException e) {
            log.error("营业执照上传失败: {}", e.getMessage(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }
}
