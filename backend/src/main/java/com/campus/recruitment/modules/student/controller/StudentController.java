package com.campus.recruitment.modules.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.result.PageResult;
import com.campus.recruitment.common.result.Result;
import com.campus.recruitment.common.utils.UserContext;
import com.campus.recruitment.modules.student.dto.StudentRegisterDTO;
import com.campus.recruitment.modules.student.entity.Student;
import com.campus.recruitment.modules.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
 * 学生端接口
 */
@Slf4j
@Tag(name = "学生模块")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    private static final List<String> ALLOWED_RESUME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Operation(summary = "学生注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody StudentRegisterDTO dto) {
        studentService.register(dto);
        return Result.ok("注册成功");
    }

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<Student> getProfile() {
        Long studentId = UserContext.getUserId();
        Student student = studentService.getById(studentId);
        if (student == null) {
            throw BusinessException.of("用户不存在");
        }
        student.setPassword(null);
        return Result.ok(student);
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Student student) {
        student.setId(UserContext.getUserId());
        studentService.updateProfile(student);
        return Result.ok("更新成功");
    }

    @Operation(summary = "分页查询学生列表（学院/管理员用）")
    @GetMapping("/list")
    public Result<PageResult<Student>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) String keyword) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (collegeId != null) wrapper.eq(Student::getCollegeId, collegeId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Student::getRealName, keyword)
                    .or().like(Student::getStudentNo, keyword));
        }
        wrapper.orderByDesc(Student::getCreateTime);
        Page<Student> studentPage = studentService.page(new Page<>(page, size), wrapper);
        studentPage.getRecords().forEach(s -> s.setPassword(null));
        return Result.ok(PageResult.of(studentPage));
    }

    @Operation(summary = "上传简历")
    @PostMapping("/resume/upload")
    public Result<Map<String, String>> uploadResume(@RequestParam("file") MultipartFile file) {
        log.info("开始处理简历上传，文件名: {}, 大小: {}, 类型: {}",
            file.getOriginalFilename(), file.getSize(), file.getContentType());

        if (file.isEmpty()) {
            return Result.fail("请选择要上传的文件");
        }

        // 检查文件大小 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.fail("文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (!ALLOWED_RESUME_TYPES.contains(contentType)) {
            log.warn("不支持的文件类型: {}", contentType);
            return Result.fail("仅支持 PDF、Word 格式的简历文件");
        }

        try {
            // 创建上传目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path targetDir = basePath.resolve("resumes").resolve(dateDir);

            log.info("上传目录: {}", targetDir);

            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                log.info("创建目录成功: {}", targetDir);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 保存文件
            Path targetPath = targetDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());

            // 构建访问URL
            String fileUrl = "/uploads/resumes/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            log.info("简历上传成功: {}", fileUrl);
            return Result.ok(result);

        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传未知错误: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return Result.fail("文件上传失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar/upload")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("头像上传，文件名: {}, 大小: {}, 类型: {}",
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
            Path targetDir = basePath.resolve("avatars").resolve(dateDir);

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

            String fileUrl = "/uploads/avatars/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);

            log.info("头像上传成功: {}", fileUrl);
            return Result.ok(result);

        } catch (IOException e) {
            log.error("头像上传失败: {}", e.getMessage(), e);
            return Result.fail("图片上传失败: " + e.getMessage());
        }
    }
}
