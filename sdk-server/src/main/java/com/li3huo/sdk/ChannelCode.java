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
	//飞流
	public static final String feiliu = "FL";
	//奇虎360
	public static final String qihoo = "360";
	//百度
	public static final String baidu = "BD";
	//UC阿里游戏
	public static final String uc = "UC";
	//华为
	public static final String huawei = "HW";
	//Vivo
	public static final String vivo = "VV";
	//小米
	public static final String xiaomi = "XM";
	//魅族
	public static final String meizu = "MZ";
	//联想
	public static final String lenovo = "LX";
	
	/**
	 * Follow HTTP Status Code
	 */
	public static final int Error_Channel_Server = 500;
	public static final int Error_Token_Timeout = 440;
}
