/**
 * AgentKey.java create at Mar 25, 2017 11:18:25 AM
 */
package com.li3huo.sdk.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * SDK Agent获取支付密钥的请求对象
 * 
 * @author liyan
 *
 */
public class AgentKey extends Domain {
	/**
	 * 与SDK Agent进行交互的JSON对象，本质上是一个Map对象
	 */
	public JSONObject keys;

	public static AgentKey parse(String text) {
		return JSON.parseObject(text, AgentKey.class);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
