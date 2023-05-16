package com.hzy.webserver.utils;


import com.hzy.webserver.enums.CodeEnum;
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
    private String msg;
    private T data;

    public Result() {

    }

    public static Result ResultOk(Object data){
        Result result = setCodeEnum(CodeEnum.SUCCESS);
        if(data!=null) {
            result.setData(data);
        }
        return result;
    }
    public static Result ResultOk(){
        Result result = setCodeEnum(CodeEnum.SUCCESS);;
        return result;
    }
    public static Result error(){
        Result result = setCodeEnum(CodeEnum.ERROR);;
        return result;
    }
    public static Result error(String msg){
        Result result = setCodeEnum(CodeEnum.ERROR);
        result.setMsg(msg);
        return result;
    }

    public static Result setCodeEnum(CodeEnum enums){
        Result result = new  Result();
        result.setCode(enums.getCode());
        result.setMsg(enums.getMsg());
        return result;
    }


    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
