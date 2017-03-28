/**
 * 
 */
package com.li3huo.sdk.domain;

import com.alibaba.fastjson.JSON;

/**
 * FL渠道支付通知信息
 * 
 * @author liyan
 *
 */
public class Pay_Notify_Feiliu {
	/** Channel Order ID*/
	public String florderid;
	
	/** Game Order ID*/
	public String cporderid;
	
	/** Game透传参数: SDK支付时传入，通知时带过来*/
	public String merpriv;
	
	/** Game ID*/
	public String appid;
	
	/** Game Group*/
	public String groupid;
	
	/** User ID*/
	public String userid;
	
	/** User Role ID*/
	public String roleid;

	/** Buy Item*/
	public String goodsid;
	
	/** Buy Amount*/
	public String amount;
	
	public String sign;
	
	public String status;
	
	/**
	 * 是否匹配
	 */
	public boolean match;
	
	public static Pay_Notify_Feiliu parse(String text) {
		return JSON.parseObject(text, Pay_Notify_Feiliu.class);
	}
	
	public String toJSONString() {
		return JSON.toJSONString(this);
	}
	
}
