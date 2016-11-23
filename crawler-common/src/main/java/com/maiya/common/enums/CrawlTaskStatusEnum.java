package com.maiya.common.enums;

/**
 * @author zhanglb
 * @date 2016年9月27日
 * @description 抓取任务的状态
 */
public enum CrawlTaskStatusEnum {

    /**
     * 处理成功
     */
    FAIL(0, "失败"),

    SUCCESS(1, "成功"),

    PASSED(2, "超过重试次数");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 默认消息
     */
    private final String message;


    CrawlTaskStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }


}
