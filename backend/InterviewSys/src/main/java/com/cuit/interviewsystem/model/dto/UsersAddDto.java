package com.cuit.interviewsystem.model.dto;


import com.cuit.interviewsystem.model.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UsersAddDto implements Serializable {
    private static final long serialVersionUID = 2327969793254805384L;

    private int cnt;
    private List<User> list;
}
