package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.*;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserAccountStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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

    private final Digester digester = new Digester(DigestAlgorithm.SHA512);

    @Value("${const-var.salt}")
    private String SALT;

    @Value("${const-var.password-match-rule}")
    private String PASSWORD_MATCH_RULE;
    @Value("${const-var.email-match-rule}")
    private String EMAIL_MATCH_RULE;
    /**
     * 系统管理员注册
     * @param userRegisterDto
     * @return 用户id
     */
    @Override
    public long sysAdminRegister(UserRegisterDto userRegisterDto) {
        //数据校验
        User admin = new User();
        BeanUtils.copyProperties(userRegisterDto, admin);
        admin.setRole(UserRoleEnum.SYS_ADMIN.getValue());
        this.objectCheck(admin);
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
        User compUser = new User();
        BeanUtils.copyProperties(userRegisterDto, compUser);
        compUser.setRole(UserRoleEnum.COMP_ADMIN.getValue());
        this.objectCheck(compUser);
        //密码加密
        compUser.setPassword(digester.digestHex(compUser.getPassword() + SALT));
        //插入数据库
        return userMapper.insert(compUser);
    }

    @Override
    public long commonUserRegister(CommonUserRegister cur) {
        User u = new User();
        BeanUtils.copyProperties(cur, u);
        this.objectCheck(u);
        //密码加密
        u.setPassword(digester.digestHex(u.getPassword() + SALT));
        //插入数据库
        return userMapper.insert(u);
    }

    @Override
    public Page<User> getUsers(UserPageDto conditions) {
        Long pageSize = conditions.getPageSize();
        Long pageNum = conditions.getPageNum();
        // 校验数据
        ThrowUtil.throwIfTure(conditions.getIsDeleted() == null ||
                conditions.getIsDeleted() != 0 && conditions.getIsDeleted() != 1, ErrorEnum.PARAMS_ERROR);
        ThrowUtil.throwIfTure(pageSize == null || pageNum == null || pageSize <= 0, ErrorEnum.PARAMS_ERROR);
        ThrowUtil.throwIfTure(pageSize > 1000, ErrorEnum.PARAMS_ERROR.getCode(), "每页最多1000条数据");
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        UserAccountStatusEnum status = UserAccountStatusEnum.getEnumByStatus(conditions.getAccountStatus());

        lambdaQueryWrapper
                //查询用户名不为空则模糊查询
                .like(StrUtil.isNotBlank(conditions.getUsername()), User::getUsername, conditions.getUsername())
                //查询邮箱的用户
                .eq(StrUtil.isNotBlank(conditions.getEmail()), User::getEmail, conditions.getEmail())
                //手机号查询
                .eq(StrUtil.isNotBlank(conditions.getPhone()), User::getPhone, conditions.getPhone())
                //查询指定账号状态
                .eq(conditions.getAccountStatus() != null, User::getAccountStatus, conditions.getAccountStatus())
                //查询指定公司
                .eq(conditions.getCompanyId() != null, User::getCompanyId, conditions.getCompanyId())
                //查询删除或已删除的用户 -- 必须指定
                .eq(User::getIsDeleted, conditions.getIsDeleted());
        UserRoleEnum role = UserRoleEnum.getRole(conditions.getRole());
        if (role != null)
            lambdaQueryWrapper.eq(User::getRole, role.getValue());
        return userMapper.selectPage(page, lambdaQueryWrapper);
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
            CompletableFuture.runAsync(() -> userMapper.updateById(user));
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
        //数据校验
        this.objectCheck(user);
        //密码加密
        user.setPassword(digester.digestHex(user.getPassword() + SALT));
        //插入数据库，返回用户id
        return userMapper.insert(user);
    }

    @Override
    //TODO 待重构
    public int deleteOneUserById(Long delUserId) {
        ThrowUtil.throwIfTure(delUserId  == null || delUserId <= 0,
                ErrorEnum.PARAMS_ERROR);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        UserRoleEnum optRole = UserRoleEnum.getEnumByValue(jwtUtil.parse(token, JWTUtil.ELEMENT_ROLE));
        User delUser = userMapper.selectById(delUserId);
        User optUser = userMapper.selectById(jwtUtil.parse(token, JWTUtil.ELEMENT_USER_ID));
        //非管理员角色，管理员能够自由删除。
        ThrowUtil.throwIfTure(ObjUtil.isEmpty(delUser),
                ErrorEnum.PARAMS_ERROR.getCode(), "用户不存在");
        ThrowUtil.throwIfTure(delUser.getIsDeleted() == 1,
                ErrorEnum.PARAMS_ERROR.getCode(), "用户已注销");
        if (!UserRoleEnum.SYS_ADMIN.equals(optRole)) {
            // 若为公司管理员角色
            if (UserRoleEnum.COMP_ADMIN.equals(optRole)) {
                // 公司管理员不能删除系统管理员，即便系统管理员的公司id与公司管理员的公司id相同
                // 若被删除的用户与公司管理员的公司id不同则无权限
                ThrowUtil.throwIfTure(Objects.equals(UserRoleEnum.getEnumByValue(delUser.getRole()), UserRoleEnum.SYS_ADMIN)
                                && !Objects.equals(optUser.getCompanyId(), delUser.getCompanyId()),
                    ErrorEnum.PARAMS_ERROR.getCode(), "无权限删除该用户");
            } else {
            // 其他角色则只能删除（账号注销）自己
                ThrowUtil.throwIfTure(!Objects.equals(optUser.getUserId(), delUser.getUserId()),
                        ErrorEnum.PARAMS_ERROR.getCode(),
                        "无权限删除该用户");
            }
        }
        delUser.setIsDeleted(1);
        delUser.setUpdateTime(new Date());
        if (UserRoleEnum.SYS_ADMIN.equals(optRole)) {
            delUser.setEditTime(new Date());
        }
        return userMapper.updateById(delUser);
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
        this.objectCheck(user);
        //获取当前操作用户，若为管理员则需要修改editTime字段
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        //权限校验，若权限不足则无法修改用户角色、账号状态字段。
        User old = userMapper.selectById(id);
        User optUser = userMapper.selectById(jwtUtil.parse(token, JWTUtil.ELEMENT_USER_ID));
        if (UserRoleEnum.SYS_ADMIN.getValue().equals(optUser.getRole())) {
            user.setEditTime(new Date());
        } else if (UserRoleEnum.COMP_ADMIN.getValue().equals(optUser.getRole())) {
            // 企业管理员无法修改不属于本企业的用户数据
            ThrowUtil.throwIfTure(!Objects.equals(old.getCompanyId(), user.getCompanyId()),
                    ErrorEnum.UNAUTHORIZED);
            // 企业管理员无法修改用户角色为系统管理员
            ThrowUtil.throwIfTure(UserRoleEnum.SYS_ADMIN.equals(UserRoleEnum.getRole(user.getRole())),
                    ErrorEnum.UNAUTHORIZED);
            // 企业管理员无法修改同公司用户状态为求职者
            ThrowUtil.throwIfTure(UserRoleEnum.JOB_SEEKER.equals(UserRoleEnum.getRole(user.getRole())),
                    ErrorEnum.UNAUTHORIZED);
            // 企业管理员无法修改其他管理员的信息
            ThrowUtil.throwIfTure(UserRoleEnum.COMP_ADMIN.equals(UserRoleEnum.getRole(old.getRole()))
                            && !Objects.equals(old.getUserId(), optUser.getUserId())
                            || UserRoleEnum.SYS_ADMIN.equals(UserRoleEnum.getRole(old.getRole())),
                    ErrorEnum.UNAUTHORIZED);
            user.setEditTime(new Date());
        } else {
            ThrowUtil.throwIfTure(!Objects.equals(old.getUserId(), user.getUserId()),
                    ErrorEnum.UNAUTHORIZED);
        }
        user.setPassword(digester.digestHex(user.getPassword() + SALT));
        user.setUpdateTime(new Date());
        return userMapper.updateById(user);
    }

    private void objectCheck(User user) {
        //region 1. 实体数据校验
        ThrowUtil.throwIfTure(
                ObjUtil.isEmpty(user),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTure(
                StrUtil.isBlankIfStr(user.getUsername()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名不能为空");
        ThrowUtil.throwIfTure(
                user.getUsername().length() > 45,
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户名过长");
        ThrowUtil.throwIfTure(
                !user.getPassword().matches(PASSWORD_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "密码不符合规则");
        ThrowUtil.throwIfTure(
                !PhoneUtil.isPhone(user.getPhone()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "请输入正确的手机号");
        ThrowUtil.throwIfTure(
                user.getEmail() != null && !user.getEmail().matches(EMAIL_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "请输入正确的邮箱");
        //TODO 公司id校验
        /*
        ThrowUtil.throwIfTure(
                user.getCompanyId() != null && ！companyMapper.exist(....),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "所属公司不存在");
        */
        ThrowUtil.throwIfTure(
                StrUtil.isBlankIfStr(user.getRole()),
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户角色不能为空"
        );
        UserRoleEnum role = UserRoleEnum.getRole(user.getRole());
        //校验用户角色的同时，并设置规范使用UserRoleEnum的value值
        ThrowUtil.throwIfTure(
                role == null,
                ErrorEnum.PARAMS_ERROR.getCode(),
                "用户角色不存在");
        user.setRole(role.getValue());
        //endregion
        //region 2. 数据库字段校验
        ThrowUtil.throwIfTure(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername())
                        .eq(User::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR.getCode(), "用户名已存在");
        ThrowUtil.throwIfTure(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, user.getPhone())
                        .eq(User::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR.getCode(), "手机号已注册");
        ThrowUtil.throwIfTure(user.getEmail() != null &&
                        userMapper.exists(new LambdaQueryWrapper<User>()
                                .eq(User::getEmail, user.getEmail())
                                .eq(User::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR.getCode(), "邮箱已注册");
        //endregion
    }
}