/**
 * 
 */
package com.li3huo.sdk.adapter;

import com.li3huo.sdk.App;
import com.li3huo.sdk.auth.AgentToken;

/**
 * @author liyan
 *
 */
public abstract class Validator {
	String game_id;
	String channel_name;

	/**
	 * 获取 <game_id>.channel.huaw<channel_name>.key的值
	 * 
	 * @param key
	 * @param defValue
	 * @return 例如 500006.channel.huawei.appid的值
	 */
	String getProperty(String key, String defValue) {
		return App.getProperty(game_id + ".channel." + channel_name + "." + key, defValue);
	}

	public abstract void check_token(AgentToken bean);
}
