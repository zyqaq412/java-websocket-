package com.hzy.webscoketserver.pojo;

import lombok.Data;

/**
 * @title: Message
 * @Author zxwyhzy
 * @Date: 2023/5/7 20:53
 * @Version 1.0
 */
@Data
public class Message {
    private long id ;
    private String sender ;
    private String time;
    private String text;
}
