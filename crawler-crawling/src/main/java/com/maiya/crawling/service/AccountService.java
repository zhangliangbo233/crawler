package com.maiya.crawling.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maiya.common.enums.PrivateKeyTypeEnum;
import com.maiya.common.util.EncrypAES;
import com.maiya.crawling.constants.CrawlingConstants;
import com.maiya.crawling.dto.JDAccount;
import com.maiya.crawling.dto.TaoBaoAccount;
import com.maiya.dal.dao.credit.MemberIntenetInfoDao;
import com.maiya.dal.dao.credit.ZlPrivateEncryptDao;
import com.maiya.dal.model.MemberIntenetInfo;
import com.maiya.dal.model.ZlPrivateEncrypt;

@Service
public class AccountService {

	public static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private MemberIntenetInfoDao memberIntenetInfoDao;

	@Autowired
	private ZlPrivateEncryptDao zlPrivateEncryptDao;

	/**
	 * 获取京东账户名和登录密码信息
	 * 
	 * @param userIdentity
	 * @param userChannel
	 * @return
	 */
	public JDAccount getJDAccount(String userIdentity, String userChannel) {

		MemberIntenetInfo memberIntenetInfo = null;

		ZlPrivateEncrypt zlPrivateEncrypt = null;

		JDAccount jdAccount = null;

		if (StringUtils.equals(userChannel, CrawlingConstants.CHANNEL_MY)) {

			try {

				memberIntenetInfo = memberIntenetInfoDao.queryMemberIntenetInfoByUserId(userIdentity);

				if (memberIntenetInfo == null) {

					LOGGER.info("没有查询到用户信息,userIdentity:{}", userIdentity);

					return null;
				}

				zlPrivateEncrypt = zlPrivateEncryptDao.queryZlPrivateEncryptByUserId(userIdentity,
						PrivateKeyTypeEnum.JD.getType());

				if (zlPrivateEncrypt == null) {

					LOGGER.info("没有查询到用户京东密码私钥,userIdentity:{}", userIdentity);

					return null;
				}

				try {

					String password = new String(EncrypAES.decrypt(memberIntenetInfo.getsJingDongPassword(),
							zlPrivateEncrypt.getsPrivateKey()));
					if(StringUtils.isEmpty(password)){
						LOGGER.warn("密码解密失败");
						return null;
					}
					jdAccount = new JDAccount(memberIntenetInfo.getsJingDong(),
							password.substring(0, password.length() - 5));
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
						| NoSuchAlgorithmException | NoSuchPaddingException e) {
					LOGGER.error("京东密码解密出现异常,exception:{}", e);
					return null;
				}
				return jdAccount;

			} catch (Exception e) {

				LOGGER.error("查询京东账户信息出现异常,exception:{}", e);
			}

		} else if (StringUtils.equals(userChannel, CrawlingConstants.CHANNEL_PH)) {

		}

		return null;
	}

	/**
	 * 获取淘宝账户名和登录密码信息
	 * 
	 * @param userIdentity
	 * @param userChannel
	 * @return
	 */
	public TaoBaoAccount getTaoBaoAccount(String userIdentity, String userChannel) {

		MemberIntenetInfo memberIntenetInfo = null;

		ZlPrivateEncrypt zlPrivateEncrypt = null;

		TaoBaoAccount taobaoAccount = null;

		if (StringUtils.equals(userChannel, CrawlingConstants.CHANNEL_MY)) {

			try {

				memberIntenetInfo = memberIntenetInfoDao.queryMemberIntenetInfoByUserId(userIdentity);

				if (memberIntenetInfo == null) {

					LOGGER.info("没有查询到用户信息,userIdentity:{}", userIdentity);

					return null;
				}

				zlPrivateEncrypt = zlPrivateEncryptDao.queryZlPrivateEncryptByUserId(userIdentity,
						PrivateKeyTypeEnum.TAOBAO.getType());

				if (zlPrivateEncrypt == null) {

					LOGGER.info("没有查询到用户淘宝密码私钥,userIdentity:{}", userIdentity);

					return null;
				}

				try {
					String password = new String(EncrypAES.decrypt(memberIntenetInfo.getsTaoBaoPassword(),
							zlPrivateEncrypt.getsPrivateKey()));
					if(StringUtils.isEmpty(password)){
						LOGGER.warn("密码解密失败");
						return null;
					}
					taobaoAccount = new TaoBaoAccount(memberIntenetInfo.getsTaoBao(),
							password.substring(0, password.length() - 5));
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
						| NoSuchAlgorithmException | NoSuchPaddingException e) {
					LOGGER.error("淘宝密码解密出现异常,exception:{}", e);
					return null;
				}

				return taobaoAccount;

			} catch (Exception e) {

				LOGGER.error("查询淘宝账户信息出现异常,exception:{}", e);
			}

		} else if (StringUtils.equals(userChannel, CrawlingConstants.CHANNEL_PH)) {

		}

		return null;
	}
}
