package com.cuit.interviewsystem.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Date;


@Setter
@Component
public class JWTUtil {
    public static class ELEMENT {
        public static final String ROLE = "role";
        public static final String USER_ID = "userId";
        public static final String COMPANY_ID = "companyId";

        private ELEMENT(){}
    }

    @Value("${const-var.jwt.secret-key}")
    private String JWT_SECRET_KEY;
    @Value("${const-var.jwt.expire-days}")
    private int JWT_EXP_OFFSET; // 获取当前时间并偏移配置的天数

    public String sign(User user){
        Date expireDate = DateUtil.offsetDay(DateUtil.date(), JWT_EXP_OFFSET);
        return JWT.create()
                .setPayload(ELEMENT.USER_ID, user.getUserId())
                .setPayload(ELEMENT.ROLE, user.getRole())
                .setPayload(ELEMENT.COMPANY_ID, user.getCompanyId())
                .setExpiresAt(expireDate)
                .setKey(JWT_SECRET_KEY.getBytes())
                .sign();
    }


    public boolean verify(String token){
        if (StrUtil.isBlankIfStr(token))
            return false; // 在核验jwt时，如果为空调用verify会抛出错误
        return JWT.of(token).setKey(JWT_SECRET_KEY.getBytes()).verify();
    }

    public String parse(String token, String eleName){
        if (verify(token)){
            Object payload = JWT.of(token).getPayload(eleName);
            if (payload != null)
                return payload.toString();
        }
        return null;
    }

    public String getLoginUserInfo(String eleName){
        if (StrUtil.isBlank(eleName))
            return null;
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        ThrowUtil.throwIfTrue(StrUtil.isBlank(token), ErrorEnum.NOT_LOGIN_ERROR);
        return parse(token, eleName);
    }

    public User parseLoginUser() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        if (!verify(token))
            return null;
        User res = new User();
        res.setUserId(Long.parseLong(parse(token, ELEMENT.USER_ID)));
        res.setRole(parse(token, ELEMENT.ROLE));
        res.setCompanyId(Long.parseLong(parse(token, ELEMENT.COMPANY_ID)));
        return res;
    }

    public String resetToken(String eleName, String eleValue){
        User old = parseLoginUser();
        ThrowUtil.throwIfTrue(old == null, ErrorEnum.NOT_LOGIN_ERROR);
        if (ELEMENT.USER_ID.equals(eleName))
            old.setUserId(Long.parseLong(eleValue));
        if (ELEMENT.ROLE.equals(eleName))
            old.setRole(eleValue);
        if (ELEMENT.COMPANY_ID.equals(eleName))
            old.setCompanyId(Long.parseLong(eleValue));
        return sign(old);
    }
}
