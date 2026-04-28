package com.campus.recruitment.common.interceptor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.recruitment.common.annotation.SensitiveField;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 敏感字段处理工具类
 * 自动将标记为@SensitiveField的字段设置为null
 */
public class SensitiveFieldInterceptor {

    /**
     * 获取查询结果后处理敏感字段
     * 建议在Service层使用工具方法处理
     */
    public static void handleSensitiveFields(Object result) {
        if (result == null) {
            return;
        }

        if (result instanceof List) {
            List<?> list = (List<?>) result;
            for (Object item : list) {
                maskSensitiveFields(item);
            }
        } else if (result instanceof IPage) {
            IPage<?> page = (IPage<?>) result;
            for (Object item : page.getRecords()) {
                maskSensitiveFields(item);
            }
        } else {
            maskSensitiveFields(result);
        }
    }

    /**
     * 隐藏对象中的敏感字段
     */
    private static void maskSensitiveFields(Object obj) {
        if (obj == null || obj instanceof Map) {
            return;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(SensitiveField.class)) {
                try {
                    field.setAccessible(true);
                    field.set(obj, null);
                } catch (IllegalAccessException e) {
                    // 忽略异常，静默处理
                }
            }
        }
    }
}
