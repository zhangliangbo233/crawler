package com.maiya.crawling.dto;

import java.util.List;

public class AddressInfoDoc {

	
	private String username;

	private List<AddressInfo> addressInfo;


	public List<AddressInfo> getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(List<AddressInfo> addressInfo) {
		this.addressInfo = addressInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
