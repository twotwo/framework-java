/**
 * 
 */
package com.li3huo.sdk.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.ChannelCode;
import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.domain.Voucher;
import com.li3huo.sdk.tools.RSAUtil;
import com.li3huo.service.FacadeContext;

/**
 * @author liyan
 *
 */
public class Validator_Feiliu extends Validator {

	static final Logger logger = LogManager.getLogger(Validator_Feiliu.class.getName());

	/**
	 * 根据游戏标识去加载对应渠道的参数
	 * 
	 * @param game_id
	 *            对接游戏标识
	 */
	public Validator_Feiliu(String game_id) {
		super(game_id, ChannelCode.feiliu);
		logger.debug("[" + super.channel_name + "] appsecret = " + getProperty("appsecret", "unknown"));
	}

	@Override
	public void check_token(AgentToken token) {
		JSONObject data = token.channelData;
		logger.debug("channelData:" + data);
		// 原始串
		String sourceStr = data.getString("timestamp") + "&" + data.getString("userId");
		// 加密串: bean.sign
		// CheckUtil.class.getResourceAsStream(name);
		// 解密方法: decodeBase64 -> rsa pk decode -> to utf-8 string
		// conf/<game_id>/fl_key.pub
		InputStream input = Validator_Feiliu.class.getResourceAsStream("/" + token.appid + "/fl_key.pub");
		try {
			// RSAPublicKey pubKey =
			// RSAUtil.parsePublicKey(IOUtils.toString(input, "ISO8859-1"));
			logger.debug("pubkey = " + getProperty("pubkey", ""));
			RSAPublicKey pubKey = RSAUtil.parsePublicKey(super.getProperty("pubkey", ""));
			byte[] bytes = RSAUtil.dec(Base64.decodeBase64(data.getString("sign")), pubKey);
			String decodeStr = StringUtils.toEncodedString(bytes, Charset.forName("UTF-8"));
			logger.debug("check_feiliu_sign(): decodeStr = " + decodeStr);
			// token is valid!
			if (sourceStr.equals(decodeStr)) {
				token.userId = data.getString("userId");
				token.code = 0;
			}
			if (null != input) {
				input.close();
			}

		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.fatal("check_feiliu_sign()", e);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final String FAILURE = "{\"status\": \"0\",\"msg\": \"failure\"}";
	private final String SUCCESS = "{\"status\": \"0\",\"msg\": \"success\"}";

	/**
	 * 处理支付通知
	 * 
	 * @param bean
	 */
	@Override
	public void check_pay_notify(Voucher voucher, FacadeContext ctx) {
		String data = StringUtils.toEncodedString(ctx.getInputStreamArray(), Charset.forName("UTF-8"));
		JSONObject bean = JSON.parseObject(data);
		String sign = bean.getString("sign");
		logger.debug("check_channel_sign(): signStr = " + sign);
		if (StringUtils.isEmpty(bean.getString("sign"))) {
			logger.error("check_channel_sign(): sign is empty!");
		}

		/* 对渠道通知参数内容进行排序 */
		Map<String, String> sortedParams = new TreeMap<String, String>();
		for (Entry<String, Object> p : bean.entrySet()) {
			String key = p.getKey();
			String value = bean.getString(key);
			/* 剔除sign字段，并拼接成验证格式字符串 */
			if (StringUtils.isBlank(value)) {
				continue;
			}
			sortedParams.put(key, value);
		}
		/* 剔除sign字段 */
		sortedParams.remove("sign");

		/* 拼接成验证格式字符串 */
		String src = super.linkString(sortedParams) + getProperty("appsecret", "unknown");

		logger.debug("check_pay_notify(): src = " + src);
		String destStr = DigestUtils.md5Hex(src);
		logger.debug("check_pay_notify(): dest = " + destStr);

		if (sign.equals(destStr)) {
			logger.debug("check_pay_notify(): sign is valid.");

			voucher.orderid = bean.getString("cpOrderId");
			voucher.channel_order_id = bean.getString("flOrderId");
			voucher.userId = bean.getString("userId");
			voucher.roleId = bean.getString("roleId");
			if (StringUtils.isNotBlank(voucher.orderid) && StringUtils.isNotBlank(voucher.channel_order_id)
					&& StringUtils.isNotBlank(voucher.userId) && StringUtils.isNotBlank(voucher.roleId)) {
				voucher.pay_status = true; // 飞流只通知支付成功的订单

				voucher.code = 0;
				/** write voucher file */
				voucher.saveToFile(voucher_dir, logger);
				voucher.response_to_channel = SUCCESS;
				return;
			} else {
				logger.error("check_pay_notify(): has empty item! voucher = " + voucher.toJSONString());
				voucher.code = ChannelCode.Error_Bad_Request;
				voucher.msg = "notify not valid, has empty item!";
			}

		} else {
			logger.error("check_pay_notify(): sign is not valid! should be = " + destStr);
			voucher.code = ChannelCode.Error_Unauthorized;
			voucher.msg = "sign is not valid!";
		}
		voucher.response_to_channel = FAILURE;
	}
}
