package com.cuit.interviewsystem.controller;


import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjUtil;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.CommonUserRegister;
import com.cuit.interviewsystem.model.dto.UserLoginDto;
import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.model.dto.UsersAddDto;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserAccountStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.UserVo;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 管理员用户注册
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/admin/register")
    public Result sysAdminRegister(UserRegisterDto userRegisterDto) {
        long userId = userService.sysAdminRegister(userRegisterDto);
        record userRecord(long userId){}
        return Result.success(new userRecord(userId));
    }

    /**
     * 公司用户注册
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/comp/register")
    public Result<Long> compUserRegister(UserRegisterDto userRegisterDto) {
        long userId = userService.compUserRegister(userRegisterDto);
        return Result.success(userId);
    }

    /**
     * 普通用户(求职者)注册
     * @param  cur
     * @return
     */
    //TODO 待重构为 JOB_SEEKER 与 RECRUITER 的通用注册
    @PostMapping("/register")
    public Result<Long> commonUserRegister(CommonUserRegister cur) {
        long userId = userService.commonUserRegister(cur);
        return Result.success(userId);
    }


//    @Operation(summary = "用户登录")

    /**
     * 通用用户登录
     * @param userLoginDto
     * @return
     */
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
        return Result.success(jwt);
    }


    /**
     *
     * @return 用户角色类型
     */
    @GetMapping("/roles")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result getUserRoles() {
        record roles(String text, String value){}
        List<roles> res = new ArrayList<>();
        for (UserRoleEnum e : UserRoleEnum.values())
            res.add(new roles(e.getText(), e.getValue()));
        return Result.success(res);
    }

    @GetMapping("/status")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN, UserRoleEnum.COMP_ADMIN})
    public Result getUserStatus() {
        record statusRecord(int status, String text){}
        List<statusRecord> res = new ArrayList<>();
        for (UserAccountStatusEnum e : UserAccountStatusEnum.values())
            res.add(new statusRecord(e.getStatus(), e.getText()));
        return Result.success(res);
    }

//    @Operation(summary = "根据组合条件获取一个用户")

    /**
     * 通用 获取单个用户
     * @param conditions
     * @return
     */
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

    /**
     * 批量添加用户
     * @param usersAddDto
     * @return
     */
    @PostMapping("/list")
    public Result addUsers(UsersAddDto usersAddDto) {
        //TODO 不太符合实际应用场景，待修改为使用excel添加
        userService.usersAdd(usersAddDto);
        return Result.success(null, "添加成功");
    }

//    @Operation(summary = "添加一个用户")

    /**
     * 管理员添加单个用户
     * @param user
     * @return
     */
    @PostMapping("")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result<Long> addOneUser(User user) {
        long id = userService.addOneUser(user);
        if (id <= 0)
            return Result.error(ErrorEnum.SYSTEM_ERROR, "添加失败");
        return Result.success(null, "添加成功");
    }

//    @Operation(summary = "根据id(userId)删除一个用户")

    /**
     * (系统、公司)管理员删除单个用户
     * @param id
     * @return
     */
    @DeleteMapping("/admin/{id}")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN, UserRoleEnum.COMP_ADMIN,
            UserRoleEnum.RECRUITER, UserRoleEnum.JOB_SEEKER})
    public Result deleteOneUser(@PathVariable Long id) {
        int i = userService.deleteOneUserById(id);
        if (i == 0)
            return Result.error(ErrorEnum.NOT_FOUND_ERROR.getCode(), "删除失败，用户不存在");
        return Result.success(i, "删除成功");
    }

    /**
     * 通用用户注销账号
     * @param id
     * @return
     */

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
