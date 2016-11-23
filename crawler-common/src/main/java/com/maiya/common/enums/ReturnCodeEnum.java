package com.maiya.common.enums;

/**
 * @author zhanglb
 * @date 2016年8月29日
 * @description 返回结果状态集合
 */
public enum ReturnCodeEnum {

    /**
     * 处理成功
     */
    SUCCESS("1000", "处理成功"),

    FAIL("2000", "处理失败"),


    /**
     * 请求参数错误
     */
    INVALID_PARAMETERS("1005", "请求参数错误"),
	
    LOGIN_FAIL("1006","登录失败");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 默认消息
     */
    private final String message;


    ReturnCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }


}
