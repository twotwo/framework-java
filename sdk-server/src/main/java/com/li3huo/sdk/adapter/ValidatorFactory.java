/**
 * 
 */
package com.li3huo.sdk.adapter;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;
import com.li3huo.sdk.auth.AgentToken;

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

	/**
	 * 获取 <game_id>.channel.huaw<channel_name>.key的值
	 * 
	 * @param key
	 * @param defValue
	 * @return 例如 500006.channel.huawei.appid的值
	 */
	public static Validator getValidator(final String game, final String channel) {
		
		Validator v = null;
		
		try {
			//500006.channel.xiaomi.class
			String c_name = App.getProperty(channel.toLowerCase()+".class","");
			@SuppressWarnings("unchecked")
			Class<Validator> classType = (Class<Validator>) Class.forName(prefix_class_name+c_name);
			v = (Validator) ConstructorUtils.invokeConstructor(classType, game);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| InstantiationException e) {
			logger.error("getValidator(): "+ e.getMessage());
		}
		
		if(null != v) {
			return v;
		}
		
		return new Validator() {
			
			@Override
			public void check_token(AgentToken bean) {
				logger.error("game[" + game + "], channel[" + channel + "]: not impletent yet!");
			}
		};
	}

	public abstract void check_token(AgentToken bean);
}
