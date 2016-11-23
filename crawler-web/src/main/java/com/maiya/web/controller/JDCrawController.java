package com.maiya.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.maiya.common.dto.BaseResponse;
import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.crawling.service.JDService;

@RestController
@RequestMapping("/crawling")
public class JDCrawController {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDCrawController.class);

	@Autowired
	private JDService service;

	@RequestMapping(value = "/crawlingJD")
	public BaseResponse crawlingJD(String userIdentity, String userChannel, String city) throws Exception {
		LOGGER.info("抓取京东数据开始,userIdentity:{},userChannel:{},city:{}", userIdentity, userChannel, city);
		BaseResponse response = new BaseResponse();
		try {
			response = this.service.crawlingJD(userIdentity, userChannel, city, null);
		} catch (Exception e) {
			LOGGER.error("抓取信息异常", e);
			response.setRetcode(ReturnCodeEnum.FAIL.getCode());
			response.setRetinfo(ReturnCodeEnum.FAIL.getMessage());
		}
		LOGGER.info("抓取京东数据结束");
		return response;
	}

	@RequestMapping(value = "/queryJDOrders")
	public BaseResponse queryJDOrders(String userIdentity, String userChannel) {

		LOGGER.info("根据userIdentity获取京东交易记录开始,userIdentity:{}", userIdentity);

		BaseResponse response = new BaseResponse();

		try {
			response = this.service.getJDOrders(userIdentity, userChannel);
		} catch (Exception e) {
			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.FAIL.getMessage());
		}
		LOGGER.info("根据userIdentity获取京东交易记录结束 ,userIdentity:{}", userIdentity);
		return response;
	}

	@RequestMapping(value = "/queryJDAddress")
	@ResponseBody
	public BaseResponse queryJDAddressInfo(String userIdentity, String userChannel) {

		LOGGER.info("根据userIdentity获取京东收货地址信息开始,userIdentity:{}", userIdentity);

		BaseResponse response = new BaseResponse();

		try {
			response = service.getJDAddressInfo(userIdentity, userChannel);

		} catch (Exception e) {
			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.FAIL.getMessage());
		}

		LOGGER.info("response:{}", response.toString());

		LOGGER.info("根据userIdentity获取京东收货地址信息结束,userIdentity:{}", userIdentity);
		return response;
	}

}
