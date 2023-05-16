package com.hzy.webserver.enums;

/**
 * @title: CodeEnum
 * @Author zxwyhzy
 * @Date: 2023/4/28 14:54
 * @Version 1.0
 */
public enum CodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    LOGIN_SUCCESS(201, "操作成功"),
    // 失败
    ERROR(400,"操作失败");
    int code;
    String msg;

    CodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
