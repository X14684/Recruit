package com.campus.recruitment.common.exception;

import com.campus.recruitment.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String msg = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.fail(400, msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String msg = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.fail(400, msg);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("数据完整性异常: ", e);
        // 处理唯一键冲突
        String message = e.getMessage();
        if (message != null && (message.contains("Duplicate entry") || message.contains("Duplicate"))) {
            return Result.fail("数据已存在，请检查重复项");
        }
        return Result.fail("数据操作失败，请检查数据完整性");
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Result<?> handleRedisConnection(RedisConnectionFailureException e) {
        log.error("Redis连接异常: ", e);
        return Result.fail("服务暂时不可用，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {} | 类型: {} | 位置: {}", e.getMessage(), e.getClass().getName(), getStackTraceLocation(e), e);
        return Result.fail("系统繁忙，请稍后重试: " + e.getClass().getSimpleName());
    }
    
    private String getStackTraceLocation(Exception e) {
        if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
            StackTraceElement first = e.getStackTrace()[0];
            return first.getClassName() + "." + first.getMethodName() + ":" + first.getLineNumber();
        }
        return "unknown";
    }
}

