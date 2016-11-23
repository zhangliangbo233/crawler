package com.maiya.common.enums;

/**
 * 授权状态
 * @author xiangdf
 *
 */
public enum AuthorStatusEnum {

	

	UNAUTHOR(0,"未授权"),
	
	SUCCESS(1,"授权成功"),
	
	FAIL(-1,"授权失败");
	
	
    /**
     * 状态码
     */
    private final int status;

    /**
     * 默认消息
     */
    private final String desc;
    
    

	AuthorStatusEnum(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public int getStatus() {
		return status;
	}

	public String getDesc() {
		return desc;
	}
	
}
