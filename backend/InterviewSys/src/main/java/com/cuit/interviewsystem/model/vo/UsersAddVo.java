package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class UsersAddVo implements Serializable {
    private static final long serialVersionUID = 1361137190404086053L;

    private int cnt;
    private List<UserAdd> list;
    private class UserAdd {
        private Long userId;
        private String username;
        private String role;
        private String email;
        private Integer accountStatus;
        private Long companyId;

        public UserAdd(Long userId, String username, String role, String email, Integer accountStatus, Long companyId) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.email = email;
            this.accountStatus = accountStatus;
            this.companyId = companyId;
        }
        public UserAdd(){}
    }
}
