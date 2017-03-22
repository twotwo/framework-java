/**
 * 
 */
package com.li3huo.sdk.auth;

import com.alibaba.fastjson.JSON;

/**
 * Agent发给Game的已经支付凭据
 * 
 * @author liyan
 *
 */
public class Voucher {
	/** Channel Name*/
	public String channel_name;
	
	/** User ID*/
	public String user_id;
	
	/** Channel Order ID*/
	public String channel_order_id;
	
	/** Game ID*/
	public String game_id;
	
	/** Game Order ID*/
	public String game_order_id;
	
	/** Order Price*/
	public String total_price;
	
	/**
	 * 凭证认证结果
	 */
	public boolean certified;
	
	/**
	 * 给渠道的响应
	 */
	public String response;
	
	public static Voucher parse(String text) {
		return JSON.parseObject(text, Voucher.class);
	}
	
	public String toJSONString() {
		return JSON.toJSONString(this);
	}
	
}
