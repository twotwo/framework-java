/**
 * 
 */
package com.li3huo.sdk;

/**
 * 渠道编码
 * 
 * @author liyan
 *
 */
public interface ChannelCode {
	// 飞流
	public static final String feiliu = "FL";
	// 奇虎360
	public static final String qihoo = "QH";
	// 百度
	public static final String baidu = "BD";
	// 当乐
	public static final String downjoy = "DJ";
	// 华为
	public static final String huawei = "HW";
	// 金立
	public static final String jinli = "JL";
	// 联想
	public static final String lenovo = "LX";
	// 魅族
	public static final String meizu = "MZ";
	// OPPO
	public static final String oppo = "OP";
	// UC阿里游戏
	public static final String uc = "UC";
	// Vivo
	public static final String vivo = "VV";
	// 小米
	public static final String xiaomi = "XM";

	/**
	 * Agent Side Error Code
	 * 
	 * Follow HTTP Status Code
	 */
	public static final int Error_Channel_Server = 500;
	
	public static final int Error_Bad_Request = 400;
	
	public static final int Error_Unauthorized = 401;
	
	public static final int Error_Token_Timeout = 440;
}
