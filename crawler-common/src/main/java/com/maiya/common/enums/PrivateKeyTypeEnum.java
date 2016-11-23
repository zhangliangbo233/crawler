package com.maiya.common.enums;

/**
 * 加密privatekey
 * @author xiangdf
 *
 */
public enum PrivateKeyTypeEnum {

	JD(21, "京东privatekey"),

	TAOBAO(20, "淘宝privatekey");

	private final int type;

	private final String desc;

	PrivateKeyTypeEnum(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}
}
