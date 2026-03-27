package com.cuit.interviewsystem.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -4888883528833092866L;

    /**
     * 用户名
     */
    @NotBlank
    @Length(max = 50, message = "用户名过长")
    private String username;

    /**
     * 头像URL
     */
    private MultipartFile avatar;

    /**
     * 个人简介
     */
    @Length(max = 1000, message = "个人简介过长")
    private String profile;
}
