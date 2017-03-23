/**
 * 
 */
package com.li3huo.sdk.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.sdk.App;

/**
 * @author liyan
 *
 */
public class RSAUtilTest {

	static final Logger logger = LogManager.getLogger(RSAUtilTest.class.getName());

	static RSAPublicKey pubKey;
	static RSAPrivateKey priKey;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		StopWatch sw = new StopWatch();
		String file = "conf/games.properties";
		try {
			App.initConfig(file);
		} catch (Exception e1) {
			logger.error("setUpBeforeClass() App.initConfig failed.");
		}
		logger.info("Generate key first!");
		logger.error("ssh-keygen -t rsa -b 4096 -C \"admin@li3huo.com\" -f /tmp/key -q -N \"\"");
		// change ssh-rsa to pem type
		logger.error("ssh-keygen -e -m pem -f /tmp/key > /tmp/key.pub");

		try {
			sw.start();
			// create key pair if not exist
			if (!new File("/tmp/key").exists()) {
				logger.debug("create key pair...");
				RSAUtil.generateKeyPair("/tmp/key");
			}

			pubKey = RSAUtil.loadPublicKey("/tmp/key.pub");
			priKey = RSAUtil.loadPrivateKey("/tmp/key");
		} catch (NoSuchAlgorithmException e) {
			logger.fatal("setUpBeforeClass(): Failed to get RSA instance.", e);
		} catch (InvalidKeySpecException e) {
			logger.fatal("setUpBeforeClass(): Invalid Key Spec.", e);
		} catch (IOException e) {
			logger.fatal("setUpBeforeClass(): Failed to load file content.", e);
		} finally {
			logger.debug("setUpBeforeClass(): [" + sw + "]");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void pubKeyEnc2priKeyDec() {
		String txt = "eat your own dog food!\n宁愿做过了后悔,也不要错过了后悔！";
		StopWatch sw = new StopWatch();

		try {
			sw.start();
			String ciphertxt = Base64.encodeBase64String(RSAUtil.enc(txt, pubKey));
			logger.debug("pubKeyEnc2priKeyDec(): [" + sw + "]ciphertxt = " + ciphertxt);

			byte[] bytes = RSAUtil.dec(Base64.decodeBase64(ciphertxt), priKey);
			logger.debug("pubKeyEnc2priKeyDec(): [" + sw + "]txt2 = "
					+ StringUtils.toEncodedString(bytes, Charset.forName("UTF-8")));

			Assert.assertEquals("should be equal. ", txt, StringUtils.toEncodedString(bytes, Charset.forName("UTF-8")));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			logger.fatal("pubKeyEnc2priKeyDec()", e);
		} finally {
			logger.debug("pubKeyEnc2priKeyDec(): [" + sw + "]");
		}
	}

	@Test
	public void loadKeyFromResource() {
		logger.debug("loadKeyFromResource() key.pub = " + getClass().getResource("/test/key.pub").getPath());
		try {
			RSAPublicKey pubKey = RSAUtil.loadPublicKey(getClass().getResource("/test/key.pub").getPath());
			Assert.assertNotNull("can load pub key", pubKey);

		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.fatal("loadKeyFromResource() " + e.getMessage(), e);
		}
	}

	@Test
	public void loadKeyFromProperties() {
		String privateKey = App.getProperty("500006.channel.lenovo.appsecret", "");
		logger.debug("loadKeyFromProperties() privateKey = " + privateKey);
		PrivateKey priKey = null;
		try {
			priKey = RSAUtil.parsePrivateKey(privateKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(priKey);
	}

	@Test
	public void test_sign_and_verify() {
		String src = "108253961490250993465260086000050576790";
		String dsa = "SHA256WithRSA";
		try {
			String priKey = App.getProperty("500006.channel.huawei.pay.pri", "");
			PrivateKey privateKey = RSAUtil.parsePrivateKey(priKey);
//			String sign = RSAUtil.sign(src.getBytes("utf-8"), privateKey, dsa);
			String sign = RSAUtil.sign(DigestUtils.sha256(src), privateKey, dsa);
			logger.debug("sign = " + sign);

			String pubKey = App.getProperty("500006.channel.huawei.pay.pub", "");
			PublicKey publicKey = RSAUtil.parsePublicKey(pubKey);
			boolean v = RSAUtil.verify(DigestUtils.sha256(src), sign, publicKey, dsa);
			logger.debug("verify = " + v);
			Assert.assertTrue(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_huawei_sign() {
		String src = "108253961490250993465260086000050576790";
		String gameAuthSign = "Tvb2jy5h8FrBZXSM1rjsp1nZ9qDGHETKXxzI2wDuV9enHBQTS6hpNCDvjnGa4qlfxq3mdbu4/ug2uNT6kKppu4HRfWcCqiOo8UgYiQyAhBiI2mEeiMotw0QLBHQpRN//idEYO1LgviNHPCpcOutMrhxv+ra/bBlNOclCNYIZTgUEokuLNeMosI5NSqnp6p8mBsAqnCT0cTawaz/hJvy4KfAeON1at9SEc/fxzcsEoGeAO7jYK06wcrH3BYpkRCWf5ryi9aS/B71L0dFau+NhmtNpFI2pH8kI+dOgiRBkMkDgZSt4NRsv76HklE15EWn+oMLaUYNvmiZmDC5XtlO9HA==";
		// String key = App.getProperty("500006.channel.huawei.pay.pub", "");
		// MLGBZD！ 这个公钥是在文档中给出来的！5.1 登录鉴权签名的验签公钥(重要)
		String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmKLBMs2vXosqSR2rojMzioTRVt8oc1ox2uKjyZt6bHUK0u+OpantyFYwF3w1d0U3mCF6rGUnEADzXiX/2/RgLQDEXRD22er31ep3yevtL/r0qcO8GMDzy3RJexdLB6z20voNM551yhKhB18qyFesiPhcPKBQM5dnAOdZLSaLYHzQkQKANy9fYFJlLDo11I3AxefCBuoG+g7ilti5qgpbkm6rK2lLGWOeJMrF+Hu+cxd 9H2y3cXWXxkwWM1OZZTgTq3Frlsv1fgkrByJotDpRe8SwkiVuRycR0AHsFfIsuZCFwZML16EGnHqm2jLJXMKIBgkZTzL8Z+201RmOheV4AQIDAQAB";
		logger.debug("test_huawei_sign() publicKey = " + key);

		try {
			PublicKey pubKey = RSAUtil.parsePublicKey(key);
			logger.debug("test_huawei_sign() gameAuthSign.length() = " + gameAuthSign.length());
			logger.debug("test_huawei_sign() decodeBase64 length =  " + Base64.decodeBase64(gameAuthSign).length);
			boolean v = RSAUtil.verify(src.getBytes("utf-8"), gameAuthSign, pubKey, "SHA256WithRSA");
			logger.debug("test_huawei_sign() verify = " + v);
			logger.debug("test_huawei_sign() gameAuthSign = " + gameAuthSign);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
