package com.cuit.interviewsystem.model.dto.company;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公司信息添加、修改dto
 */
@Data
public class CompanyInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3350086490852821341L;

    private Long companyId;
    private Long adminId;
    private String companyName;
    /**
     * Logo
     */
    private MultipartFile logo;
    private String website;
    private String introduction;
    private String industry;
    private String scale;
    private String city;
    /**
     * 状态(0待审,1正常,2驳回,3禁用)
     */
    private Integer status;

    /**
     * 营业执照URL
     */
    private MultipartFile businessLicense;
    private Integer isDeleted;
}
