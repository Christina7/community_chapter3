package com.newcoder.community.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

//登录以ticket为核心，服务端给登陆成功的用户一个ticket，上面有有效的时间
//ticket存在cookie中，服务端根据ticket来识别用户
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
