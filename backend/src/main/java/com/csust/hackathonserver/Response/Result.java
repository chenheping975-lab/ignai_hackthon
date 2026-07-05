package com.csust.hackathonserver.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误码，成功时为 null
     */
    private String errorCode;

    /**
     * 返回提示信息
     */
    private String message;

    /**
     * 返回数据，可以放对象、集合、字符串、数字等
     */
    private Object data;

    public static Result ok() {
        return new Result(true, null, "ok", null);
    }

    public static Result ok(Object data) {
        return new Result(true, null, "ok", data);
    }

    public static Result ok(String message, Object data) {
        return new Result(true, null, message, data);
    }

    public static Result fail(String errorCode, String message) {
        return new Result(false, errorCode, message, null);
    }

    public static Result fail(String message) {
        return new Result(false, "COMMON_ERROR", message, null);
    }
}