package com.cuit.interviewsystem.controller;


import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.UserLoginDto;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.dto.UsersAddDto;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserAccountStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.UserVo;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/user")
//@Tag(name = "用户管理")
//TODO springboot4.0引入knife4j 似乎有版本冲突
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private JWTUtil jwtUtil;


//    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> sysAdminRegister(UserRegisterDto userRegisterDto) {
        long userId = userService.sysAdminRegister(userRegisterDto);
        return Result.success(userId);
    }
//    @Operation(summary = "用户登录")
    @GetMapping("/login")
    @AuthCheck
    public Result<String> login(UserLoginDto userLoginDto) {
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
        String jwt = jwtUtil.sign(user);
        ThrowUtil.throwIfTure(StrUtil.isBlankIfStr(jwt),
                ErrorEnum.SYSTEM_ERROR.getCode(), "JWT创建失败");
        return Result.success(jwt);
    }

//    @Operation(summary = "根据组合条件获取一个用户")
    @GetMapping
    public Result<UserVo> getOneUser(User conditions) {
        User res = userService.getOneUser(conditions);
        UserVo resVo = null;
        if (res != null) {
            resVo = new UserVo();
            BeanUtils.copyProperties(res, resVo);
            resVo.setPhone(DesensitizedUtil.mobilePhone(resVo.getPhone()));
            resVo.setEmail(DesensitizedUtil.email(resVo.getEmail()));
            resVo.setAccountStatus(Objects.requireNonNull(UserAccountStatusEnum.getEnumByStatus(res.getAccountStatus())).getText());
            resVo.setRole(Objects.requireNonNull(UserRoleEnum.getEnumByValue(res.getRole())).getText());
        }
        return Result.success(resVo);
    }

//    @Operation(summary = "批量添加用户 # 待重构")
    @PostMapping("/list")
    public Result addUsers(UsersAddDto usersAddDto) {
        //TODO 不太符合实际应用场景，待修改为使用excel添加
        userService.usersAdd(usersAddDto);
        return Result.success(null, "添加成功");
    }

//    @Operation(summary = "添加一个用户")
    @PostMapping("")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result<Long> addOneUser(User user) {
        long id = userService.addOneUser(user);
        if (id <= 0)
            return Result.error(ErrorEnum.SYSTEM_ERROR, "添加失败");
        return Result.success(null, "添加成功");
    }

//    @Operation(summary = "根据id(userId)删除一个用户")
    @DeleteMapping("/{id}")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN, UserRoleEnum.COMP_ADMIN})
    public Result deleteOneUser(@PathVariable Long id) {
        int i = userService.deleteOneUserById(id);
        if (i == 0)
            return Result.error(ErrorEnum.NOT_FOUND_ERROR.getCode(), "删除失败，用户不存在");
        return Result.success(i, "删除成功");
    }

//    @Operation(summary = "根据id(userId)更新一个用户")
    @PutMapping({"/{id}"})
    @AuthCheck()
    public Result updateOneUser(@PathVariable Long id, @RequestBody User user) {
        int cnt = userService.updateOneUser(id, user);
        if (cnt == 0)
            return Result.error(ErrorEnum.NOT_FOUND_ERROR.getCode(), "更新失败，用户不存在");
        return Result.success(null);
    }
}
