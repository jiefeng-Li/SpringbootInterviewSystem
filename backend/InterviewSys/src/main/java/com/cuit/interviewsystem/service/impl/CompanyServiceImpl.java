package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.model.dto.CompanyAddDto;
import com.cuit.interviewsystem.model.dto.CompanyInfoDto;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.utils.AliOSSUtil;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
* @author jiefe
* @description 针对表【t_company(公司基本信息表)】的数据库操作Service实现
* @createDate 2026-01-28 20:13:34
*/
@Service
@Slf4j
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company>
    implements CompanyService{
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private AliOSSUtil aliOSSUtil;
    @Override
    public Company getCompanyById(Long id) {
        ThrowUtil.throwIfTrue(id <= 0, ErrorEnum.PARAMS_ERROR);
        return companyMapper.selectById(id);
    }

    /**
     * 所属公司员工数不为0时，也可以删除（注销）
     * @param id
     * @return
     */
    @Override
    public int deleteCompanyById(Long id) {
        ThrowUtil.throwIfTrue(id <= 0, ErrorEnum.PARAMS_ERROR);
        return companyMapper.deleteById(id);
    }

    /**
     * 非企业管理员无法注册公司，
     * 公司logo，营业执照仅支持png、jpg、jpeg格式的图片
     * 当前登录用户的id为该企业的管理员
     * @param cad
     * @return 注册成功后的公司id
     */
    @Override
    @Transactional
    public String addOneCompany(CompanyAddDto cad) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        ThrowUtil.throwIfTrue(jwtUtil.parse(token, JWTUtil.ELEMENT_COMPANY_ID) != null,
                ErrorEnum.PARAMS_ERROR.getCode(), "您已注册公司，无法再次注册");
        Company cmp = new Company();
        BeanUtils.copyProperties(cad, cmp);
        String logoUrl = null, licenseUrl = null;
        MultipartFile logoPicture = cad.getLogo();
        MultipartFile businessLicensePicture = cad.getBusinessLicense();

        //region 数据校验
        //当前登录用户的id为该企业的管理员
        cmp.setAdminId(Long.valueOf(jwtUtil.parse(token, JWTUtil.ELEMENT_USER_ID)));
        cmp.setBusinessLicenseUrl("TEMP");//设置临时值
        cmp.setStatus(CompanyStatusEnum.REVIEWING.getStatus());
        objectCheck(cmp);
        ThrowUtil.throwIfTrue(businessLicensePicture == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "营业执照不能为空");
        String licenseSuffix = businessLicensePicture.getOriginalFilename().substring(businessLicensePicture.getOriginalFilename().lastIndexOf("."));
        //判断是否图片格式
        ThrowUtil.throwIfTrue(!licenseSuffix.equals(".png") && !licenseSuffix.equals(".jpg") && !licenseSuffix.equals(".jpeg"),
                ErrorEnum.PARAMS_ERROR.getCode(), "图片格式不正确, 请上传png、jpg、jpeg格式的图片");
        //限制图片大小
        ThrowUtil.throwIfTrue(businessLicensePicture.getSize() > 1024 * 1024 * 5,
                ErrorEnum.PARAMS_ERROR.getCode(), "图片大小不能超过5MB");
        if (logoPicture != null) {
            //获取上传的logo、营业执照的文件后缀
            String logoSuffix = logoPicture.getOriginalFilename().substring(logoPicture.getOriginalFilename().lastIndexOf("."));
            //判断是否图片格式
            ThrowUtil.throwIfTrue(!logoSuffix.equals(".png") && !logoSuffix.equals(".jpg") && !logoSuffix.equals(".jpeg"),
                    ErrorEnum.PARAMS_ERROR.getCode(), "图片格式不正确, 请上传png、jpg、jpeg格式的图片");
            ThrowUtil.throwIfTrue(logoPicture.getSize() > 1024 * 1024 * 5,
                    ErrorEnum.PARAMS_ERROR.getCode(), "图片大小不能超过5MB");
        }
        //endregion
        //上传图片
        try {
            logoUrl = aliOSSUtil.uploadFile(logoPicture, Base64.encode(cad.getCompanyName().getBytes()));
            licenseUrl = aliOSSUtil.uploadFile(businessLicensePicture, Base64.encode(cad.getCompanyName().getBytes()));
        } catch (IOException e) {
            throw new BusinessException(ErrorEnum.SYSTEM_ERROR, "上传图片失败");
        }
        cmp.setLogoUrl(logoUrl);
        cmp.setBusinessLicenseUrl(licenseUrl);
        User compAdmin = new User();
        compAdmin.setUserId(cmp.getAdminId());
        int row = companyMapper.insert(cmp);
        if (row > 0) {
            compAdmin.setCompanyId(cmp.getCompanyId());
            userMapper.updateById(compAdmin);
        }
        //将公司id加入token
        return jwtUtil.sign(compAdmin);
    }

    @Override
    public int updateCompanyById(Long id, CompanyInfoDto cid) {
        Company cmp = new Company();
        BeanUtils.copyProperties(cid, cmp);
        ThrowUtil.throwIfTrue(id == null || id <= 0, ErrorEnum.PARAMS_ERROR);
        ThrowUtil.throwIfTrue(!id.equals(cid.getCompanyId()), ErrorEnum.PARAMS_ERROR);
        objectCheck(cmp);
        //获取当前角色
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        UserRoleEnum ure = UserRoleEnum.getRole(jwtUtil.parse(token, JWTUtil.ELEMENT_ROLE));
        //非系统管理员无法修改status
        if (cmp.getStatus() != null) {
            //用户不为系统管理员，且status字段被修改。抛出无权限异常
            ThrowUtil.throwIfTrue(!UserRoleEnum.SYS_ADMIN.equals(ure)
                    && companyMapper.exists(new LambdaQueryWrapper<Company>()
                    .eq(Company::getCompanyId, id)
                    .ne(Company::getStatus, cmp.getStatus())),
                    ErrorEnum.UNAUTHORIZED);
        }
        if (UserRoleEnum.SYS_ADMIN.equals(ure)) {
            cmp.setEditTime(new Date());
        }
        cmp.setUpdateTime(new Date());

        return companyMapper.updateById(cmp);
    }

    private void objectCheck(Company c) {
        //region 非空字段校验
        ThrowUtil.throwIfTrue(c == null, ErrorEnum.PARAMS_ERROR.getCode(), "公司信息不能为空");
        ThrowUtil.throwIfTrue(c.getCompanyName() == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司名称不能为空");
        ThrowUtil.throwIfTrue(c.getCompanyName().length() >= 100,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司名称过长");
        CompanyStatusEnum status = CompanyStatusEnum.getEnum(c.getStatus());
        ThrowUtil.throwIfTrue(status == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司状态不合法");
        ThrowUtil.throwIfTrue(c.getBusinessLicenseUrl() == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "营业执照不能为空");
        ThrowUtil.throwIfTrue(c.getAdminId() == null,
                ErrorEnum.PARAMS_ERROR.getCode(), "管理员id不能为空");
        ThrowUtil.throwIfTrue(!userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, c.getAdminId())),
                ErrorEnum.PARAMS_ERROR.getCode(), "管理员用户不存在");
        //endregion
        ThrowUtil.throwIfTrue(c.getIntroduction() != null && c.getIntroduction().length() >= 1024,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司简介过长");
        ThrowUtil.throwIfTrue(c.getIndustry() != null && c.getIndustry().length() >= 100,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司所属行业，数据过长");
        ThrowUtil.throwIfTrue(c.getScale() != null && c.getScale().length() >= 20,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司规模，数据过长");
        ThrowUtil.throwIfTrue(c.getCity() != null && c.getCity().length() >= 50,
                ErrorEnum.PARAMS_ERROR.getCode(), "公司所在城市，数据过长");
    }
}
