package com.cuit.interviewsystem.controller;


import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.company.CommonUserRegister;
import com.cuit.interviewsystem.model.dto.user.*;
import com.cuit.interviewsystem.model.entity.BindingRequest;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.UserAccountStatusEnum;
import com.cuit.interviewsystem.model.enums.UserBindingStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.BindingRequestVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.model.vo.UserVo;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.service.UserBindingRequestService;
import com.cuit.interviewsystem.service.UserService;
import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    private CompanyService companyService;
    @Resource
    private UserBindingRequestService bindingService;
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
        if (userId <= 0)
            return Result.error(ErrorEnum.SYSTEM_ERROR, "用户注册失败");
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
            resVo.setCompanyName(companyService.getCompanyById(res.getCompanyId()).getCompanyName());
        }
        return Result.success(resVo);
    }

    @GetMapping("/list")
//    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN, UserRoleEnum.COMP_ADMIN})
    public Result<PageVo<UserVo>> getUserPage(UserPageDto conditions) {
        Page<User> users = userService.getUsers(conditions);
        List<User> records = users.getRecords();
        List<UserVo> res = new ArrayList<>();
        for (User u : records) {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(u, userVo);
            userVo.setPhone(DesensitizedUtil.mobilePhone(userVo.getPhone()));
            userVo.setEmail(DesensitizedUtil.email(userVo.getEmail()));
            userVo.setAccountStatus(UserAccountStatusEnum.getEnumByStatus(u.getAccountStatus()).getText());
            userVo.setRole(UserRoleEnum.getEnumByValue(u.getRole()).getText());
            res.add(userVo);
        }
        PageVo<UserVo> pageVo = new PageVo<>();
        pageVo.setPageSize(users.getSize());
        pageVo.setPages(users.getPages());
        pageVo.setPageNum(users.getCurrent());
        pageVo.setTotal(users.getTotal());
        pageVo.setList(res);
        return Result.success(pageVo);
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
        return Result.success();
    }

    //region  招聘者绑定公司
    @GetMapping("/binding/status")
    public Result<?> getBindingStatus() {
        record bindingStatusRecord(Integer status, String text) {}
        List<bindingStatusRecord> res = new ArrayList<>();
        for (UserBindingStatusEnum e : UserBindingStatusEnum.values()) {
            res.add(new bindingStatusRecord(e.getStatus(), e.getText()));
        }
        return Result.success(res);
    }

    @GetMapping("/binding")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<BindingRequestVo> getBindingInfo(Long id) {
        BindingRequest request = bindingService.getRequestById(id);
        User requestUser = userService.getById(request.getUserId());
        User reviewAdmin = userService.getById(request.getReviewedBy());
        Company cmp = companyService.getCompanyById(request.getCompanyId());
        BindingRequestVo res = new BindingRequestVo();
        BeanUtils.copyProperties(request, res);
        res.setUsername(requestUser.getUsername());
        res.setUserPhone(DesensitizedUtil.mobilePhone(requestUser.getPhone()));
        res.setReviewedByName(reviewAdmin.getUsername());
        res.setCompanyName(cmp.getCompanyName());
        return Result.success(res);
    }

    @PostMapping("/binding")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> addBindingCompany(BindingRequestDto bindingRequestDto) {
        String token = bindingService.bindingCompany(bindingRequestDto);
        return Result.success(token, "提交成功");
    }

    @PutMapping("/binding/{id}")
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN})
    public Result<?> reviewBindingRequest(@PathVariable Long id, @Valid ReviewBindingRequestDto dto) {
        int i = bindingService.reviewBindingRequest(id, dto);
        return i == 0 ? Result.error(ErrorEnum.SYSTEM_ERROR, "审核失败") : Result.success(null, "审核成功");
    }

    @DeleteMapping("/binding/cancel/{id}")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> cancelBindingCompany(@PathVariable Long id) {
        int i = bindingService.cancelBinding(id);
        return i == 0 ? Result.error(ErrorEnum.SYSTEM_ERROR, "取消失败") : Result.success(null, "取消成功");
    }

    @PutMapping("/unbind/{id}")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER, UserRoleEnum.COMP_ADMIN})
    public Result<?> unbindCompany(@PathVariable Long id) {
        String newToken = bindingService.unbindCompany(id);
        return newToken == null ? Result.error(ErrorEnum.SYSTEM_ERROR, "解绑失败") : Result.success(newToken, "解绑成功");
    }

    @GetMapping("/binding/list")
//    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN})
    public Result<PageVo<?>> getBindingList(@Valid BindingRequestPageDto pageDto) {
        Page<BindingRequestVo> page = bindingService.getBindingList(pageDto);
        PageVo<BindingRequestVo> res = PageVo.of(page);
        //手机号脱敏
        res.getList().forEach(vo ->
            vo.setUserPhone(DesensitizedUtil.mobilePhone(vo.getUserPhone()))
        );
        return Result.success(res);
    }
    //endregion
}
