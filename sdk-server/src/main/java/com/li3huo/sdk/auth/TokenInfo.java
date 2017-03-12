/**
 * 
 */
package com.li3huo.sdk.auth;

import com.alibaba.fastjson.JSON;

/**
 * 渠道登录Token
 * 
 * @author liyan
 *
 */
public class TokenInfo {
	public String sign;
	public String timestamp;
	public String userId;
	/**
	 * 是否匹配
	 */
	public boolean match;

	public static TokenInfo parse(String text) {
		return JSON.parseObject(text, TokenInfo.class);
	}
	
	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
