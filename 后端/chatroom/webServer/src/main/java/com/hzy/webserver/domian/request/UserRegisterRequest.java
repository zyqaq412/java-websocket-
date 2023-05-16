package com.hzy.webserver.domian.request;

import lombok.Data;

/**
 *  接收前端注册参数
 * @title: UserRegisterRequest
 * @Author zxwyhzy
 * @Date: 2023/4/28 15:43
 * @Version 1.0
 */
@Data
public class UserRegisterRequest {
    // 用户名
    private String userAccount;
    // 密码
    private String userPassword;
    // 确认密码
    private String checkPassword;
    // 验证码
    private String code;
    // 邮箱
    private String email;
}
