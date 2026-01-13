package com.cuit.interviewsystem.service;

import com.cuit.interviewsystem.model.dto.UserLoginDto;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.dto.UsersAddDto;
import com.cuit.interviewsystem.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.UsersAddVo;

/**
* @author jiefe
* @description 针对表【t_user(系统用户表)】的数据库操作Service
* @createDate 2026-01-09 16:15:57
*/
public interface UserService extends IService<User> {
    long sysAdminRegister(UserRegisterDto userRegisterDto);

    User login(UserLoginDto userLoginDto);

    UsersAddDto usersAdd(UsersAddDto usersAddDto);
}
