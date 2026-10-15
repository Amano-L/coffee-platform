package com.coffee.common.util;

import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类
 */
public class SecurityUtil {

    // 获取当前登录用户 ID，未登录抛 401
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return (Long) authentication.getPrincipal();
    }
}