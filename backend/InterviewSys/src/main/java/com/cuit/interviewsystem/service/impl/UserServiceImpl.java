package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.UserLoginDto;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.dto.UsersAddDto;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    @Resource
    private JWTUtil jwtUtil;

    private Digester digester = new Digester(DigestAlgorithm.SHA512);

    @Value("${const-var.salt}")
    private String SALT;

    @Value("${const-var.password-match-rule}")
    private String PASSWORD_MATCH_RULE;

    /**
     * 系统管理员注册
     * @param userRegisterDto
     * @return 用户id
     */
    @Override
    public long sysAdminRegister(UserRegisterDto userRegisterDto) {
        //数据校验
        this.objectCheck(userRegisterDto);
        //创建用户对象
        User admin = new User();
        BeanUtils.copyProperties(userRegisterDto, admin);
        //设置角色
        admin.setRole(UserRoleEnum.SYS_ADMIN.getValue());
        //密码加密
        admin.setPassword(digester.digestHex(admin.getPassword() + SALT));
        //插入数据库
        long userId = userMapper.insert(admin);
        log.info(admin.toString());
        log.info("userid is {}", userId);
        //返回用户id
        return userId;
    }

    @Override
    public long compUserRegister(UserRegisterDto userRegisterDto) {
        this.objectCheck(userRegisterDto);
        //创建用户对象
        User compUser = new User();
        BeanUtils.copyProperties(userRegisterDto, compUser);
        //设置角色
        compUser.setRole(UserRoleEnum.RECRUITER.getValue());
        //密码加密
        compUser.setPassword(digester.digestHex(compUser.getPassword() + SALT));
        //插入数据库
        return userMapper.insert(compUser);
    }

    @Override
    public long commonUserRegister(UserRegisterDto userRegisterDto) {
        this.objectCheck(userRegisterDto);
        //创建用户对象
        User commonUser = new User();
        BeanUtils.copyProperties(userRegisterDto, commonUser);
        //设置角色
        commonUser.setRole(UserRoleEnum.JOB_SEEKER.getValue());
        //密码加密
        commonUser.setPassword(digester.digestHex(commonUser.getPassword() + SALT));
        //插入数据库
        return userMapper.insert(commonUser);
    }

    /**
     * 通用用户登录
     *
     * @param userLoginDto
     * @return
     */
    @Override
    public User login(UserLoginDto userLoginDto) {
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(userLoginDto),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));

        ThrowUtil.throwIfTure(
                StrUtil.hasBlank(userLoginDto.getAccount(), userLoginDto.getPassword()),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空")
                );
        userLoginDto.setPassword(digester.digestHex(userLoginDto.getPassword() + SALT));
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
        lambdaQueryWrapper.eq(User::getPassword, userLoginDto.getPassword())
                .and(i -> i.eq(User::getPhone, userLoginDto.getAccount())
                        .or().eq(User::getUsername, userLoginDto.getAccount())
                        .or().eq(User::getEmail, userLoginDto.getAccount()));
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (!ObjUtil.isEmpty(user)) {
            user.setLastLoginTime(new Date());
            //异步更新登录时间
            CompletableFuture.runAsync(() -> {
                userMapper.updateById(user);
            });
        }
        return user;
    }
    @Transactional
    @Override
    public boolean usersAdd(UsersAddDto usersAddDto) {
        //TODO 待重构
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(usersAddDto)
                        || ObjUtil.isEmpty(usersAddDto.getCnt())
                        || ObjUtil.isEmpty(usersAddDto.getCnt()),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTure(
                usersAddDto.getCnt() != usersAddDto.getList().size(),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据输入异常")
        );
        //数据校验start
        int cnt = usersAddDto.getCnt();
        List<String> uniqueUsernameList = usersAddDto.getList().stream()
                .map(User::getUsername).filter(name -> !StrUtil.isBlankIfStr(name))//过滤掉空字符串
                .distinct().toList();
        List<String> uniquePhoneList = usersAddDto.getList().stream()
                .map(User::getPhone).filter(phone -> !StrUtil.isBlankIfStr(phone))//过滤掉空字符串
                .distinct().toList();
        List<String> passwordList = usersAddDto.getList().stream()
                .map(User::getPassword).filter(p -> !StrUtil.isBlankIfStr(p))//过滤掉空字符串
                .toList();
        List<String> uniqueEmailList = usersAddDto.getList().stream()
                .map(User::getEmail).filter(e -> !StrUtil.isBlankIfStr(e))//过滤掉空字符串
                .distinct().toList();
        ThrowUtil.throwIfTure(
                uniqueUsernameList.size() != cnt,
                new BusinessException(ErrorEnum.PARAMS_ERROR, "用户名重复或为空"));
        ThrowUtil.throwIfTure(uniquePhoneList.size() != cnt,
                new BusinessException(ErrorEnum.PARAMS_ERROR, "手机号重复或为空"));
        ThrowUtil.throwIfTure(uniqueEmailList.size() != cnt,
                new BusinessException(ErrorEnum.PARAMS_ERROR, "邮箱重复或为空"));
        for (String password : passwordList) {
            ThrowUtil.throwIfTure(
                    StrUtil.isBlankIfStr(password),
                    new BusinessException(ErrorEnum.PARAMS_ERROR, "密码不能为空"));
            //密码长度在8-20，只包含且至少包含一个数字字母与一个数字，
            ThrowUtil.throwIfTure(
                    !password.matches(PASSWORD_MATCH_RULE),
                    ErrorEnum.PARAMS_ERROR.getCode(),
                    "密码不符合规则"); //待抽取为常量
        }
        //数据校验end

        /**
         * 批量插入
         * saveBatch()方法会自动将传入的List<User>中的User对象插入到数据库中
         */
        boolean b = this.saveBatch(usersAddDto.getList());
        ThrowUtil.throwIfTure(!b, new BusinessException(ErrorEnum.SYSTEM_ERROR, "用户批量插入失败"));
        return true;
    }

    @Override
    public User getOneUser(User user) {
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(user),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
        lambdaQueryWrapper.eq(user.getUserId() != null, User::getUserId, user.getUserId())
                .or().eq(user.getUsername() != null, User::getUsername, user.getUsername())
                .or().eq(user.getPhone() != null, User::getPhone, user.getPhone())
                .or().eq(user.getEmail() != null, User::getEmail, user.getEmail())
                .or().eq(user.getAccountStatus() != null, User::getAccountStatus, user.getAccountStatus())
                .or().eq(user.getIsDeleted() != null, User::getIsDeleted, user.getIsDeleted())
                .or().eq(user.getCompanyId() != null, User::getCompanyId, user.getCompanyId());
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public long addOneUser(User user) {
        //region 数据校验
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(user),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTure(
                StrUtil.isBlankIfStr(user.getUsername()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名不能为空");
        //密码长度在8-20，只包含且至少包含一个数字字母与一个数字
        ThrowUtil.throwIfTure(
                StrUtil.isBlankIfStr(user.getPassword()) || !user.getPassword().matches(PASSWORD_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "密码不符合规则"); //待抽取为常量

        ThrowUtil.throwIfTure(
                !PhoneUtil.isPhone(user.getPhone()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "请输入正确的手机号");

        ThrowUtil.throwIfTure(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, user.getPhone())
                        .or().eq(User::getUsername, user.getUsername())
                        .or().eq(!StrUtil.isBlankIfStr(user.getEmail()), User::getEmail, user.getEmail())),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名/手机号已存在"
        );
        //以上同系统用户注册的数据校验
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(user.getRole()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户角色不能为空"
        );
        //根据传入的角色找到对应的UserRoleEnum，不存在valueRole和textRole都不为空的情况
        UserRoleEnum valueRole = UserRoleEnum.getEnumByValue(user.getRole());
        UserRoleEnum textRole = UserRoleEnum.getEnumByText(user.getRole());
        ThrowUtil.throwIfTure(
                valueRole == null && textRole == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "用户角色不存在"
                );
        //endregion
        //规范存储UserRoleEnum的value值
        user.setRole(valueRole == null ? textRole.getValue() : valueRole.getValue());
        //密码加密
        user.setPassword(digester.digestHex(user.getPassword() + SALT));
        //插入数据库
        long userId = userMapper.insert(user);
        //返回用户id
        return userId;
    }

    @Override
    public int deleteOneUserById(Long delUserId) {
        ThrowUtil.throwIfTure(delUserId  == null || delUserId <= 0,
                ErrorEnum.PARAMS_ERROR);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        UserRoleEnum optRole = UserRoleEnum.getEnumByValue(jwtUtil.parse(token, JWTUtil.ELEMENT_ROLE));
        User delUser = userMapper.selectById(delUserId);
        User optUser = userMapper.selectById(jwtUtil.parse(token, JWTUtil.ELEMENT_USER_ID));
        if (UserRoleEnum.COMP_ADMIN.equals(optRole)) {
            ThrowUtil.throwIfTure(ObjUtil.isEmpty(delUser),
                    ErrorEnum.PARAMS_ERROR.getCode(), "用户不存在");
            ThrowUtil.throwIfTure(!Objects.equals(optUser.getCompanyId(), delUser.getCompanyId()),
                    ErrorEnum.PARAMS_ERROR.getCode(), "无权限删除该用户");
        }
        return userMapper.deleteById(delUserId);
    }

    @Override
    public int updateOneUser(Long id, User user) {
        ThrowUtil.throwIfTure(id == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "路径请求参数为空");
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(user),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTure(!id.equals(user.getUserId()),
                ErrorEnum.PARAMS_ERROR.getCode(), "路径参数与请求体数据不匹配");
        ThrowUtil.throwIfTure(
                !user.getPassword().matches(PASSWORD_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "密码不符合规则");
        ThrowUtil.throwIfTure(
                StrUtil.isBlankIfStr(user.getUsername()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名不能为空");
        ThrowUtil.throwIfTure(
                !PhoneUtil.isPhone(user.getPhone()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "请输入正确的手机号");
        //TODO 校验邮箱
        ThrowUtil.throwIfTure(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .ne(User::getUserId, user.getUserId())
                        .and(i -> i.eq(User::getUsername, user.getUsername())
                                .or().eq(User::getPhone, user.getPhone())
                                .or().eq(!StrUtil.isBlankIfStr(user.getEmail()), User::getEmail, user.getEmail()))),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名/手机号/邮箱已存在"
        );
        user.setPassword(digester.digestHex(user.getPassword() + SALT));
        user.setUpdateTime(new Date());
        //获取当前操作用户，若为管理员则需要修改editTime字段
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        String role = jwtUtil.parse(token, JWTUtil.ELEMENT_ROLE);
        if (UserRoleEnum.SYS_ADMIN.equals(UserRoleEnum.getEnumByValue(role))) {
            user.setEditTime(new Date());
        } else {
            //权限校验，若权限不足则无法修改用户角色字段。
            User old = userMapper.selectById(id);
            ThrowUtil.throwIfTure(
                    !Objects.equals(UserRoleEnum.getEnumByValue(old.getRole()),
                            UserRoleEnum.getEnumByValue(user.getRole())), ErrorEnum.UNAUTHORIZED);
        }
        return userMapper.updateById(user);
    }

    private void objectCheck(UserRegisterDto userRegisterDto) {
        //region 数据校验
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(userRegisterDto),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        //密码长度在8-20，只包含且至少包含一个数字字母与一个数字，
        ThrowUtil.throwIfTure(
                !userRegisterDto.getPassword().matches(PASSWORD_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "密码不符合规则"); //待抽取为常量

        ThrowUtil.throwIfTure(
                StrUtil.isBlankIfStr(userRegisterDto.getUsername()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名不能为空");

        ThrowUtil.throwIfTure(
                !PhoneUtil.isPhone(userRegisterDto.getPhone()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "请输入正确的手机号");

        ThrowUtil.throwIfTure(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, userRegisterDto.getPhone())
                        .eq(User::getUsername, userRegisterDto.getUsername())),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名/手机号已存在"
        );
        //endregion
    }

    private boolean objectCheck(User user) {
        return true;
    }
}