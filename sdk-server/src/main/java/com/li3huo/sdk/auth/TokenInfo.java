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
	/** Channel Name*/
	public String channel_name;
	/** Game ID*/
	public String game_id;
	
	/** User ID*/
	public String user_id;
	

	/**
	 * 是否匹配
	 */
	public boolean match;
	
	/**
	 * 凭证认证结果
	 */
	public boolean certified;

	public static TokenInfo parse(String text) {
		return JSON.parseObject(text, TokenInfo.class);
	}
	
	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
