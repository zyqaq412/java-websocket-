package com.hzy.webserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.webserver.domian.dto.ToEmail;
import com.hzy.webserver.domian.entity.User;
import com.hzy.webserver.domian.entity.vo.UserVo;
import com.hzy.webserver.domian.request.UserRegisterRequest;
import com.hzy.webserver.mapper.UserMapper;
import com.hzy.webserver.service.UserService;
import com.hzy.webserver.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-04-28 14:23:33
 */
@Service()
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final String SALT = "zy";
    @Override
    public Result userRegister(UserRegisterRequest user) {
        // 虽然前端也会判断，但是有绕过前端直接发送请求的可能
        if (StringUtils.isAnyBlank(user.getUserAccount(),
                user.getUserPassword(),
                user.getCheckPassword())){
            return Result.error("用户名或密码为空");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUseraccount,user.getUserAccount());
        long count = count(queryWrapper);
        if (count > 0 ) return Result.error("用户名以存在");
        if (user.getUserPassword().equals(user.getCheckPassword())){
            User register = new User();
            register.setUseraccount(user.getUserAccount());
            register.setEmail(user.getEmail());
            // 加密
            String password = DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes());
            register.setUserpassword(password);
            boolean save = save(register);
            if (!save){
                return Result.error("注册失败");
            }
            return Result.ResultOk("注册成功");
        }
        return Result.error("两次输入的密码不一致");
    }

    @Override
    public boolean isEmailNull(ToEmail toEmail) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,toEmail.getTo());
        long count = count(queryWrapper);
        if (count <1 )
            return true;
        return false;
    }

    @Override
    public Result login(UserRegisterRequest user) {
        if(StringUtils.isAnyBlank(user.getUserAccount(),user.getUserPassword())){
            return Result.error("用户名或密码为空");
        }
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + user.getUserPassword())
                .getBytes())) ;

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUseraccount,user.getUserAccount());
        queryWrapper.eq(User::getUserpassword,user.getUserPassword());
        User flag = getOne(queryWrapper);
        if (null == flag) {
            return Result.error("密码错误");
        }

        UserVo userVo = new UserVo();
        userVo.setUsername(flag.getUseraccount());
        userVo.setId(flag.getId());
        return Result.ResultOk(userVo);
    }


}
