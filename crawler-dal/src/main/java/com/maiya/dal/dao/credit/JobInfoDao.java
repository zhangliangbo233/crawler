package com.maiya.dal.dao.credit;

import com.maiya.dal.model.JobInfo;

public interface JobInfoDao {

	JobInfo getJobInfoByUserId(String userId);
	
}
