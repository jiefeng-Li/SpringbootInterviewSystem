package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.model.dto.company.CommonUserRegister;
import com.cuit.interviewsystem.model.dto.user.*;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserAccountStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.utils.AliOSSUtil;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.RedisUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    private CompanyMapper companyMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private AliOSSUtil aliOSSUtil;

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
        //插入数据库, 返回用户id
        return userMapper.insert(admin) > 0 ? admin.getUserId() : 0L;
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
        return userMapper.insert(compUser) > 0 ? compUser.getUserId() : 0L;
    }

    @Override
    public long commonUserRegister(CommonUserRegister cur) {
        User u = new User();
        BeanUtils.copyProperties(cur, u);
        this.objectCheck(u);
        //防止调用该接口来注册成为管理员
        if (u.getRole().equals(UserRoleEnum.COMP_ADMIN.getValue()) || u.getRole().equals(UserRoleEnum.SYS_ADMIN.getValue())) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "用户类型错误");
        }
        //密码加密
        u.setPassword(digester.digestHex(u.getPassword() + SALT));
        //插入数据库
        return userMapper.insert(u) > 0 ? u.getUserId() : 0L;
    }

    @Override
    public Page<User> getUsers(UserPageDto conditions) {
        Page<User> page = new Page<>(conditions.getPageNum(), conditions.getPageSize());
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

    @Override
    public User getCurrentUser() {
        Long userId = jwtUtil.parseLoginUser().getUserId();
        return userMapper.selectById(userId);
    }

    @Override
    public int deleteOneUserById(Long delUserId) {
        ThrowUtil.throwIfTrue(delUserId  == null || delUserId <= 0,
                ErrorEnum.PARAMS_ERROR);
        User u = jwtUtil.parseLoginUser();
        User optUser = userMapper.selectById(u.getUserId());
        UserRoleEnum optRole = UserRoleEnum.getEnumByValue(optUser.getRole());
        User delUser = userMapper.selectById(delUserId);

        ThrowUtil.throwIfTrue(ObjUtil.isEmpty(delUser),
                ErrorEnum.PARAMS_ERROR, "用户不存在");
        ThrowUtil.throwIfTrue(delUser.getIsDeleted() == 1,
                ErrorEnum.PARAMS_ERROR, "用户已注销");
        ThrowUtil.throwIfTrue(delUser.getUserId() != optUser.getUserId() && optUser.getRole() != UserRoleEnum.SYS_ADMIN.getValue(),
                ErrorEnum.UNAUTHORIZED, "无权删除用户");
        if (Objects.equals(delUser.getRole(), UserRoleEnum.COMP_ADMIN.getValue()) && delUser.getCompanyId() != null) {
            //如果该公司不存在其他未被删除,账号状态正常的管理员
            if (!userMapper.exists(new LambdaUpdateWrapper<User>()
                    .eq(User::getCompanyId, delUser.getCompanyId())
                    .ne(User::getUserId, delUser.getUserId())
                    .eq(User::getIsDeleted, 0)
                    .ne(User::getAccountStatus, UserAccountStatusEnum.NORMAL.getStatus()))
                    //并且该公司未注销
                    && !Objects.equals(companyMapper.selectById(delUser.getCompanyId()).getStatus(), CompanyStatusEnum.DEREGISTER.getStatus())) {
                throw new BusinessException(ErrorEnum.PARAMS_ERROR, "该公司不存在其他未注销,账号状态正常的管理员, 请先注销公司");
            }
        }
        delUser.setIsDeleted(1);
        delUser.setUpdateTime(LocalDate.now());
        if (UserRoleEnum.SYS_ADMIN.equals(optRole)) {
            delUser.setEditTime(LocalDate.now());
        }
        return userMapper.updateById(delUser);
    }

    /**
     * 通用用户登录
     *
     * @param userLoginDto
     * @return
     */
    @Override
    public User login(UserLoginDto userLoginDto) {
        ThrowUtil.throwIfTrue(
                ObjUtil.isEmpty(userLoginDto),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));

        ThrowUtil.throwIfTrue(
                StrUtil.hasBlank(userLoginDto.getAccount(), userLoginDto.getPassword()),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空")
                );
        userLoginDto.setPassword(digester.digestHex(userLoginDto.getPassword() + SALT));
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
        lambdaQueryWrapper.eq(User::getPassword, userLoginDto.getPassword())
                                .eq(User::getIsDeleted, 0)
                .and(i -> i.eq(User::getPhone, userLoginDto.getAccount())
                        .or().eq(User::getUsername, userLoginDto.getAccount())
                        .or().eq(User::getEmail, userLoginDto.getAccount()));
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (!ObjUtil.isEmpty(user)) {
                        ThrowUtil.throwIfTrue(!UserAccountStatusEnum.NORMAL.getStatus().equals(user.getAccountStatus()),
                                        ErrorEnum.UNAUTHORIZED, "账号状态异常，请联系管理员");
            user.setLastLoginTime(LocalDate.now());
            //异步更新登录时间
            CompletableFuture.runAsync(() -> userMapper.updateById(user));
        }
        return user;
    }
    @Transactional
    @Override
    public boolean usersAdd(UsersAddDto usersAddDto) {
        //TODO 待重构
        ThrowUtil.throwIfTrue(
                ObjUtil.isEmpty(usersAddDto)
                        || ObjUtil.isEmpty(usersAddDto.getCnt())
                        || ObjUtil.isEmpty(usersAddDto.getCnt()),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTrue(
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
        ThrowUtil.throwIfTrue(
                uniqueUsernameList.size() != cnt,
                new BusinessException(ErrorEnum.PARAMS_ERROR, "用户名重复或为空"));
        ThrowUtil.throwIfTrue(uniquePhoneList.size() != cnt,
                new BusinessException(ErrorEnum.PARAMS_ERROR, "手机号重复或为空"));
        ThrowUtil.throwIfTrue(uniqueEmailList.size() != cnt,
                new BusinessException(ErrorEnum.PARAMS_ERROR, "邮箱重复或为空"));
        for (String password : passwordList) {
            ThrowUtil.throwIfTrue(
                    StrUtil.isBlankIfStr(password),
                    new BusinessException(ErrorEnum.PARAMS_ERROR, "密码不能为空"));
            //密码长度在8-20，只包含且至少包含一个数字字母与一个数字，
            ThrowUtil.throwIfTrue(
                    !password.matches(PASSWORD_MATCH_RULE),
                    ErrorEnum.PARAMS_ERROR,
                    "密码不符合规则"); //待抽取为常量
        }
        //数据校验end

        /**
         * 批量插入
         * saveBatch()方法会自动将传入的List<User>中的User对象插入到数据库中
         */
        boolean b = this.saveBatch(usersAddDto.getList());
        ThrowUtil.throwIfTrue(!b, new BusinessException(ErrorEnum.SYSTEM_ERROR, "用户批量插入失败"));
        return true;
    }

    @Override
    public User getOneUser(User user) {
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
        return userMapper.insert(user) > 0 ? user.getUserId() : 0;
    }



    @Override
    public User updateOneUser(Long id, UserUpdateDto user) {
        ThrowUtil.throwIfTrue(id == null,
                ErrorEnum.PARAMS_ERROR, "路径请求参数为空");
        ThrowUtil.throwIfTrue(
                ObjUtil.isEmpty(user),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTrue(userMapper.exists(new LambdaQueryWrapper<User>()
                        .ne(User::getUserId, id)
                        .eq(User::getIsDeleted, 0)
                        .eq(User::getUsername, user.getUsername())),
                ErrorEnum.PARAMS_ERROR, "用户名已存在");
        //获取当前操作用户，若为管理员则需要修改editTime字段
        User optUser = userMapper.selectById(jwtUtil.getLoginUserInfo(JWTUtil.ELEMENT.USER_ID));
        //权限校验，若权限不足则无法修改用户角色、账号状态字段。
        User target = userMapper.selectById(id);
        if (UserRoleEnum.SYS_ADMIN.getValue().equals(optUser.getRole())) {
            target.setEditTime(LocalDate.now());
        } else if (UserRoleEnum.COMP_ADMIN.getValue().equals(optUser.getRole())) {
            // 企业管理员无法修改不属于本企业的用户数据
            ThrowUtil.throwIfTrue(!Objects.equals(target.getCompanyId(), optUser.getCompanyId()),
                    ErrorEnum.UNAUTHORIZED);
            // 企业管理员无法修改用户角色为系统管理员
            ThrowUtil.throwIfTrue(UserRoleEnum.SYS_ADMIN.equals(UserRoleEnum.getRole(target.getRole())),
                    ErrorEnum.UNAUTHORIZED);
            // 企业管理员无法修改其他管理员的信息
            ThrowUtil.throwIfTrue(UserRoleEnum.COMP_ADMIN.equals(UserRoleEnum.getRole(target.getRole()))
                            && !Objects.equals(target.getUserId(), optUser.getUserId())
                            || UserRoleEnum.SYS_ADMIN.equals(UserRoleEnum.getRole(target.getRole())),
                    ErrorEnum.UNAUTHORIZED);
            target.setEditTime(LocalDate.now());
        } else {
            ThrowUtil.throwIfTrue(!Objects.equals(target.getUserId(), optUser.getUserId()),
                    ErrorEnum.UNAUTHORIZED);
        }
        target.setProfile(user.getProfile());
        target.setUsername(user.getUsername());
        target.setUpdateTime(LocalDate.now());

        // 处理头像上传
        if (user.getAvatar() != null ) {
            try {
                // 校验文件类型
                String originalFilename = user.getAvatar().getOriginalFilename();
                if (originalFilename == null) {
                    throw new BusinessException(ErrorEnum.PARAMS_ERROR, "头像文件名不能为空");
                }
                // 允许的图片类型
                String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
                boolean isValidType = false;
                for (String type : allowedTypes) {
                    if (type.equals(fileExtension)) {
                        isValidType = true;
                        break;
                    }
                }
                if (!isValidType) {
                    throw new BusinessException(ErrorEnum.PARAMS_ERROR, "头像文件类型不支持，仅支持jpg、jpeg、png、gif、bmp、webp格式");
                }

                // 校验文件大小（5MB = 5 * 1024 * 1024 bytes）
                long maxSize = 5 * 1024 * 1024;
                if (user.getAvatar().getSize() > maxSize) {
                    throw new BusinessException(ErrorEnum.PARAMS_ERROR, "头像文件大小不能超过5MB");
                }

                // 上传文件
                String avatarUrl = aliOSSUtil.uploadFile(user.getAvatar(), id.toString());
                target.setAvatarUrl(avatarUrl);
            } catch (IOException e) {
                throw new BusinessException(ErrorEnum.SYSTEM_ERROR, "头像上传失败");
            }
        }
        userMapper.update(target, new LambdaUpdateWrapper<User>().eq(User::getUserId, id));
        return target;
    }

        @Override
        public int updateUserStatusByAdmin(Long id, Integer accountStatus) {
                ThrowUtil.throwIfTrue(id == null || id <= 0, ErrorEnum.PARAMS_ERROR, "用户ID错误");
                UserAccountStatusEnum statusEnum = UserAccountStatusEnum.getEnumByStatus(accountStatus);
                ThrowUtil.throwIfTrue(statusEnum == null, ErrorEnum.PARAMS_ERROR, "账号状态错误");

                User target = userMapper.selectById(id);
                ThrowUtil.throwIfTrue(target == null, ErrorEnum.NOT_FOUND_ERROR, "用户不存在");
                ThrowUtil.throwIfTrue(target.getIsDeleted() == 1, ErrorEnum.PARAMS_ERROR, "用户已注销");

                target.setAccountStatus(accountStatus);
                target.setUpdateTime(LocalDate.now());
                target.setEditTime(LocalDate.now());
                if (UserAccountStatusEnum.DEREGISTER.equals(statusEnum)) {
                        target.setIsDeleted(1);
                }
                return userMapper.updateById(target);
        }

        @Override
        public int deregisterSelf() {
                User cur = jwtUtil.parseLoginUser();
                ThrowUtil.throwIfTrue(cur == null || cur.getUserId() == null, ErrorEnum.NOT_LOGIN_ERROR, "未登录");
                User target = userMapper.selectById(cur.getUserId());
                ThrowUtil.throwIfTrue(target == null, ErrorEnum.NOT_FOUND_ERROR, "用户不存在");
                ThrowUtil.throwIfTrue(target.getIsDeleted() == 1 || UserAccountStatusEnum.DEREGISTER.getStatus().equals(target.getAccountStatus()),
                                ErrorEnum.PARAMS_ERROR, "账号已注销");

                if (Objects.equals(target.getRole(), UserRoleEnum.COMP_ADMIN.getValue()) && target.getCompanyId() != null) {
                        if (!userMapper.exists(new LambdaUpdateWrapper<User>()
                                                        .eq(User::getCompanyId, target.getCompanyId())
                                                        .ne(User::getUserId, target.getUserId())
                                                        .eq(User::getIsDeleted, 0)
                                                        .eq(User::getAccountStatus, UserAccountStatusEnum.NORMAL.getStatus()))
                                        && !Objects.equals(companyMapper.selectById(target.getCompanyId()).getStatus(), CompanyStatusEnum.DEREGISTER.getStatus())) {
                                throw new BusinessException(ErrorEnum.PARAMS_ERROR, "该公司不存在其他正常管理员，请先处理公司注销或管理员交接");
                        }
                }

                target.setAccountStatus(UserAccountStatusEnum.DEREGISTER.getStatus());
                target.setIsDeleted(1);
                target.setUpdateTime(LocalDate.now());
                return userMapper.updateById(target);
        }

    private void objectCheck(User user) {
        //region 1. 实体数据校验
        ThrowUtil.throwIfTrue(
                ObjUtil.isEmpty(user),
                new BusinessException(ErrorEnum.PARAMS_ERROR, "数据不能为空"));
        ThrowUtil.throwIfTrue(
                StrUtil.isBlankIfStr(user.getUsername()),
                ErrorEnum.PARAMS_ERROR,
                "用户名不能为空");
        ThrowUtil.throwIfTrue(
                user.getUsername().length() > 45,
                ErrorEnum.PARAMS_ERROR,
                "用户名过长");
        ThrowUtil.throwIfTrue(
                !user.getPassword().matches(PASSWORD_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR,
                "密码不符合规则");
        ThrowUtil.throwIfTrue(
                !PhoneUtil.isPhone(user.getPhone()),
                ErrorEnum.PARAMS_ERROR,
                "请输入正确的手机号");
        ThrowUtil.throwIfTrue(
                user.getEmail() != null && !user.getEmail().matches(EMAIL_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR,
                "请输入正确的邮箱");
        ThrowUtil.throwIfTrue(
                StrUtil.isBlankIfStr(user.getRole()),
                ErrorEnum.PARAMS_ERROR,
                "用户角色不能为空"
        );
        UserRoleEnum role = UserRoleEnum.getRole(user.getRole());
        //校验用户角色的同时，并设置规范使用UserRoleEnum的value值
        ThrowUtil.throwIfTrue(
                role == null,
                ErrorEnum.PARAMS_ERROR,
                "用户角色不存在");
        user.setRole(role.getValue());
        //endregion
        //region 2. 数据库字段校验
        ThrowUtil.throwIfTrue(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername())
                        .eq(User::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR, "用户名已存在");
        ThrowUtil.throwIfTrue(
                userMapper.exists(new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, user.getPhone())
                        .eq(User::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR, "手机号已注册");
        ThrowUtil.throwIfTrue(user.getEmail() != null &&
                        userMapper.exists(new LambdaQueryWrapper<User>()
                                .eq(User::getEmail, user.getEmail())
                                .eq(User::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR, "邮箱已注册");
        //endregion
    }
}