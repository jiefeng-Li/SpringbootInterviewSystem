package com.cuit.interviewsystem.controller;


import cn.hutool.core.util.ObjUtil;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.UserLoginDto;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserAccountStatusEnum;
import com.cuit.interviewsystem.model.vo.UserLoginVo;
import com.cuit.interviewsystem.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@Slf4j
@RestController
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/user")
    public Result sysAdminRegister(UserRegisterDto userRegisterDto) {
        long userId = userService.sysAdminRegister(userRegisterDto);
        return Result.success(userId);
    }

    @GetMapping("/user")
    public Result<UserLoginVo> login(UserLoginDto userLoginDto) {
        User user = userService.login(userLoginDto);
        if (ObjUtil.isEmpty(user)) {
            return Result.error(ErrorEnum.PARAMS_ERROR, "用户名或密码错误");
        }
        if (user.getIsDeleted() == 1) {
            return Result.error(ErrorEnum.PARAMS_ERROR, "该用户已被删除");
        }
        if (Objects.equals(user.getAccountStatus(), UserAccountStatusEnum.BANED.getStatus())) {
            return Result.error(ErrorEnum.PARAMS_ERROR, "该用户已被封禁");
        }
        if (Objects.equals(user.getAccountStatus(), UserAccountStatusEnum.LOCKED.getStatus())) {
            return Result.error(ErrorEnum.PARAMS_ERROR, "该用户已被锁定");
        }
        UserLoginVo res = new UserLoginVo();
        BeanUtils.copyProperties(user, res);
        return Result.success(res);
    }
}
