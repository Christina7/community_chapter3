package com.newcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int status;
    private int type;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
