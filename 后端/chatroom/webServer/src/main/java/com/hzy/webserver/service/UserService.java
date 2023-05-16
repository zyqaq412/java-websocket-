package com.hzy.webserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.webserver.domian.dto.ToEmail;
import com.hzy.webserver.domian.entity.User;
import com.hzy.webserver.domian.request.UserRegisterRequest;
import com.hzy.webserver.utils.Result;


/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-04-28 14:23:32
 */
public interface UserService extends IService<User> {

    /**
     *  用户注册
     * @param user 注册参数
     * @return 是否注册功能 或失败原因
     */
    Result userRegister(UserRegisterRequest user);

    /**
     *  判断邮箱是否注册
     * @return true|false
     */
    boolean isEmailNull(ToEmail toEmail);

    /**
     *  用户登录
     * @param user 用户数据
     * @return 返回用户信息(用户名与id)
     */
    Result login(UserRegisterRequest user);
}

