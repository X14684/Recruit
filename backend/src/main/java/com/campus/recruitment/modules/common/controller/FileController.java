package com.campus.recruitment.modules.common.controller;

import com.campus.recruitment.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
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
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}") // 默认10MB
    private Long maxFileSize;

    // 允许的文件类型
    private static final List<String> ALLOWED_RESUME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * 上传简历
     */
    @PostMapping("/upload/resume")
    public Result<Map<String, String>> uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        log.info("=== 开始处理简历上传 ===");
        log.info("文件信息: name={}, size={}, contentType={}", 
            file.getName(), file.getSize(), file.getContentType());
        log.info("原始文件名: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            log.warn("文件为空");
            return Result.fail("请选择要上传的文件");
        }

        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            log.warn("文件大小超过限制: {} > {}", file.getSize(), maxFileSize);
            return Result.fail("文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        log.info("文件Content-Type: {}", contentType);
        if (!ALLOWED_RESUME_TYPES.contains(contentType)) {
            log.warn("文件类型不支持: {}", contentType);
            return Result.fail("仅支持 PDF、Word 格式的简历文件");
        }

        try {
            // 创建上传目录（使用绝对路径）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            
            // 关键修复：直接使用 Paths.get 获取绝对路径
            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path resumesDir = basePath.resolve("resumes");
            Path targetDir = resumesDir.resolve(dateDir);
            
            log.info("上传路径配置: {}", uploadPath);
            log.info("基础目录: {}", basePath);
            log.info("目标目录: {}", targetDir);
            
            // 确保目录存在
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
            log.info("开始保存文件到: {}", targetPath);
            
            file.transferTo(targetPath.toFile());
            
            log.info("文件保存成功，大小: {}", Files.size(targetPath));

            // 构建访问URL
            String fileUrl = "/uploads/resumes/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            log.info("简历上传成功: {}", fileUrl);
            log.info("=== 简历上传完成 ===");
            return Result.ok(result);

        } catch (IOException e) {
            log.error("文件上传IO错误: {}", e.getMessage(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传未知错误: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return Result.fail("文件上传失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Result.fail("请选择要上传的图片");
        }

        // 检查文件大小 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.fail("图片大小不能超过2MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return Result.fail("仅支持 JPG、PNG、GIF、WebP 格式的图片");
        }

        try {
            // 创建上传目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path targetDir = Paths.get(uploadPath, "avatars", dateDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 生成文件名
            String extension = ".jpg";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 保存文件
            Path targetPath = targetDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());

            // 构建访问URL
            String fileUrl = "/uploads/avatars/" + dateDir + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", originalFilename);

            log.info("头像上传成功: {}", fileUrl);
            return Result.ok(result);

        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.fail("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 文件预览/下载
     * URL: /uploads/resumes/202604/xxx.pdf  ->  /api/common/file?path=resumes/202604/xxx.pdf
     */
    @GetMapping("/file")
    public ResponseEntity<Resource> serveFile(@RequestParam("path") String filePath) {
        log.info("文件访问请求: path={}", filePath);
        
        try {
            // 获取上传目录的绝对路径
            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path targetFile = basePath.resolve(filePath).normalize();
            
            // 安全检查：确保文件在上传目录内
            if (!targetFile.startsWith(basePath)) {
                log.warn("非法文件路径访问: {}", targetFile);
                return ResponseEntity.status(403).build();
            }
            
            if (!Files.exists(targetFile) || !Files.isRegularFile(targetFile)) {
                log.warn("文件不存在: {}", targetFile);
                return ResponseEntity.notFound().build();
            }
            
            File file = targetFile.toFile();
            
            // 确定 Content-Type
            String contentType;
            try {
                contentType = Files.probeContentType(targetFile);
            } catch (IOException e) {
                contentType = null;
            }
            if (contentType == null) {
                // 根据扩展名判断
                String filename = file.getName().toLowerCase();
                if (filename.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (filename.endsWith(".doc")) {
                    contentType = "application/msword";
                } else if (filename.endsWith(".docx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (filename.endsWith(".webp")) {
                    contentType = "image/webp";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            
            String filename = file.getName();
            
            log.info("文件访问成功: {} -> {}", filePath, contentType);
            
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("文件访问失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
