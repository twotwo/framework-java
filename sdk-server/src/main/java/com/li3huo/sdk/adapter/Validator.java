/**
 * 
 */
package com.li3huo.sdk.adapter;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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

	/**
	 * 按照 key=value&key=value生成String, 对value不做任何转换
	 * 
	 * @param keyMap
	 * @return
	 */
	String linkString(Map<String, String> keyMap) {
		if (keyMap == null || keyMap.size() == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (String key : keyMap.keySet()) {
			sb.append(key).append("=");
			sb.append(keyMap.get(key)).append("&");
		}

		return StringUtils.removeEnd(sb.toString(), "&");
	}

	public abstract void check_token(AgentToken bean);
}
