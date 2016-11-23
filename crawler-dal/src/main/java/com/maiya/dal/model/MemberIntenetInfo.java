package com.maiya.dal.model;

public class MemberIntenetInfo {
	
	
	private String sGuid;
	
	private String sUserId;
	
	private String sTaoBao;
	
	private byte[] sTaoBaoPassword;
	
	private String sJingDong;
	
	private byte[] sJingDongPassword;
	
	private String sJDAuthorFlag;
	
	private String sTaoBaoAuthorFlag;
	
	

	public String getsUserId() {
		return sUserId;
	}

	public void setsUserId(String sUserId) {
		this.sUserId = sUserId;
	}

	public String getsTaoBao() {
		return sTaoBao;
	}

	public void setsTaoBao(String sTaoBao) {
		this.sTaoBao = sTaoBao;
	}



	public String getsJingDong() {
		return sJingDong;
	}

	public void setsJingDong(String sJingDong) {
		this.sJingDong = sJingDong;
	}


	public String getsJDAuthorFlag() {
		return sJDAuthorFlag;
	}

	public void setsJDAuthorFlag(String sJDAuthorFlag) {
		this.sJDAuthorFlag = sJDAuthorFlag;
	}

	public String getsTaoBaoAuthorFlag() {
		return sTaoBaoAuthorFlag;
	}

	public void setsTaoBaoAuthorFlag(String sTaoBaoAuthorFlag) {
		this.sTaoBaoAuthorFlag = sTaoBaoAuthorFlag;
	}

	public byte[] getsTaoBaoPassword() {
		return sTaoBaoPassword;
	}

	public void setsTaoBaoPassword(byte[] sTaoBaoPassword) {
		this.sTaoBaoPassword = sTaoBaoPassword;
	}

	public byte[] getsJingDongPassword() {
		return sJingDongPassword;
	}

	public void setsJingDongPassword(byte[] sJingDongPassword) {
		this.sJingDongPassword = sJingDongPassword;
	}

	public String getsGuid() {
		return sGuid;
	}

	public void setsGuid(String sGuid) {
		this.sGuid = sGuid;
	}
	

}
