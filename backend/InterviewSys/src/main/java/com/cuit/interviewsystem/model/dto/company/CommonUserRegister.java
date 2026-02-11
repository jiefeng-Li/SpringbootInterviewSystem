package com.cuit.interviewsystem.model.dto.company;

import lombok.Data;

import java.util.Date;

@Data
public class CommonUserRegister {
    private String username;
    private String role;
    private String phone;
    private String password;
}
