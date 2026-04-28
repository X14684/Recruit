package com.campus.recruitment.common.config;

import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.utils.JwtUtils;
import com.campus.recruitment.common.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @NonNull
    private final JwtUtils jwtUtils;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 白名单：不需要认证的路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/**",              // 登录相关（如果有的话）
            "/admin/login",          // 统一登录
            "/admin/reset-password", // 重置密码（临时）
            "/admin/statistics",     // 首页公开统计数据
            "/student/register",     // 学生注册
            "/company/register",     // 企业注册
            "/company/list",         // 企业列表（公开浏览）
            "/job/list",             // 职位列表（公开）
            "/job/*",                // 职位详情（公开）
            "/college/list",         // 学院列表（公开）
            "/college/talk/list",    // 宣讲会列表（公开）
            "/common/file",          // 文件预览/下载（公开，URL 为随机哈希）
            "/actuator/**",          // 健康检查
            "/error"                 // 错误页面
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String rawPath = request.getRequestURI();
        
        // 剥除 context-path（嵌入式容器 getContextPath() 可能返回空串，也可能返回 /api）
        String contextPath = request.getContextPath();
        String path;
        if (StringUtils.hasText(contextPath) && rawPath.startsWith(contextPath)) {
            path = rawPath.substring(contextPath.length());
        } else {
            path = rawPath;
        }
        // 兜底：如果 path 还是以 /api 开头，再剥一次（context-path 未生效时）
        if (path.startsWith("/api/")) {
            path = path.substring(4); // 去掉 /api，保留后面的 /xxx
        }
        
        // 特殊处理：/company/{id} 是公开的，但 /company/profile 需要认证
        if (path.matches("^/company/\\d+$")) {
            return true;  // 公开访问
        }
        
        // 检查是否在白名单中
        for (String pattern : WHITE_LIST) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        String requestUri = request.getRequestURI();
        
        if (!StringUtils.hasText(token)) {
            // 没有Token，返回401
            log.warn("请求未携带Token: {}, Authorization Header: {}", requestUri, request.getHeader("Authorization"));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
            return;
        }
        
        log.info("收到Token请求: {}, Token前缀: {}", requestUri, token.substring(0, Math.min(20, token.length())));
        
        try {
            Claims claims = jwtUtils.parseToken(token);
            UserContext.UserInfo userInfo = new UserContext.UserInfo();
            
            // 安全处理：防止空指针
            Object id = claims.get("id");
            if (id == null) {
                throw new BusinessException("Token缺少用户ID");
            }
            userInfo.setId(Long.parseLong(id.toString()));
            
            Object username = claims.get("username");
            if (username == null) {
                throw new BusinessException("Token缺少用户名");
            }
            userInfo.setUsername(username.toString());
            
            Object role = claims.get("role");
            if (role == null) {
                throw new BusinessException("Token缺少角色信息");
            }
            userInfo.setRole(role.toString());
            
            log.info("Token解析成功: userId={}, username={}, role={}", userInfo.getId(), userInfo.getUsername(), userInfo.getRole());
            UserContext.setUser(userInfo);
        } catch (BusinessException e) {
            log.warn("Token无效，拒绝请求: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"" + e.getMessage() + "\"}");
            return;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期\"}");
            return;
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
