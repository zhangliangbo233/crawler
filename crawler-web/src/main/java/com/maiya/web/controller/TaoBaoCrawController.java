package com.maiya.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maiya.common.dto.BaseResponse;
import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.crawling.service.TAOBAOService;

@RestController
@RequestMapping("/crawling")
public class TaoBaoCrawController {

	public static final Logger LOGGER = LoggerFactory.getLogger(TaoBaoCrawController.class);

	@Autowired
	private TAOBAOService service;

	@RequestMapping(value = "/crawlingTaoBao")
	public BaseResponse crawlingTaoBao(String userIdentity, String userChannel, String city) throws Exception {
		LOGGER.info("抓取淘宝数据开始,userIdentity:{},userChannel:{},city:{}", userIdentity, userChannel, city);
		BaseResponse response = this.service.crawlingTaoBao(userIdentity, userChannel, city, null);
		LOGGER.info("抓取淘宝数据结束");
		return response;

	}

	@RequestMapping(value = "/queryTaoBaoOrders")
	public BaseResponse queryTaoBaoOrders(String userIdentity, String userChannel) {

		LOGGER.info("根据userIdentity获取淘宝交易记录开始,userIdentity:{}", userIdentity);

		BaseResponse response = new BaseResponse();

		try {

			response = this.service.queryTaoBaoOrders(userIdentity, userChannel);

		} catch (Exception e) {
			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.FAIL.getMessage());
		}
		LOGGER.info("根据userIdentity获取淘宝交易记录结束,userIdentity:{}", userIdentity);
		return response;
	}

	@RequestMapping(value = "/queryTaoBaoAddress")
	public BaseResponse queryTaoBaoAddressInfo(String userIdentity, String userChannel) {

		LOGGER.info("根据userIdentity获取淘宝收货地址信息开始,userIdentity:{}", userIdentity);

		BaseResponse response = new BaseResponse();

		try {
			response = this.service.queryTaoBaoAddressInfo(userIdentity, userChannel);

		} catch (Exception e) {

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.FAIL.getMessage());

		}

		LOGGER.info("response:{}", response.toString());
		LOGGER.info("根据userIdentity获取淘宝收货地址信息结束,userIdentity:{}", userIdentity);
		return response;
	}

}
