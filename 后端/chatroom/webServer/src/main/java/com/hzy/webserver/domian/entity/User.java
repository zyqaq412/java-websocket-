package com.hzy.webserver.domian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2023-04-28 14:23:28
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    // 用户id@TableId
    @TableId(value = "id" ,type = IdType.AUTO)
    private Long id;
    // 昵称
    private String username;
    // 登录账号
    private String useraccount;
    // 头像
    private String avatarurl;
    // 性别
    private Integer gender;
    // 密码
    private String userpassword;
    // 电话
    private String phone;
    // 邮箱
    private String email;
    // 用户状态：0正常
    private Integer userstatus;
    // 创建时间，数据插入时间
    private Date createtime;
    // 更新时间，数据更新时间
    private Date updatetime;
    // 是否删除：0未删除
    private Integer isdelete;
    // 0普通用户：1管理员
    private Integer role;
}
