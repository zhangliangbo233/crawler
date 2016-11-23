package com.maiya.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 隐位工具类
 * @author xiangdefei
 *
 */
public class MaskUtil {

	/**
	 * 手机号码正则
	 */
	public static final String REGEX_MOBILE_NO = "^1[0-9]{10}$";

	/**
	 * 匹配电子邮件的正则表达式
	 */
	public static final String REGEX_EMAIL = "^[\\w-]+[-.\\w]*@[-\\w]+\\.[-.\\w]*[\\w-]+$";

	
	   /**
     * 邮箱隐位 <br/>
     * 邮箱隐藏规则:<br/>
     * a.邮箱账号只有一位，账号形式如7@qq.com 隐位之后为77***7@<br/>
     * b.两位以及以上的保留前两位以及最后一位中间用***填充 如89@q.com 隐位之后为89***9@qq.com<br/>
     * 手机号码掩位规则：<br>
     * c：手机号码保留前三位以及后两位中间以******填充,如15845845845隐位后变为158******45
     * 
     * @param email
     * @return
     * @see
     * @since
     */
	public static String maskUserName(String userName) {

		if (isEmail(userName)) {
			int index = userName.indexOf("@");
			String emailAccount = userName.substring(0, index);
			int accountLength = emailAccount.length();
			String accountPrefix = "";
			String accountSuffix = emailAccount.substring(accountLength - 1);
			if (accountLength < 2) {
				accountPrefix = emailAccount + emailAccount;
			} else {
				accountPrefix = emailAccount.substring(0, 2);
			}
			return String.format("%s%s%s%s", accountPrefix, "***", accountSuffix, userName.substring(index));
		} else {

			if (isMobilePhoneNo(userName)) {

				String mobilePrefix = userName.substring(0, 3);
				String mobileSuffix = userName.substring(11 - 4);
				return String.format("%s%s%s", mobilePrefix, "****", mobileSuffix);
			}
		}
		return userName;

	}

	/**
	 * 功能描述:验证是否位邮箱 <br>
	 * 〈验证邮箱的有效性 成功匹配则返回true， 不匹配则返回false〉
	 * 
	 * @param str
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private static boolean isEmail(String str) {
		return match(REGEX_EMAIL, str);
	}

	/**
	 * 功能描述: 验证是否是手机号 <br>
	 * 匹配规则：只需为11位的纯数字即可
	 * 
	 * @param str
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private static boolean isMobilePhoneNo(String value) {
		return match(REGEX_MOBILE_NO, value);
	}

	/**
	 * 功能描述:正则表达式验证方法<br>
	 * 〈正则表达式验证方法 匹配表达式则返回true〉
	 * 
	 * @param regex
	 * @param str
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private static boolean match(String regex, String str) {
		// 为空一定不匹配
		if (StringUtils.isAnyEmpty(regex, str)) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	
	public static void main(String[] args) {
		
		
		System.out.println(maskUserName("12312313122"));
	}

}
