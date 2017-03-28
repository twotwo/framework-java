/**
 * 
 */
package com.li3huo.sdk.domain;

/**
 * 待签名订单
 * 
 * @author liyan
 *
 */
public abstract class Domain {
	/**
	 * SDK Agent添加的信息
	 */
	public String channelId; // 发行渠道编码
	public String appid; // 发行游戏编号

	/**
	 * Agent Server添加信息
	 */
	public int code = -1; // 状态码: 0 成功； -1 未实现； 其它值 失败错误码
	/** 错误信息 */
	public String msg;
}
