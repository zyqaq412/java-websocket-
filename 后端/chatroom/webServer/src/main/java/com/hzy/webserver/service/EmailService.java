package com.hzy.webserver.service;


import com.hzy.webserver.domian.dto.ToEmail;
import com.hzy.webserver.utils.Result;
import org.springframework.stereotype.Service;

/**
 * @title: EmailService
 * @Author zxwyhzy
 * @Date: 2023/4/28 14:49
 * @Version 1.0
 */
@Service
public interface EmailService {
    /**
     *  发送验证码
     * @param toEmail 邮箱数据传输类
     * @return
     */
    Result commonEmail(ToEmail toEmail);
}
