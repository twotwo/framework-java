package com.li3huo.sdk.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * SDK Agent 封装的渠道返回的登录用户Token
 * 
 * @author liyan
 *
 */
public class AgentToken {
	/**
	 * SDK Agent添加的信息
	 */
	public String channelId; //发行渠道编码
	public String appid; //发行游戏编号
	/**
	 * 渠道SDK返回的用户登录token
	 */
	public JSONObject channelData;

	/** 从渠道token中解析出来的用户ID */
	public String userId;
	
	/**
	 * 状态码: 0 成功； -1 未实行
	 */
	public int code = -1;
	/** 错误信息 */
	public String msg;
	
	/**
	 * 凭证认证结果
	 */
	public boolean certified;

	public static AgentToken parse(String text) {
		return JSON.parseObject(text, AgentToken.class);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
