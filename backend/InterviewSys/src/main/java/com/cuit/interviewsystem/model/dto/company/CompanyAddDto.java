package com.cuit.interviewsystem.model.dto.company;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;


@Data
public class CompanyAddDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 8644221451512279800L;

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
     * 营业执照URL
     */
    private MultipartFile businessLicense;
}
