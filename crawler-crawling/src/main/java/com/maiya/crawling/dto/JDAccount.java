package com.maiya.crawling.dto;

public class JDAccount {

	
	private String userName;
	

	private String password;
	
	
	public JDAccount(){
		
	}
	
	public JDAccount(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
