/**
 * 
 */
package com.li3huo.sdk.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 待签名订单
 * 
 * @author liyan
 *
 */
public class AgentOrder {
	/**
	 * SDK Agent添加的信息
	 */
	public String channelId; // 发行渠道编码
	public String appid; // 发行游戏编号
	/**
	 * 渠道SDK返回的用户登录token
	 */
	public JSONObject orderData;

	/**
	 * 状态码: 0 成功； -1 未实行
	 */
	public int code = -1;
	/** 错误信息 */
	public String msg;
	
	public static AgentOrder parse(String text) {
		return JSON.parseObject(text, AgentOrder.class);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
