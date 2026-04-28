package com.campus.recruitment.common.utils;

import com.campus.recruitment.common.exception.BusinessException;
import lombok.Data;

/**
 * 用户上下文工具类（存储当前登录用户信息）
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    public static UserInfo getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static Long getUserId() {
        UserInfo user = getUser();
        if (user == null) {
            throw BusinessException.unauthorized();
        }
        return user.getId();
    }

    public static String getRole() {
        UserInfo user = getUser();
        if (user == null) {
            throw BusinessException.unauthorized();
        }
        return user.getRole();
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;  // STUDENT / COMPANY / COLLEGE / ADMIN
    }
}
