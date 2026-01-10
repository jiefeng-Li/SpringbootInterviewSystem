package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
* @author jiefe
* @description 针对表【t_user(系统用户表)】的数据库操作Service实现
* @createDate 2026-01-09 16:15:57
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    private Digester digester = new Digester(DigestAlgorithm.SHA512);

    @Value("${const-var.salt}")
    private String SALT;
    @Override
    public long userRegister(UserRegisterDto userRegisterDto) {
        if (ObjUtil.isEmpty(userRegisterDto)) {
            return 0;
        }
        //密码长度在8-20，只包含且至少包含一个数字字母与一个数字，
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
        User user = new User();
        BeanUtils.copyProperties(userRegisterDto, user);
        //设置角色
        user.setRole(UserRoleEnum.SYS_ADMIN.getValue());
        //密码加密
        user.setPassword(digester.digestHex(user.getPassword() + SALT));
        //插入数据库
        long userId = userMapper.insert(user);
        log.info(user.toString());
        log.info("userid is {}", userId);
        //返回用户id
        return userId;
    }
}




