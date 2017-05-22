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
	// 安智
	public static final String anzhi = "AZ";
	// 飞流
	public static final String feiliu = "FL";
	// 奇虎360
	public static final String qihoo = "QH";

	/**
	 * Agent Side Error Code
	 * 
	 * Follow
	 * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">HTTP
	 * Status Code</a>
	 */

	// ===========================
	// 5xx Server error
	// ===========================
	/** 渠道服务器错误 500 */
	public static final int Error_Channel_Server = 500;

	/** 服务未实现(501 Not Implemented), 包含无法获取必要服务参数等 */
	public static final int Error_Service_Not_Implemented = 501;

	/** 503 Service Unavailable */
	public static final int Error_Channel_Unavailable = 503;

	/** 504 Gateway Timeout */
	public static final int Error_Channel_Timeout = 504;

	// ===========================
	// 4xx Client errors
	// ===========================
	/** 1、语义有误，当前请求无法被服务器理解 2、请求参数有误。 400 */
	public static final int Error_Bad_Request = 400;
	/** token验证逻辑走通，但验证状态返回失败 401 */
	public static final int Error_Unauthorized = 401;
	/** 资源未找到 404 */
	public static final int Error_NotFound = 404;
	/** Token失效(440 Login Time-out) */
	public static final int Error_Token_Timeout = 440;
}
