package com.maiya.common.exceptions;


import com.maiya.common.enums.ReturnCodeEnum;

/**
 * 业务异常类
 *
 * @author zhanglb
 * @date 2016年8月29日
 * @description
 */
public class CustomBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误信息
     */
    private String message;


    /**
     * 状态信息
     */
    private ReturnCodeEnum returnCodeEnum;

    /**
     * @param returnCodeEnum
     */
    public CustomBusinessException(ReturnCodeEnum returnCodeEnum) {
        super();
        this.returnCodeEnum = returnCodeEnum;
    }

    /**
     * @param returnCodeEnum
     */
    public CustomBusinessException(ReturnCodeEnum returnCodeEnum, String message) {
        this(returnCodeEnum);
        this.returnCodeEnum = returnCodeEnum;
        this.message = message;
    }

    /**
     * @param cause
     */
    public CustomBusinessException(ReturnCodeEnum returnCodeEnum, Throwable cause) {
        super(cause);
        this.returnCodeEnum = returnCodeEnum;
    }

    public ReturnCodeEnum getReturnCodeEnum() {
        return returnCodeEnum;
    }

    public void setReturnCodeEnum(ReturnCodeEnum returnCodeEnum) {
        this.returnCodeEnum = returnCodeEnum;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
