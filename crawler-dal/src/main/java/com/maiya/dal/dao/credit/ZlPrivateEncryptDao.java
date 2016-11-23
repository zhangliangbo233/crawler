package com.maiya.dal.dao.credit;

import com.maiya.dal.model.ZlPrivateEncrypt;

public interface ZlPrivateEncryptDao {


	ZlPrivateEncrypt queryZlPrivateEncryptByUserId(String userId, int infoType);
	
	
	void insert(ZlPrivateEncrypt obj);
	
}
