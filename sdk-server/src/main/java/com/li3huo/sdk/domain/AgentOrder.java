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
public class AgentOrder extends Domain {
	/**
	 * 渠道SDK返回的用户登录token
	 */
	public JSONObject orderData;

	public static AgentOrder parse(String text) {
		return JSON.parseObject(text, AgentOrder.class);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
