package com.campus.recruitment.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注：文件访问由 FileController 处理
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 文件访问已移至 FileController
}
