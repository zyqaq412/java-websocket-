package com.hzy.webscoketserver.utils;


import com.hzy.webscoketserver.enums.CodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @title: Result
 * @Author zxwyhzy
 * @Date: 2023/4/28 14:54
 * @Version 1.0
 */
@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String id;
    private String msg;
    private T data;

    public Result() {

    }
    public static Result error(CodeEnum enums){
        Result result = new  Result();
        result.setCode(enums.getCode());
        result.setMsg(enums.getMsg());
        return result;
    }

    public static Result ok(Object data){
        Result result = new  Result();
        result.setData(data);
        result.setCode(CodeEnum.SUCCESS.getCode());
        result.setMsg(CodeEnum.SUCCESS.getMsg());
        return result;
    }
    public static Result set(CodeEnum enums,String id,Object data){
        Result result = new  Result();
        result.setCode(enums.getCode());
        result.setMsg(enums.getMsg());
        result.setData(data);
        result.setId(id);
        return result;
    }
}
