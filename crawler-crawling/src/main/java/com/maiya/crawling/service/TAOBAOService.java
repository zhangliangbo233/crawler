package com.maiya.crawling.service;

import com.maiya.common.dto.BaseResponse;

/**
 * 
 * @author xiangdf
 *
 */
public interface TAOBAOService {
	
	/**
	 * 爬取淘宝订单信息
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public BaseResponse crawlingTaoBao(String userIdentity,String userChannel,String city,Long taskId);
	
	
	/**
	 * 查询淘宝订单信息
	 * @param userIdentity
	 * @param userChannel
	 * @return
	 * @throws Exception
	 */
	public BaseResponse queryTaoBaoOrders(String userIdentity,String userChannel) throws Exception;
	
	/**
	 * 查询淘宝收货地址信息
	 * @param userIdentity
	 * @param userChannel
	 * @return
	 * @throws Exception
	 */
	public BaseResponse queryTaoBaoAddressInfo(String userIdentity,String userChannel) ;

}
