package com.maiya.web.model;

import com.maiya.common.enums.ReturnCodeEnum;

/**
 * 返回的结果对象
 * Created by zhanglb on 16/9/13.
 */
public class SimpleMessageResult {

    /**
     * 结果码
     */
    private String code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 结果数据
     */
    private Object data;

    public SimpleMessageResult(){}

    public SimpleMessageResult(ReturnCodeEnum returnCodeEnum){
        this.code = returnCodeEnum.getCode();
        this.message = returnCodeEnum.getMessage();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
