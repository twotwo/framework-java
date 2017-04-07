/**
 * 
 */
package com.li3huo.sdk.adapter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.domain.AgentToken;

/**
 * 根据游戏(game_id)，加载特定渠道(channel_name)的验证器
 * 
 * 每个游戏的每个渠道仅构造一个实例
 * 
 * @author liyan
 *
 */
public abstract class ValidatorFactory {
	private static final String prefix_class_name = "com.li3huo.sdk.adapter.Validator_";
	static final Logger logger = LogManager.getLogger(ValidatorFactory.class.getName());
	/** 渠道编码到适配器类的匹配 */
	static Properties adapters = new Properties();

	/** 所有游戏的适配器 */
	static Map<String, Map<String, Validator>> validators = new HashMap<String, Map<String, Validator>>();

	static {
		if (null == ValidatorFactory.class.getResource("adapters.properties")) {
			logger.fatal("add adapters.properties in factory folder!");
		}
		try {
			logger.debug("load class map from " + ValidatorFactory.class.getResource("adapters.properties").getPath());
			adapters.load(ValidatorFactory.class.getResourceAsStream("adapters.properties"));
		} catch (IOException e) {
			logger.fatal("failed to load class map!", e);
		}
	}

	/**
	 * 获取 <game_id>.channel.huaw<channel_name>.key的值
	 * 
	 * @param key
	 * @param defValue
	 * @return 例如 500006.channel.huawei.appid的值
	 */
	public static synchronized Validator getValidator(final String game, final String channel) {
		Map<String, Validator> g = validators.get(game);
		if (g == null) {
			g = new HashMap<String, Validator>();
			validators.put(game, g);
		}
		Validator v = g.get(channel);
		if (v == null) {
			v = getV(game, channel);
			g.put(channel, v);
		}

		return v;
	}

	private static Validator getV(final String game, final String channel) {

		Validator v = null;

		try {
			// xm.class
			String c_name = adapters.getProperty(channel.toLowerCase() + ".class", "");
			@SuppressWarnings("unchecked")
			Class<Validator> classType = (Class<Validator>) Class.forName(prefix_class_name + c_name);
			v = (Validator) ConstructorUtils.invokeConstructor(classType, game);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| InstantiationException e) {
			logger.error("getValidator(): " + e.getMessage());
		}

		if (null != v) {
			return v;
		}

		return new Validator(game, channel) {

			@Override
			public void check_token(AgentToken bean) {
				logger.fatal("game[" + game + "], channel[" + channel + "]: not impletent yet!");
			}
		};
	}

}
