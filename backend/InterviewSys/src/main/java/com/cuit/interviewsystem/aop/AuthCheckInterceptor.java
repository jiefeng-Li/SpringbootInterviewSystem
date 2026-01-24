package com.cuit.interviewsystem.aop;


import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class AuthCheckInterceptor {
    @Resource
    private JWTUtil jwtUtil;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        List<UserRoleEnum> mustRoles = Arrays.stream(authCheck.roles()).toList();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        log.info("token:{}", token);
        // 如果不需要权限，放行
        if (mustRoles.isEmpty()) {
            return joinPoint.proceed();
        }
        // 以下的代码：必须有权限，才会通过
        // token为空或校验不通过
        ThrowUtil.throwIfTure(token == null || !jwtUtil.verify(token), ErrorEnum.NOT_LOGIN_ERROR);
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(jwtUtil.parse(token, JWTUtil.ELEMENT_ROLE));
        // 要求权限与登录用户的权限不同
        //可能存在多种角色访问同一接口的情况
        ThrowUtil.throwIfTure(!UserRoleEnum.SYS_ADMIN.equals(userRoleEnum)
                || !mustRoles.contains(userRoleEnum), ErrorEnum.UNAUTHORIZED);
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}
