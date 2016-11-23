package com.maiya.dal.model;

public class ZlPrivateEncrypt {
	
	private String sGuid;
	
	private String sUserId;
	
	private int sInfoType;
	
	private byte[] sPrivateKey;
	
	private int iDelFlag;
	
	

	public String getsUserId() {
		return sUserId;
	}

	public void setsUserId(String sUserId) {
		this.sUserId = sUserId;
	}


	public byte[] getsPrivateKey() {
		return sPrivateKey;
	}

	public void setsPrivateKey(byte[] sPrivateKey) {
		this.sPrivateKey = sPrivateKey;
	}

	public String getsGuid() {
		return sGuid;
	}

	public void setsGuid(String sGuid) {
		this.sGuid = sGuid;
	}

	public int getiDelFlag() {
		return iDelFlag;
	}

	public void setiDelFlag(int iDelFlag) {
		this.iDelFlag = iDelFlag;
	}

	public int getsInfoType() {
		return sInfoType;
	}

	public void setsInfoType(int sInfoType) {
		this.sInfoType = sInfoType;
	}


}
