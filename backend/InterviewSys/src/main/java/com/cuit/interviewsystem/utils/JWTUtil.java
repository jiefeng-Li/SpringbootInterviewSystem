package com.cuit.interviewsystem.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.cuit.interviewsystem.model.entity.User;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JWTUtil {
    public static final String ELEMENT_ROLE = "role";
    public static final String ELEMENT_USER_ID = "userId";
    public static final String ELEMENT_COMPANY_ID = "companyId";

    @Value("${const-var.jwt.secret-key}")
    private static String JWT_SECRET_KEY;
    @Value("${const-var.jwt.expire-days}")
    private static int JWT_EXP_OFFSET; // 获取当前时间并偏移配置的天数

    public static String sign(User user){
        Date expireDate = DateUtil.offsetDay(DateUtil.date(), JWT_EXP_OFFSET);
        return JWT.create()
                .setPayload(ELEMENT_USER_ID, user.getUserId())
                .setPayload(ELEMENT_ROLE, user.getRole())
                .setPayload(ELEMENT_COMPANY_ID, user.getCompanyId())
                .setExpiresAt(expireDate)
                .setKey(JWT_SECRET_KEY.getBytes())
                .sign();
    }


    public static boolean verify(String token){
        if (StrUtil.isBlankIfStr(token))
            return false; // 在核验jwt时，如果为空调用verify会抛出错误
        return JWT.of(token).setKey(JWT_SECRET_KEY.getBytes()).verify();
    }

    public static String parse(String token, String eleName){
        String res = null;
        if (verify(token)){
            res = JWT.of(token).getPayload(eleName).toString();
        }
        return res;
    }
}
