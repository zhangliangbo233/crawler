package com.maiya.common.constants;

public class HbaseConstants {

	
	public  final static String HBASE_TABLE_JD="my_crawler_jd";
	
	
	public final static String HBASE_TABLE_TAOBAO="my_crawler_taobao";
	
	
	public final static String HBASE_FAMILY="userinfo";
	
	
	public final static String HBASE_QUALIFIERS_INFOMESSAGE="infomessage";
	
	
	public final static String HBASE_QUALIFIERS_ANALYSISMESSAGE="analysismessage";
	
	
	/**
	 * 收货地址信息表
	 */
	public final static String HBASE_TABLE_ADDRESS="my_crawler_address";
	
	/**
	 * 解析后的收货地址信息列族
	 */
	public final static String HBASE_FAMILY_ADDRESS="addressInfo";
	/**
	 * 解析后的京东收货地址信息列
	 */
	public final static String HBASE_QUALIFIERS_JD_ADDRESS="jd";
	
	/**
	 * 解析后的淘宝收货地址信息列
	 */
	public final static String HBASE_QUALIFIERS_TAOBAO_ADDRESS="taobao";
	
	
	/**
	 * 原始的京东收货地址信息列
	 */
	public final static String HBASE_QUALIFIERS_JD_ORIGINAL_ADDRESS="jd_origina";
	
	/**
	 * 原始的淘宝收货地址信息列
	 */
	public final static String HBASE_QUALIFIERS_TAOBAO_ORIGINAL_ADDRESS="taobao_original";
	
	
	

}
