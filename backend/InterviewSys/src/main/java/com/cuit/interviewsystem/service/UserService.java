package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.company.CommonUserRegister;
import com.cuit.interviewsystem.model.dto.user.UserLoginDto;
import com.cuit.interviewsystem.model.dto.user.UserPageDto;
import com.cuit.interviewsystem.model.dto.user.UserRegisterDto;
import com.cuit.interviewsystem.model.dto.user.UsersAddDto;
import com.cuit.interviewsystem.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jiefe
* @description 针对表【t_user(系统用户表)】的数据库操作Service
* @createDate 2026-01-09 16:15:57
*/
public interface UserService extends IService<User> {
    long sysAdminRegister(UserRegisterDto userRegisterDto);

    User login(UserLoginDto userLoginDto);

    boolean usersAdd(UsersAddDto usersAddDto);

    User getOneUser(User user);

    long addOneUser(User user);

    int deleteOneUserById(Long delUserId);

    int updateOneUser(Long id, User user);

    long compUserRegister(UserRegisterDto userRegisterDto);
    long commonUserRegister(CommonUserRegister cur);

    Page<User> getUsers(UserPageDto conditions);
}
