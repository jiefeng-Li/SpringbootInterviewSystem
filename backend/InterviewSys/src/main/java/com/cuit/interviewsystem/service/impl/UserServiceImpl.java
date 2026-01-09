package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author jiefe
* @description 针对表【t_user(系统用户表)】的数据库操作Service实现
* @createDate 2026-01-09 16:15:57
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public long userRegister(UserRegisterDto userRegisterDto) {
        if (ObjUtil.isEmpty(userRegisterDto)) {
            return 0;
        }
        if (!userRegisterDto.getPassword().matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]{8,20}$")) {
            return 0;
        }
        if (StrUtil.isBlankIfStr(userRegisterDto.getUsername())) {
            return 0;
        }
        if (!PhoneUtil.isPhone(userRegisterDto.getPhone())) {
            return 0;
        }
        //创建用户对象
        //插入数据库
        //返回用户id
        return 1;
    }
}




