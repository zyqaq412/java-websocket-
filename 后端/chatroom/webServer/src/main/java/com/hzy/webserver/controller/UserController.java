package com.hzy.webserver.controller;


import com.hzy.webserver.domian.request.UserRegisterRequest;
import com.hzy.webserver.service.UserService;
import com.hzy.webserver.utils.RedisCache;
import com.hzy.webserver.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: UserController
 * @Author zxwyhzy
 * @Date: 2023/4/28 14:35
 * @Version 1.0
 */
@Slf4j
@RestController // 适用于编写restful风格的api，返回值默认为json类型
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result userRegister(@RequestBody UserRegisterRequest user){
        if (null == user) return Result.error("参数不能为空");
        String yzm = redisCache.getCacheObject(user.getEmail());
        if (user.getCode()!=null && user.getCode().equals(yzm)){
            return userService.userRegister(user);
        }
        return Result.error("验证码错误");
    }

    @PostMapping("/login")
    public Result userLogin(@RequestBody UserRegisterRequest user){
        log.info("登录请求");
        if (null == user) return Result.error("参数不能为空");
        return userService.login(user);
    }
}
