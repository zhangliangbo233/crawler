package com.maiya.crawling.service;

import java.io.IOException;

import com.maiya.common.dto.BaseResponse;


public interface JDService {
	
	/**
	 * 爬取京东信息
	 * @param map
	 * @throws Exception
	 */
	public  BaseResponse crawlingJD(String userIdentity, String userChannel,String city,Long taskId);
	
	/**
	 * 获取京东订单信息
	 * @param map
	 * @return
	 */
	public BaseResponse getJDOrders(String userIdentity, String userChannel)  throws IOException;
	
	
	/**
	 * 获取京东收货地址信息
	 * @param userIdentity
	 * @param userChannel
	 * @return
	 */
	public BaseResponse getJDAddressInfo(String userIdentity,String userChannel);

}
