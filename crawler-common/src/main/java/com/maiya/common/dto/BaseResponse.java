package com.maiya.common.dto;

import com.maiya.common.enums.ReturnCodeEnum;

public class BaseResponse {

	
	 private String retcode;
	 
	 private String retinfo;
	 
	 private Object doc;
	 
	 
	 public BaseResponse(){}
	 
	 public BaseResponse(ReturnCodeEnum returnCodeEnum){
		  this.retcode=returnCodeEnum.getCode();
		  this.retinfo=returnCodeEnum.getMessage();
		  
	 }

	public String getRetcode() {
		return retcode;
	}

	public void setRetcode(String retcode) {
		this.retcode = retcode;
	}

	public String getRetinfo() {
		return retinfo;
	}

	public void setRetinfo(String retinfo) {
		this.retinfo = retinfo;
	}


	public String toString(){
		return "retcode:"+this.retcode+",retinfo:"+this.retinfo;
		
	}

	public Object getDoc() {
		return doc;
	}

	public void setDoc(Object doc) {
		this.doc = doc;
	}


}
