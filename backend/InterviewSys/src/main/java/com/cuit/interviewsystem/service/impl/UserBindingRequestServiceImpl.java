package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.model.dto.BindingRequestDto;
import com.cuit.interviewsystem.model.dto.BindingRequestPageDto;
import com.cuit.interviewsystem.model.entity.BindingRequest;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserBindingStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.BindingRequestVo;
import com.cuit.interviewsystem.service.UserBindingRequestService;
import com.cuit.interviewsystem.mapper.UserBindingRequestMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
* @author jiefe
* @description 针对表【t_binding_request(招聘者绑定公司申请记录表)】的数据库操作Service实现
* @createDate 2026-02-07 15:47:56
*/
@Service
public class UserBindingRequestServiceImpl extends ServiceImpl<UserBindingRequestMapper, BindingRequest>
    implements UserBindingRequestService {
    @Resource
    private UserBindingRequestMapper bindingMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private UserMapper userMapper;

    private static final int EXPIRE_OFFSET_DAYS = 7;

    @Override
    public String bindingCompany(BindingRequestDto bindingRequestDto) {
        BindingRequest br = new BindingRequest();
        BeanUtils.copyProperties(bindingRequestDto, br);
        User curUser = jwtUtil.parseLoginUser();
        ThrowUtil.throwIfTrue(curUser.getCompanyId() != null,
                ErrorEnum.PARAMS_ERROR, "公司已绑定");
        //只有未被删除、公司状态为正常（通过认证）的公司才能提交绑定
        Company target = companyMapper.selectById(br.getCompanyId());
        if (target == null || target.getIsDeleted() == 1)
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "公司不存在");
        if (!Objects.equals(target.getStatus(), CompanyStatusEnum.NORMAL.getStatus()))
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "公司未通过认证");
        //存在未被删除的、待审核的、未过期的，当前用户提交了同一个公司的申请
        ThrowUtil.throwIfTrue(bindingMapper.exists(new LambdaQueryWrapper<BindingRequest>()
                        .eq(BindingRequest::getUserId, curUser.getUserId())
                        .eq(BindingRequest::getCompanyId, br.getCompanyId())
                        .eq(BindingRequest::getIsDeleted, 0)
                        .eq(BindingRequest::getStatus, UserBindingStatusEnum.REVIEWING.getStatus())
                        .lt(BindingRequest::getExpiresAt, new Date())),
                ErrorEnum.PARAMS_ERROR, "已提交过申请");
        ThrowUtil.throwIfTrue(br.getApplicationNotes().length() > 1024,
                ErrorEnum.PARAMS_ERROR, "申请理由过长");

        br.setUserId(curUser.getUserId());
        br.setExpiresAt(DateUtil.offsetDay(new Date(), EXPIRE_OFFSET_DAYS));
        bindingMapper.insert(br);
        curUser.setCompanyId(br.getCompanyId());
        return jwtUtil.sign(curUser);
    }

    /**
     * 解绑公司
     */
    @Override
    public String unbindCompany(Long id) {
        ThrowUtil.throwIfTrue(id == null || id <= 0, ErrorEnum.PARAMS_ERROR);
        UserRoleEnum role = UserRoleEnum.getRole(jwtUtil.getLoginUserInfo(JWTUtil.ELEMENT.ROLE));
        User targetUser = userMapper.selectById(id);
        User curUser = jwtUtil.parseLoginUser();
        //若当前操作用户为公司管理员
        if (UserRoleEnum.COMP_ADMIN.equals(role)) {
            //当前登录的公司管理员所属的企业id
            Long curCompId = curUser.getCompanyId();
            //若公司管理员的公司id与被解绑的用户的公司id不同则无法解绑
            ThrowUtil.throwIfTrue(!Objects.equals(curCompId, targetUser.getCompanyId()), ErrorEnum.UNAUTHORIZED);
            //若被解绑的用户角色为系统管理员、其他同一个企业的公司管理员，则无法解绑
            ThrowUtil.throwIfTrue(UserRoleEnum.COMP_ADMIN.getValue().equals(targetUser.getRole()) || UserRoleEnum.SYS_ADMIN.getValue().equals(targetUser.getRole()),
                    ErrorEnum.UNAUTHORIZED);
        } else {
            //若当前操作用户为招聘者，则只能解绑自己
            ThrowUtil.throwIfTrue(!id.equals(curUser.getUserId()), ErrorEnum.PARAMS_ERROR, "无权限解绑");
        }
        int i = userMapper.update(new LambdaUpdateWrapper<User>()
                .set(User::getCompanyId, null)
                .eq(User::getUserId, id));
        curUser.setCompanyId(null);
        return i == 0 ? null : jwtUtil.sign(curUser);
    }

    @Override
    public Page<BindingRequestVo> getBindingList(BindingRequestPageDto pageDto) {
        Page<BindingRequestVo> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        bindingMapper.getBindingPage(page, pageDto);
        return page;
    }

    @Override
    public BindingRequest getRequestById(Long id) {
        return bindingMapper.selectById(id);
    }


}




