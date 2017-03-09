/**
 * 
 */
package com.li3huo.sdk.auth;

import com.alibaba.fastjson.JSON;

/**
 * @author liyan
 *
 */
public class LoginBean {
	public String sign;
	public String timestamp;
	public String userId;
	/**
	 * 是否匹配
	 */
	public boolean match;

	public static LoginBean parse(String text) {
		return JSON.parseObject(text, LoginBean.class);
	}
	
	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
